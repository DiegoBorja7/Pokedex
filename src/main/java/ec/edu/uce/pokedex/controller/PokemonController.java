package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.API.PokemonServiceAPI;
import ec.edu.uce.pokedex.entities.Pokemonv2;
import ec.edu.uce.pokedex.model.PokemonList;
import ec.edu.uce.pokedex.model.PokemonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {
    private final PokemonServiceAPI pokemonService;

    @Autowired
    public PokemonController(PokemonServiceAPI pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public Mono<Pokemonv2> getPokemon(@PathVariable String name) {
        return pokemonService.getPokemonByName(name);
    }


    /**
     * Endpoint para obtener una lista de Pokémon con paginación.
     *
     * @param offset Inicio de la lista (por defecto 0).
     * @param limit  Número máximo de resultados por consulta (por defecto 20).
     * @return Un Mono que contiene un objeto PokemonList.
     */
    @GetMapping("/get-all-pokemon")
    public Mono<PokemonList> getAllPokemon(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {
        return pokemonService.getAllPokemon(offset, limit);
    }

    /**
     * Endpoint para obtener detalles de todos los Pokémon (usando paginación interna).
     *
     * @return Un Flux que contiene objetos Pokemonv2.
     */
    @GetMapping("/get-all-pokemon-details")
    public Flux<PokemonModel> getAllPokemonDetails() {
        return pokemonService.getAllPokemonDetails();
    }
}
