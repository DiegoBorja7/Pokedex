package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.API.PokemonServiceAPI;
import ec.edu.uce.pokedex.model.PokemonModel;
import ec.edu.uce.pokedex.services.PokemonDatabaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class PokedexApplication {
    
    public static void main(String[] args) {
        var context = SpringApplication.run(PokedexApplication.class, args);

        PokemonServiceAPI pokemonService = context.getBean(PokemonServiceAPI.class);
        PokemonDatabaseService pokemonDatabaseService = context.getBean(PokemonDatabaseService.class);

        System.out.println("Cargando Pokemons.");
        Flux<PokemonModel> pokemonDetails = pokemonService.getAllPokemonDetails();
        pokemonDetails
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pokemon -> pokemonDatabaseService.savePokemon(pokemon.toEntity()))
                .doOnError(error -> System.err.println("Error al procesar Pokémon: " + error.getMessage()))
                .subscribe(
                        pokemon -> System.out.println("Pokémon guardado: " + pokemon.getName()),
                        error -> System.err.println("Error en el flujo: " + error.getMessage()),
                        () -> System.out.println("Procesamiento de Pokémon completado.")
                );
        
    }

}
