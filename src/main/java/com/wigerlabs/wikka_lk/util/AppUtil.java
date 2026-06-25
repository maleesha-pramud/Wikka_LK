package com.wigerlabs.wikka_lk.util;

import com.google.gson.Gson;
import java.security.SecureRandom;

public class AppUtil {
    public static final Gson GSON = new Gson();
    public static final int DEFAULT_SELECTOR_VALUE = 0;
    public static final String MAIN_APP_CURRENCY = "LKR";
    public static final String APP_COUNTRY = "Sri Lanka";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateCode() {
        int randomNumber = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%6d", randomNumber);
    }

    public static void sendEmail(String toEmail, String subject, String htmlContent) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
                // If you are using onboarding@resend.dev, Resend restricts sending ONLY to your own verified email.
                // Make sure you send to your registered email or use a verified custom domain.
                payload.addProperty("from", "Wikka.lk <onboarding@resend.dev>");
                com.google.gson.JsonArray toArray = new com.google.gson.JsonArray();
                toArray.add(toEmail);
                payload.add("to", toArray);
                payload.addProperty("subject", subject);
                payload.addProperty("html", htmlContent);

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create("https://api.resend.com/emails"))
                        .header("Authorization", "Bearer re_bUHXUpKS_6wGxGAR55PjjDUnY4tmM1v9D")
                        .header("Content-Type", "application/json")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                        .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

                System.out.println("Resend Email API Status: " + response.statusCode());
                System.out.println("Resend Email API Response: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
