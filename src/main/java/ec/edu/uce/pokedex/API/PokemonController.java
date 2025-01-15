package ec.edu.uce.pokedex.API;

import ec.edu.uce.pokedex.entities.Pokemon;
import ec.edu.uce.pokedex.entities.Pokemonv2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PokemonController {
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/pokemon/{name}")
    public Mono<Pokemonv2> getPokemon(@PathVariable String name) {
        return pokemonService.getPokemonByName(name);
    }
}
