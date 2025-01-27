package ec.edu.uce.pokedex.services;

import ec.edu.uce.pokedex.entities.AbilityEntity;
import ec.edu.uce.pokedex.entities.PokemonEntity;
import ec.edu.uce.pokedex.entities.SpritesEntity;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonDatabaseService {
    @Autowired
    private final PokemonRepository pokemonRepository;

    public PokemonDatabaseService( PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    @Transactional
    public PokemonEntity savePokemon(PokemonEntity pokemon) {
        return pokemonRepository.save(pokemon);
    }

    public PokemonEntity createPokemon(int id, String name, int baseExperience, int height, int weight,
                                       List<AbilityEntity> abilities, SpritesEntity sprites) {
        PokemonEntity pokemon = new PokemonEntity();
        pokemon.setId(id);
        pokemon.setName(name);
        pokemon.setBaseExperience(baseExperience);
        pokemon.setHeight(height);
        pokemon.setWeight(weight);
        pokemon.setAbilities(abilities);
        pokemon.setSprites(sprites);

        return savePokemon(pokemon);
    }
}
