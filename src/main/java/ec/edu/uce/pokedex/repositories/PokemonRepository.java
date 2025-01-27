package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.entities.PokemonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends JpaRepository<PokemonEntity, Integer> {
    @Query("SELECT p FROM PokemonEntity p WHERE p.name = :nombre")
    PokemonEntity buscarPorNombre(String nombre);
}

