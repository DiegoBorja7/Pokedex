package ec.edu.uce.pokedex.API;

import ec.edu.uce.pokedex.entities.Pokemon;
import ec.edu.uce.pokedex.entities.Pokemonv2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PokemonService {

    private final WebClient webClient;

    public PokemonService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://pokeapi.co/api/v2").build();
    }

    public Mono<Pokemonv2> getPokemonByName(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name)
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
}
