package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.entities.SpritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpritesRepository extends JpaRepository<SpritesEntity, Integer> {
}
