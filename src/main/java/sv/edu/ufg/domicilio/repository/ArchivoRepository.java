package sv.edu.ufg.domicilio.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import util.ArchivoUtil;
import util.ModificacionUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class ArchivoRepository {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.bucket.app-folder}")
    private String appFolder;
    private final AmazonS3 s3;
    private final Logger logger = LoggerFactory.getLogger(ArchivoRepository.class);
    private  ArchivoUtil archivoUtil;
    private  ModificacionUtil modificacionUtil;


    @PostConstruct
    public void inicializar(){
        archivoUtil = new ArchivoUtil();
        modificacionUtil = new ModificacionUtil();
    }

    public String subir(MultipartFile multipartFile, String carpetaDestino, String nombreAnterior){

        return subir(multipartFile, carpetaDestino, nombreAnterior, archivoUtil.obtenerNuevoNombreArchivo(multipartFile));

    }

    private String subir(MultipartFile multipartFile, String carpetaDestino, String nombreAnterior, String nombreArchivoNuevo) {
        boolean existeArchivoAnterior = !modificacionUtil.textoEsNuloOEnBlanco((nombreAnterior));
        boolean existeArchivoNuevo = false;

        // Extraer metadata
        Map<String, String> atributosMetadata = archivoUtil.extraerMetadata(multipartFile);
        ObjectMetadata metadata = new ObjectMetadata();
        atributosMetadata.forEach(metadata::addUserMetadata);
        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivoNuevo);
            s3.putObject(bucketName,llave,multipartFile.getInputStream(),metadata);
            existeArchivoNuevo = true;
            if (existeArchivoAnterior){
                borrar(carpetaDestino,nombreAnterior);
            }
            return nombreArchivoNuevo;
        }catch (AmazonServiceException | IOException e){
            logger.error(e.getClass().getCanonicalName()+": "+ e.getMessage());
        }

        if (existeArchivoNuevo){
            return nombreArchivoNuevo;
        }else{
            return nombreAnterior;
        }
    }

    public void borrar(String carpetaDestino, String nombreArchivo) {
        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivo);
            s3.deleteObject(bucketName,llave);

        }catch (AmazonServiceException e){
            String mensajeError = String.format("No se pudo borrar el archivo %s", nombreArchivo);
            logger.error(mensajeError, e);
            throw new UncheckedIOException(new IOException(mensajeError));
        }
    }

    private String obtenerLlaveBucket(String carpetaDestino, String nombreArchivoNuevo) {
        return String.format("%s/%s/%s",appFolder, carpetaDestino, nombreArchivoNuevo);
    }

    public byte[] descargar(String carpetaDestino, String nombreArchivo){
        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivo);
            S3Object s3Object = s3.getObject(bucketName,llave);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }catch (AmazonServiceException | IOException e){
            String mensajeError = String.format("No se pudo descargar el archivo %s", nombreArchivo);
            logger.error(mensajeError, e);
            return new byte[0];
        }
    }
}
