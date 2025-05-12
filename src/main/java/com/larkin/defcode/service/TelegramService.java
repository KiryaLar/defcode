package com.larkin.defcode.service;

import com.larkin.defcode.exception.CodeSendingMethodException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.api.url}")
    private String telegramApiUrl;

    public void sendCode(String chatId, int code) {
        log.info("Sending code by telegram");
        String message = String.format("Your code is %s", code);
        String url = String.format(telegramApiUrl, botToken) + "?chat_id=" + chatId + "&text=" + urlEncode(message);
        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    log.error("Telegram API error. Status: {}, Response: {}", statusCode, responseBody);
                    CodeSendingMethodException.telegram("Failed to send Telegram message");
                }
              log.info("Telegram message sent successfully");
            }
        } catch (IOException e) {
            log.error("Failed to send Telegram message: {}", e.getMessage());
            CodeSendingMethodException.telegram("Failed to send Telegram message");
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
