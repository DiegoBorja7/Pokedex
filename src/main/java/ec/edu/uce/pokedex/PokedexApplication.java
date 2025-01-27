package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.API.PokemonServiceAPI;
import ec.edu.uce.pokedex.gui.Menu;
import ec.edu.uce.pokedex.model.PokemonModel;
import ec.edu.uce.pokedex.services.PokemonDatabaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class PokedexApplication {

    private static PokemonServiceAPI pokemonService;
    private static PokemonDatabaseService pokemonDatabaseService;
    
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        var context = SpringApplication.run(PokedexApplication.class, args);

        pokemonService = context.getBean(PokemonServiceAPI.class);
        pokemonDatabaseService = context.getBean(PokemonDatabaseService.class);
        Menu menufrm = new Menu(pokemonDatabaseService);

        //chargePokemons();

        // Abre la interfaz gráfica en un hilo separado para evitar bloqueos
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.out.println("La aplicación está ejecutándose en un entorno headless.");

        } else {
            System.setProperty("java.awt.headless", "false");
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    menufrm.setVisible(true);
                }
            });
        }
    }

    private static void chargePokemons(){
        System.out.println("Cargando Pokemons.");
        Flux<PokemonModel> pokemonDetails = pokemonService.getAllPokemonDetails();
        pokemonDetails
                .publishOn(Schedulers.boundedElastic())
                .filter(pokemon -> !pokemonDatabaseService.existsById(pokemon.getId())) // Verificar si ya existe en la base de datos
                .doOnNext(pokemon -> pokemonDatabaseService.savePokemon(pokemon.toEntity()))
                .doOnError(error -> System.err.println("Error al procesar Pokémon: " + error.getMessage()))
                .subscribe(
                        pokemon -> {},
                        error -> System.err.println("Error en el flujo: " + error.getMessage()),
                        () -> System.out.println("Carga de Pokémons completado.")
                );
    }
}