package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.API.PokemonServiceAPI;
import ec.edu.uce.pokedex.entities.Pokemonv2;
import ec.edu.uce.pokedex.model.PokemonList;
import ec.edu.uce.pokedex.model.PokemonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/load-all-pokemon")
    public ResponseEntity<String> loadAllPokemon() {
        //pokemonDatabaseService.saveAllPokemonToDatabase();
        return ResponseEntity.ok("Carga de Pokémon iniciada");
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

    @GetMapping("/get-all-pokemon-details2")
    public Flux<PokemonModel> getAllPokemonDetails2() {
        return pokemonService.getAllDetailedPokemon()
            .doOnError(e -> System.out.println("Error al obtener los Pokémon: " + e.getMessage()));
    }

    @GetMapping("/get-all-pokemon-details7")
    public ResponseEntity<Flux<PokemonModel>> getAllPokemonDetailsFinal(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit){
        // Validar que los parámetros sean correctos
        if (offset < 0 || limit <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.empty());  // Si los parámetros son inválidos, devolver un Flux vacío
        }

        try {
            // Llamamos al servicio para obtener los Pokémon detallados de manera reactiva
            Flux<PokemonModel> pokemons = pokemonService.getAllDetailedPokemonFinal(offset, limit);
            return ResponseEntity.ok(pokemons); // Retorna los Pokémon con estado 200 OK

        } catch (Exception e) {
            // En caso de error, retornamos una respuesta 400 con un Flux vacío
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.empty());
        }
    }
}
