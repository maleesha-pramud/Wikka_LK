package com.wigerlabs.wikka_lk.util;

import com.google.gson.Gson;
import java.security.SecureRandom;

public class AppUtil {
    public static final Gson GSON = new Gson();
    public static final int DEFAULT_SELECTOR_VALUE = 0;
    public static final String MAIN_APP_CURRENCY = "LKR";
    public static final String APP_COUNTRY = "Sri Lanka";
    public static final String FRONTEND_BASE_URL = "http://localhost:5173/";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateCode() {
        int randomNumber = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%6d", randomNumber);
    }

    public static String getVerificationEmailTemplate(String name, String verificationCode, String email) {
        String activationLink = FRONTEND_BASE_URL + "activate?email=" + email + "&code=" + verificationCode;
        return "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px;\">" +
               "<div style=\"background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05);\">" +
               "<h2 style=\"color: #333333; margin-top: 0;\">Welcome to Wikka.lk!</h2>" +
               "<p style=\"color: #555555; font-size: 16px; line-height: 1.5;\">Hello <strong>" + name + "</strong>,</p>" +
               "<p style=\"color: #555555; font-size: 16px; line-height: 1.5;\">Thank you for creating an account with us. Please activate your account by clicking the button below:</p>" +
               "<div style=\"text-align: center; margin: 30px 0;\">" +
               "<a href=\"" + activationLink + "\" style=\"display: inline-block; padding: 12px 24px; color: #ffffff; background-color: #0066cc; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; transition: background-color 0.3s;\">Activate Account</a>" +
               "</div>" +
               "<p style=\"color: #555555; font-size: 16px; line-height: 1.5;\">If the button doesn't work, you can also activate your account by entering this verification code manually:</p>" +
               "<div style=\"background-color: #f0f7ff; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; border: 1px dashed #0066cc;\">" +
               "<span style=\"font-size: 24px; font-weight: bold; color: #0066cc; letter-spacing: 5px;\">" + verificationCode + "</span>" +
               "</div>" +
               "<hr style=\"border: none; border-top: 1px solid #eeeeee; margin: 30px 0;\">" +
               "<p style=\"color: #888888; font-size: 14px; text-align: center; margin-bottom: 0;\">Best Regards,<br><strong>Wikka.lk Team</strong></p>" +
               "</div></div>";
    }

    public static String getActivationSuccessEmailTemplate(String name) {
        String loginLink = FRONTEND_BASE_URL + "login";
        return "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px;\">" +
               "<div style=\"background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border-top: 4px solid #28a745;\">" +
               "<h2 style=\"color: #333333; margin-top: 0;\">Account Activated! 🎉</h2>" +
               "<p style=\"color: #555555; font-size: 16px; line-height: 1.5;\">Hello <strong>" + name + "</strong>,</p>" +
               "<p style=\"color: #555555; font-size: 16px; line-height: 1.5;\">Your Wikka.lk account has been successfully verified and activated. You can now log in and start exploring our platform.</p>" +
               "<div style=\"text-align: center; margin: 30px 0;\">" +
               "<a href=\"" + loginLink + "\" style=\"display: inline-block; padding: 12px 24px; color: #ffffff; background-color: #28a745; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;\">Go to Login</a>" +
               "</div>" +
               "<hr style=\"border: none; border-top: 1px solid #eeeeee; margin: 30px 0;\">" +
               "<p style=\"color: #888888; font-size: 14px; text-align: center; margin-bottom: 0;\">Best Regards,<br><strong>Wikka.lk Team</strong></p>" +
               "</div></div>";
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
