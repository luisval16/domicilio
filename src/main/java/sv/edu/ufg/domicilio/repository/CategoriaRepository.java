package sv.edu.ufg.domicilio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.ufg.domicilio.model.Categoria;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Optional<Categoria> findByNombre(String nombre);

}
