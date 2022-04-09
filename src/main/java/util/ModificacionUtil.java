package util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ModificacionUtil {

    public boolean textoEsNuloOEnBlanco(String valor){
        return valor == null || valor.isBlank();
    }

    public boolean textoHaSidoModificado(String nuevoValor, String valorAnterior){
        return (!textoEsNuloOEnBlanco(nuevoValor) && !Objects.equals(nuevoValor,valorAnterior));
    }
}
