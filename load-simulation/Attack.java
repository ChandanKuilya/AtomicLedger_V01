import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Attack {

    // UPDATE PORT IF NEEDED
    private static final String URL = "http://localhost:8081/api/ledger/transfer?fromId=5&toId=6&amount=1.00";
    private static final int THREAD_COUNT = 100;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("  Starting Java Attack Simulation ");
        System.out.println("Target: " + URL);
        System.out.println("Threads: " + THREAD_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        HttpClient client = HttpClient.newHttpClient();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            tasks.add(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .POST(HttpRequest.BodyPublishers.noBody()) // Empty body, params are in URL
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        System.out.println("......Attacked!......"); // Success
                        successCount.incrementAndGet();
                    } else {
                        System.out.print("x...Failed to Attack...Application Error....x"); // Fail (Application Error)
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.print("Error!...Connection Error...."); // Connection Error
                    failCount.incrementAndGet();
                }
                return null;
            });
        }

        long start = System.currentTimeMillis();
        executor.invokeAll(tasks); // Run all tasks
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();

        System.out.println("\n\n--- Report ---");
        System.out.println("Time: " + (end - start) + "ms");
        System.out.println("Success: " + successCount.get());
        System.out.println("Failed: " + failCount.get());
        System.out.println("-------------------------------------------");
        System.out.println("NOW CHECK DATABASE.");
        System.out.println("Expected Balance User 1: 900.00");
        System.out.println("Actual Balance User 1:   [Run SQL to check]");
    }
}