package org.jcv.aimo.services;

import org.jcv.aimo.models.User;
import org.jcv.aimo.utils.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.*;

@Service
public class PetService
{
    public static final String USER_URL = "https://randomuser.me/api/";
    public static final String DOG_URL = "https://dog.ceo/api/breeds/image/random/";

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    public static final int SEQUENTIAL_LIMIT = 50;
    public static final int MAX_PER_DOG_REQUEST = 50;

    public static final int MAX_PER_USER_REQUEST = 5000;
    public static final int USER_API_BUFFER_SIZE = 7 * 1024 * 1024;

    private final WebClient userClient;
    private final WebClient dogClient;

    private final String USER_BASE_URL;
    private final String DOG_BASE_URL;

    @Autowired
    ExecutorService executor;

    public
    @Autowired
    PetService(WebClient.Builder webClientBuilder)
    {
        this(webClientBuilder, USER_URL, DOG_URL);
    }

    /**
     * This constructor uses for testing
     *
     * @param webClientBuilder
     * @param user_url
     * @param dog_url
     */
    public PetService(WebClient.Builder webClientBuilder, String user_url, String dog_url)
    {
        USER_BASE_URL = user_url;
        DOG_BASE_URL = dog_url;
        this.userClient = webClientBuilder.baseUrl(USER_BASE_URL).exchangeStrategies(ExchangeStrategies.builder().
                codecs(c -> c.defaultCodecs().maxInMemorySize(USER_API_BUFFER_SIZE)).build()).
            build();
        this.dogClient = webClientBuilder.baseUrl(DOG_BASE_URL).build();
    }

    /**
     * Fetch users and Pets
     *
     * @param count
     * @param nationalities
     * @return List of Users with their Pet images
     */
    public List<User> fetchUsersWithPets(int count, String nationalities)
    {

        if (count < 1) {
            return new ArrayList<>();
        }
        // if result count higher than 50 then need to load dogs and users parallel
        boolean PARALLEL =  count > SEQUENTIAL_LIMIT;
        List<User> userResults = null;
        List<String> dogImages = null;
        if (PARALLEL) {
            // Run both API calls in parallel
            CompletableFuture<List<User>> usersFuture = CompletableFuture.supplyAsync(() ->
                loadUsersParallel(count, nationalities, executor));
            CompletableFuture<List<String>> dogsFuture =
                CompletableFuture.supplyAsync(() -> loadDogsParallel(count, executor));

            // Wait for both to complete
            CompletableFuture.allOf(usersFuture, dogsFuture).join();
            try {
                userResults = usersFuture.get();
                dogImages = dogsFuture.get();
            } catch (InterruptedException e) {
                logger.error("Interrupted.", e);
                String msg = getErrorMsg(e);
                msg = "Error in loading. " + msg;
                throw new RuntimeException(msg, e);
            } catch (ExecutionException e) {
                logger.error("Runtime exception occurred.", e);
                String msg = getErrorMsg(e);
                msg = "Error in loading. " + msg;
                throw new RuntimeException(msg, e);
            }
        } else {
            userResults = loadUsers(count, nationalities);
            dogImages = loadDogs(count);
        }

        return combineUserAndPet(userResults, dogImages);
    }

    /**
     * Load users parallel if the count is higher than 200
     *
     * @param count
     * @param nationalities
     * @return
     */
    private List<User> loadUsersParallel(int count, String nationalities, ExecutorService executor)
    {
        // TODO increase the WebClient buffer size to handle larger responses

        final int MAX_PER_REQUEST = MAX_PER_USER_REQUEST;   // RandomUser API recommended batch size
        List<Integer> batchSizes = new ArrayList<>();

        // ---- Split into batches ----
        int fullBatches = count / MAX_PER_REQUEST;
        int remainder = count % MAX_PER_REQUEST;

        for (int i = 0; i < fullBatches; i++) {
            batchSizes.add(MAX_PER_REQUEST);
        }
        if (remainder > 0) {
            batchSizes.add(remainder);
        }

        // ---- Parallel execution ----
        List<Future<List<User>>> futures = new ArrayList<>();

        for (Integer batchSize : batchSizes) {
            futures.add(executor.submit(() -> {

                // --- Build user API request ---
                Map<String, Object> response = userClient.get()
                    .uri(uri -> uri
                        .queryParam("results", batchSize)
                        .queryParam("nat", nationalities)
                        .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

                // --- Convert API results to User objects ---
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                return results.stream()
                    .map(DataParser::mapToUser)
                    .toList();
            }));
        }

        // ---- Combine results from all batches ----
        List<User> allUsers = new ArrayList<>();

        try {
            for (Future<List<User>> future : futures) {
                allUsers.addAll(future.get());
            }
        } catch (Exception e) {
            String msg = getErrorMsg(e);
            msg = "Error in parallel loading users. " + msg;
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        return allUsers;
    }

    /**
     * Load users from user api
     *
     * @param count
     * @param nationalities
     * @return
     */
    private List<User> loadUsers(int count, String nationalities)
    {
        Map<String, Object> userResponse = null;
        try {
            userResponse = userClient.get().uri(uri -> uri
                .queryParam("results", count)
                .queryParam("nat", nationalities)
                .build()).retrieve().bodyToMono(Map.class).block();
        } catch (Exception e) {
            String msg = getErrorMsg(e);
            msg = "Error in loading users. " + msg;
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) userResponse.get("results");

        List<User> users = results.stream()
            .map(DataParser::mapToUser)
            .toList();

        return users;
    }

    /**
     * Loading dogs using parallel threads because max dogs per request is 50 THis method load dogs from api size of 50
     * batches
     *
     * @param count
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private List<String> loadDogsParallel(int count, ExecutorService executor)
    {

        final int MAX_PER_REQUEST = MAX_PER_DOG_REQUEST;
        int fullBatches = count / MAX_PER_REQUEST;
        int remainder = count % MAX_PER_REQUEST;

        List<Integer> batchSizes = new ArrayList<>();
        for (int i = 0; i < fullBatches; i++) {
            batchSizes.add(MAX_PER_REQUEST);
        }
        if (remainder > 0) {
            batchSizes.add(remainder);
        }

        List<Future<List<String>>> futures = new ArrayList<>();

        for (Integer batchSize : batchSizes) {
            Future<List<String>> future = executor.submit(() -> {
                Map<String, Object> dogResponse = dogClient.get()
                    .uri(uri -> uri.path(String.valueOf(batchSize)).build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

                return (List<String>) dogResponse.get("message");
            });
            futures.add(future);
        }

        // Collect results
        List<String> allDogs = new ArrayList<>();

        try {
            for (Future<List<String>> future : futures) {
                allDogs.addAll(future.get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error merging dog batches", e);
        }
        return allDogs;
    }

    /**
     * Load dogs sequentially
     *
     * @param count
     * @return
     */
    private List<String> loadDogs(int count)
    {
        Map<String, Object> dogResponse = null;
        try {
            dogResponse =
                dogClient.get()
                    .uri(uri -> uri.path(String.valueOf(count)).build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            String msg = getErrorMsg(e);
            msg = "Error in loading dogs. " + msg;
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        List<String> dogImages = (List<String>) dogResponse.get("message");
        return dogImages;
    }

    /**
     * This method adding dog images to relevant users
     *
     * @param userResults
     * @param dogImages
     * @return List of User
     */
    private static List<User> combineUserAndPet(List<User> userResults, List<String> dogImages)
    {
        if (userResults.size() != dogImages.size()) {
            logger.warn("Mismatch found between result counts.");
        }
        int min = Math.min(userResults.size(), dogImages.size());
        for (int i = 0; i < min; i++) {
            User user = userResults.get(i);
            user.setPetImage(dogImages.get(i));
        }
        return userResults;
    }

    /**
     * Extract detail error message frpm exceptions
     *
     * @param e
     * @return detail error message
     */
    private static String getErrorMsg(Exception e)
    {
        String msg = "";
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            msg = e.getCause().getMessage();
        }
        return msg;
    }
}
