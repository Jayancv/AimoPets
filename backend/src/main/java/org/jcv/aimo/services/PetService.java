package org.jcv.aimo.services;

import org.jcv.aimo.models.User;
import org.jcv.aimo.utils.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.util.*;
import java.util.concurrent.*;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final WebClient webClient;

    private final String USER_BASE_URL;
    private final String DOG_BASE_URL;

    public
    @Autowired
    PetService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        USER_BASE_URL = "https://randomuser.me/api/";
        DOG_BASE_URL = "https://dog.ceo/api/breeds/image/random/";
    }

    public PetService(WebClient.Builder webClientBuilder, String user_url, String dog_url) {
        this.webClient = webClientBuilder.build();
        USER_BASE_URL = user_url;
        DOG_BASE_URL = dog_url;
    }

    /**
     * Fetch users and Pets
     *
     * @param count
     * @param nationalities
     * @return List of Users with their Pet images
     */
    public List<User> fetchUsersWithPets(int count, String nationalities) {

        boolean PARALLEL = true;
        List<User> userResults = null;
        List<String> dogImages = null;
        if (PARALLEL) {
            // Run both API calls in parallel
            CompletableFuture<List<User>> usersFuture = CompletableFuture.supplyAsync(() -> loadUsers(count, nationalities));
            CompletableFuture<List<String>> dogsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return loadDogsParallel(count);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            // Wait for both to complete
            CompletableFuture.allOf(usersFuture, dogsFuture).join();
            try {
                userResults = usersFuture.get();
                dogImages = dogsFuture.get();
            } catch (InterruptedException e) {
                logger.error("Interrupted.", e);
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                logger.error("Runtime exception occurred.", e);
                throw new RuntimeException(e);
            }

        } else {
            userResults = loadUsers(count, nationalities);
            dogImages = loadDogs(count);
        }

        List<User> combined = combineUserAndPet(userResults, dogImages);

        return combined;
    }

    private List<User> loadUsers(int count, String nationalities) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(USER_BASE_URL).builder();
        uriBuilder.queryParam("results", String.valueOf(count));

        if (nationalities != null && !nationalities.isBlank()) {
            uriBuilder.queryParam("nat", nationalities);
        }

        String finalUrl = uriBuilder.toUriString();

        Map<String, Object> userResponse = null;
        try {
            userResponse = webClient.get().uri(finalUrl).retrieve().bodyToMono(Map.class).block();
        } catch (Exception e) {
            String msg = "";
            if (e.getCause()!= null && e.getCause().getMessage()!=null) {
                msg = e.getCause().getMessage();
            }
            msg  = "Error in loading users. " + msg;
            logger.error(msg , e);
            throw new RuntimeException(msg, e);
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) userResponse.get("results");

        List<User> users = results.stream()
                .map(DataParser::mapToUser)
                .toList();

        return users;
    }

    private List<String> loadDogsParallel(int count) throws ExecutionException, InterruptedException {

        final int MAX_PER_REQUEST = 50;
        int fullBatches = count / MAX_PER_REQUEST;
        int remainder = count % MAX_PER_REQUEST;

        List<Integer> batchSizes = new ArrayList<>();
        for (int i = 0; i < fullBatches; i++) batchSizes.add(MAX_PER_REQUEST);
        if (remainder > 0) batchSizes.add(remainder);

        WebClient webClient = WebClient.builder().baseUrl(DOG_BASE_URL).build();

        // ExecutorService with fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(batchSizes.size());

        List<Future<List<String>>> futures = new ArrayList<>();

        for (Integer batchSize : batchSizes) {
            Future<List<String>> future = executor.submit(() -> {
                Map<String, Object> dogResponse = webClient.get()
                        .uri("/{count}", batchSize)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                return (List<String>) dogResponse.get("message");
            });
            futures.add(future);
        }

        // Collect results
        List<String> allDogs = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            allDogs.addAll(future.get());
        }

        executor.shutdown();
        return allDogs;
    }

    private List<String> loadDogs(int count) {

        UriBuilder uriBuilder = new DefaultUriBuilderFactory(DOG_BASE_URL).builder();
        uriBuilder.path(String.valueOf(count));
        String finalUrl = uriBuilder.toUriString();

        Map<String, Object> dogResponse = webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<String> dogImages = (List<String>) dogResponse.get("message");
        return dogImages;
    }


    private static List<User> combineUserAndPet(List<User> userResults, List<String> dogImages) {
        for (int i = 0; i < userResults.size(); i++) {
            User user = userResults.get(i);
            user.setPetImage(dogImages.get(i));
        }
        return userResults;
    }


}
