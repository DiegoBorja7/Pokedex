package ec.edu.uce.pokedex.API;

import ec.edu.uce.pokedex.entities.Pokemonv2;
import ec.edu.uce.pokedex.model.PokemonList;
import ec.edu.uce.pokedex.model.PokemonModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PokemonServiceAPI {

    private final WebClient webClient;
    //private final PokeApiClient pokeApiClient;

    public PokemonServiceAPI(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://pokeapi.co/api/v2").build();
    }

    public Mono<Pokemonv2> getPokemonByName(String name) {
        String cleanedName = name.trim();

        return webClient.get()
                .uri("/pokemon/{name}", cleanedName)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> Mono.error(new RuntimeException("Pokémon no encontrado"))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("Error del servidor"))
                )
                .bodyToMono(Pokemonv2.class);
    }

    /**
     * Obtiene una lista de Pokémon con paginación.
     *
     * @param offset Inicio de la lista.
     * @param limit  Número máximo de resultados por consulta.
     * @return Un Mono que contiene un objeto PokemonList.
     */
    public Mono<PokemonList> getAllPokemon(int offset, int limit) {
        // Validar que offset y limit sean positivos
        if (offset < 0 || limit <= 0) {
            return Mono.error(new IllegalArgumentException("Offset y limit deben ser mayores o iguales a 0"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pokemon")
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> Mono.error(new RuntimeException("No se pudieron recuperar los Pokémon"))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("Error del servidor"))
                )
                .bodyToMono(PokemonList.class);
    }

    /**
     * Obtiene la información de todos los Pokémon.
     *
     * @return Un Flux que contiene objetos Pokemonv2.
     */
    public Flux<PokemonModel> getAllPokemonDetails() {
        int startId = 1;
        int endId = 1350;
        return Flux.range(startId, endId - startId + 1)
                .parallel() // Habilitar paralelismo
                .runOn(Schedulers.parallel()) // Ejecutar en hilos paralelos
                .flatMap(id -> webClient.get()
                        .uri("/pokemon/{id}", id)
                        .retrieve()
                        .bodyToMono(PokemonModel.class)
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(500)))// Recolecta todos los detalles de los Pokémon
                        .doOnError(e -> System.out.println("Error con ID: " + id))
                        .onErrorResume(e -> Mono.empty())
                    )
                .sequential();

    }

    /**
     * Obtiene la información detallada de todos los Pokémon disponibles en la API.
     *
     * @return Un Flux que contiene objetos PokemonModel con la información completa.
     */
    public Flux<PokemonModel> getAllDetailedPokemon() {
        int batchSize = 400; // Tamaño del lote para la paginación
        int startId = 1;
        int endId = 1350;

        // Flux para recorrer la lista de Pokémon usando paginación
        return Flux.range(startId, endId - startId + 1) // Generar páginas ilimitadas hasta que la API no devuelva más resultados
                .flatMap(page -> {
                    int offset = page * batchSize; // Calcula el inicio del lote
                    return getAllPokemon(offset, batchSize) // Llama al método paginado
                            .flatMapMany(pokemonList -> {
                                if (pokemonList.getResults().isEmpty()) {
                                    return Flux.empty(); // Si no hay más resultados, detén el flujo
                                }
                                return Flux.fromIterable(pokemonList.getResults());
                            });
                })
                .takeUntil(pokemon -> pokemon == null) // Detenerse si no hay más Pokémon
                .map(pokemon -> pokemon.getId()) // Extraer el ID del Pokémon
                .distinct() // Eliminar IDs duplicados, si los hubiera
                .flatMapSequential(id -> webClient.get()
                        .uri("/pokemon/{id}", id)
                        .retrieve()
                        .bodyToMono(PokemonModel.class)
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(500))) // Reintenta en caso de errores
                        .doOnError(e -> System.out.println("Error con ID: " + id + " - Causa: " + e.getMessage()))
                        .onErrorResume(e -> Mono.empty()) // Omite errores individuales
                );
    }

    public Flux<PokemonModel> getAllDetailedPokemonFinal(int offset, int limit) {
        // Validar que offset y limit sean positivos
        if (offset < 0 || limit <= 0) {
            return Flux.error(new IllegalArgumentException("Offset y limit deben ser mayores o iguales a 0"));
        }

        // Paso 1: Obtienes la lista de los IDs de los Pokémon
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pokemon")
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(PokemonList.class)
                .doOnNext(pokemonList -> {
                    // Log de la respuesta de la lista de Pokémon
                    System.out.println("Pokémon list retrieved: " + pokemonList);
                })
                .flatMapMany(pokemonList -> {
                    if (pokemonList == null || pokemonList.getResults().isEmpty()) {
                        return Flux.empty();  // Si no hay Pokémon, retorna un Flux vacío
                    }

                    // Log de los resultados
                    pokemonList.getResults().forEach(pokemon -> {
                        System.out.println("Pokemon ID: " + pokemon.getId() + ", Name: " + pokemon.getName());
                    });

                    // Paso 2: Para cada Pokémon, obtener sus detalles
                    return Flux.fromIterable(pokemonList.getResults())
                            .parallel()
                            .runOn(Schedulers.parallel())
                            .flatMap(pokemon -> webClient.get()
                                    .uri("/pokemon/{name}", pokemon.getName())
                                    .retrieve()
                                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                                        if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                            // Registra el error o maneja el caso de Pokémon no encontrado
                                            return Mono.error(new RuntimeException("Pokémon no encontrado: " + pokemon.getId()));
                                        }
                                        return Mono.error(new RuntimeException("Error inesperado con el Pokémon ID: " + pokemon.getId()));
                                    })
                                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                                        // Maneja errores de servidor (5xx)
                                        return Mono.error(new RuntimeException("Error del servidor al obtener el Pokémon ID: " + pokemon.getId()));
                                    })
                                    .bodyToMono(PokemonModel.class)
                            );
                });
    }
}
