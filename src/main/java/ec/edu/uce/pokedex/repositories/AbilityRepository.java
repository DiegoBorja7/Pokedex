package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.entities.AbilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbilityRepository extends JpaRepository<AbilityEntity, Integer> {
}
