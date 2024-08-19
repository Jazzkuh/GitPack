package com.jazzkuh.gitpack.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class RequestUtils {
    public final ExecutorService executorService = Executors.newCachedThreadPool();

    @SneakyThrows
    public CompletableFuture<JsonObject> get(String url, @Nullable Map<String, String> properties, String... authorization) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        executorService.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                if (properties != null) {
                    for (Map.Entry<String, String> entry : properties.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (authorization.length > 0) {
                    connection.setRequestProperty("Authorization", authorization[0]);
                }

                @Cleanup
                InputStream inputStream = connection.getInputStream();
                JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
                future.complete(jsonObject);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}