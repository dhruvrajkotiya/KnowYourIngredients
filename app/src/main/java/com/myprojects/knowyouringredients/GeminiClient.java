package com.myprojects.knowyouringredients;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiClient {

    private static final String API_KEY = ""; // 🔁 Replace with your own key
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface GeminiCallback {
        void onResponse(String result);
        void onError(String error);
    }

    public static void fetchAnswer(String prompt, String question, GeminiCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            // 🔧 Correct JSON structure
            JSONObject requestJson = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject contentItem = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            contentItem.put("parts", parts);
            contents.put(contentItem);
            requestJson.put("contents", contents);

            RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(requestBody)
                    .build();

            new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONArray candidates = responseJson.getJSONArray("candidates");
                        JSONObject firstCandidate = candidates.getJSONObject(0);
                        JSONObject content = firstCandidate.getJSONObject("content");
                        JSONArray responseParts = content.getJSONArray("parts");
                        String text = responseParts.getJSONObject(0).getString("text");
                        callback.onResponse(text);
                    } else {
                        callback.onError("HTTP Error: " + response.code() + "\n" + response.body().string());
                    }
                } catch (IOException | JSONException e) {
                    callback.onError("Exception: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            callback.onError("Build Error: " + e.getMessage());
        }
    }
}
