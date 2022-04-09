package util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.entity.ContentType.*;

@Component
@RequiredArgsConstructor
public class ArchivoUtil {
    public String obtenerNuevoNombreArchivo(MultipartFile archivo){
        String nombreOriginal = archivo.getOriginalFilename();
        String extensionArchivo = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")+1);
        String nombreArchivo = String.format("%s.%s", UUID.randomUUID(), extensionArchivo);
        return nombreArchivo;
    }

    public Map<String,String> extraerMetadata(MultipartFile archivo){
        Map<String,String> metadata = new HashMap<>();
        metadata.put("Content-Type",archivo.getContentType());
        metadata.put("Content-Length",String.valueOf(archivo.getContentType()));
        return metadata;
    }

    public void esImagen(MultipartFile archivo){
        String tipoContenido = archivo.getContentType();
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(tipoContenido)){
            throw new IllegalArgumentException(String.format("El archivo debe ser una imagen [%s]",tipoContenido));
        }
    }

    public void esArchivoVacio(MultipartFile archivo){
        if (archivo.isEmpty()){
            throw new IllegalArgumentException(String.format("No se puede subir un archivo vacio [%s]",archivo.getSize()));
        }
    }
}
