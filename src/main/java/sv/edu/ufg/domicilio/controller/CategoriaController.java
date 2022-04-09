package sv.edu.ufg.domicilio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.edu.ufg.domicilio.model.Categoria;
import sv.edu.ufg.domicilio.request.CategoriaRequest;
import sv.edu.ufg.domicilio.service.CategoriaService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping("/categorias")
    public Page<Categoria> obtenerCategorias(Pageable pageable){
        return categoriaService.obtenerCategorias(pageable);
    }

    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crearCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest){
        return categoriaService.crearCategoria(categoriaRequest);
    }

    @PatchMapping("/categorias/{idCategoria}")
    public Categoria modificarCategoria(@PathVariable("idCategoria") Integer idCategoria, @RequestBody CategoriaRequest categoriaRequest){

        return categoriaService.modificarCategoria(idCategoria,categoriaRequest);
    }

    @DeleteMapping("/categorias/{idCategoria}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarCategoria(@PathVariable("idCategoria") Integer idCategoria){

        categoriaService.borrarCategoria(idCategoria);
    }

    @GetMapping("/categorias/{idCategoria}/imagen")
    public byte[] obtenerImagenCategoria(@PathVariable("idCategoria") Integer idCategoria){
        return categoriaService.obtenerImagenCategoria(idCategoria);
    }

    @PutMapping(
            value="/categorias/{idCategoria}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void modificarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                         @PathVariable("archivo") MultipartFile archivo){
            categoriaService.modificarImagenCategoria(idCategoria, archivo);
    }

    @DeleteMapping("/categorias/{idCategoria}/imagen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria){

        categoriaService.borrarImagenCategoria(idCategoria);
    }

}
