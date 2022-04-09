package sv.edu.ufg.domicilio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sv.edu.ufg.domicilio.model.Categoria;
import sv.edu.ufg.domicilio.repository.ArchivoRepository;
import sv.edu.ufg.domicilio.repository.CategoriaRepository;
import sv.edu.ufg.domicilio.request.CategoriaRequest;
import util.ArchivoUtil;
import util.ModificacionUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ArchivoRepository archivoRepository;
    private final String CARPETA = "categoria";
    private  ArchivoUtil archivoUtil;
    private  ModificacionUtil modificacionUtil;

    @PostConstruct
    public void inicializar(){
        archivoUtil = new ArchivoUtil();
        modificacionUtil = new ModificacionUtil();
    }

    public Page<Categoria> obtenerCategorias(Pageable pageable){
        return categoriaRepository.findAll(pageable);
    }

    public Categoria obtenerCategoriaPorId(Integer idCategoria){
        Categoria categoria = encontrarCategoriaPorId(idCategoria);
        return categoria;
    }

    private Categoria encontrarCategoriaPorId(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElseThrow(
                () -> new EntityNotFoundException(String.format("La categoria no existe", idCategoria)));
    }

    public Categoria crearCategoria(CategoriaRequest categoriaRequest){


        validateExistingCategory(categoriaRequest);

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(categoriaRequest.getNombre());

        Categoria categoria = categoriaRepository.save(nuevaCategoria);
        return categoria;
    }

    private void validateExistingCategory(CategoriaRequest categoriaRequest) {
        Optional<Categoria> categoriaHomonima = categoriaRepository.findByNombre(categoriaRequest.getNombre());
        if (categoriaHomonima.isPresent())
            throw new EntityExistsException(String.format("La categoria %s ya existe", categoriaRequest.getNombre()));
    }

    public Categoria modificarCategoria(Integer idCategoria, CategoriaRequest categoriaRequest){
    Categoria categoria =  this.obtenerCategoriaPorId(idCategoria);
        validateExistingCategory(categoriaRequest);
        if (!categoria.getNombre().equals(categoriaRequest.getNombre()) && categoriaRequest.getNombre().length() > 0){
            validateExistingCategory(categoriaRequest);
            categoria.setNombre(categoriaRequest.getNombre());
            Categoria categoriaModificada = categoriaRepository.save(categoria);
            return categoriaModificada;
        }else{

            return categoria;

        }

    }

    public void borrarCategoria(Integer idCategoria){
        Categoria categoria = obtenerCategoriaPorId(idCategoria);

        String urlImagen = categoria.getUrlImagen();


        categoriaRepository.delete(categoria);

        if (!modificacionUtil.textoEsNuloOEnBlanco(urlImagen)){
            archivoRepository.borrar(CARPETA, urlImagen);
        }
    }


    public byte[] obtenerImagenCategoria(Integer idCategoria){
    Categoria categoria = encontrarCategoriaPorId(idCategoria);
    if (categoria.getUrlImagen() == null || categoria.getUrlImagen().isBlank()){
        return new byte[0];
    }else{
        return archivoRepository.descargar(CARPETA, categoria.getUrlImagen());
    }
    }

    public void modificarImagenCategoria(Integer idCategoria, MultipartFile archivo){
        archivoUtil.esArchivoVacio(archivo);
        archivoUtil.esImagen(archivo);

        Categoria categoria = obtenerCategoriaPorId(idCategoria);

        String nombreArchivoNuevo = archivoRepository.subir(archivo,CARPETA,categoria.getUrlImagen());

        if (modificacionUtil.textoHaSidoModificado(nombreArchivoNuevo, categoria.getUrlImagen())){
            categoria.setUrlImagen(nombreArchivoNuevo);
            categoriaRepository.save(categoria);
        }
    }

    public void borrarImagenCategoria(Integer idCategoria){
        Categoria categoria = obtenerCategoriaPorId(idCategoria);

        if (!modificacionUtil.textoEsNuloOEnBlanco(categoria.getUrlImagen())){
            return;
        }

        archivoRepository.borrar(CARPETA, categoria.getUrlImagen());

        categoria.setUrlImagen(null);
        categoriaRepository.save(categoria);
    }


}
