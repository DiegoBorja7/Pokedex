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

    @Transactional
    public List<PokemonEntity> obtenerTodosLosPokemones() {
        return pokemonRepository.findAll();
    }

    public boolean existsById(int id) {
        return pokemonRepository.existsById(id);
    }

    public PokemonEntity obtenerPokemonConDetalles(int id) {
        PokemonEntity pokemon = pokemonRepository.findById(id).orElse(null);
        return pokemon;
    }

    @Transactional
    public PokemonEntity buscarPorNombre(String nombre){
        return pokemonRepository.buscarPorNombre(nombre);
    }
}
