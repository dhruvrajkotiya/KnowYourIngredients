package com.myprojects.knowyouringredients;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Handles communication with the Google Gemini API to fetch generated content.
 * This class provides a static method to send a prompt to the Gemini model
 * and receive a text-based response.
 */
public class GeminiClient {

    // IMPORTANT: It is strongly recommended to not hardcode API keys directly in the source code.
    // Consider using a backend proxy or secure build configurations for API key management in production environments.
    private static final String API_KEY = "AIzaSyDDwF-KJ2VlQF9J0EgIR8WOU5ekWTeemKM";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Asynchronously fetches an answer from the Gemini API based on the provided prompt.

     * This method constructs the JSON request body required by the Gemini API,
     * including the prompt and generation configuration parameters. It then executes
     * the HTTP POST request on a new background thread to avoid blocking the main UI thread.

     * The results (either the generated text or an error message) are delivered
     * via the provided {@link GeminiCallback}.

     * @param prompt The main input text/prompt to send to the Gemini model.
     * @param question This parameter is currently unused in the request construction but is kept for potential future use or context.
     * @param callback The callback to be invoked with the API response or error.
     */
    public static void fetchAnswer(String prompt, String question, GeminiCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Construct the JSON request body for the Gemini API.
            JSONObject requestJson = new JSONObject();

            // Define the content part of the request, containing the prompt.
            JSONArray contents = new JSONArray();
            JSONObject contentItem = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            contentItem.put("parts", parts);
            contents.put(contentItem);
            requestJson.put("contents", contents);

            // Define the generation configuration parameters.
            // These parameters influence how the model generates the response.

            /*
             | Property         | Type        | Description                                          |
             |------------------|-------------|------------------------------------------------------|
             | temperature      | float       | Controls randomness in the output.                   |
             | topK             | int         | Limits the model to the top K tokens by probability. |
             | topP             | float       | Limits the model to the top P cumulative probability.|
             | maxOutputTokens  | int         | Max number of tokens (words) in the response.        |
             | stopSequences    | string[]    | Specifies strings where output should stop.          |
             | candidateCount   | int         | How many response variations you want.               |
            */


            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.0);// Controls randomness. 0.0 is deterministic.
            generationConfig.put("candidateCount", 1); // Number of response candidates to generate.
            generationConfig.put("top_k", 1); // Considers the top K tokens at each step.
            generationConfig.put("maxOutputTokens", 150); // Maximum number of tokens in the generated response

            requestJson.put("generationConfig", generationConfig);

            // Log the complete request body for debugging purposes (pretty printed JSON).
            Log.d("requestBody", requestJson.toString(4));

            RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);

            // Build the HTTP POST request.
            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(requestBody)
                    .build();

            // Execute the network request on a new background thread.
            new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        // Parse the JSON response from the API.
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONArray candidates = responseJson.getJSONArray("candidates");
                        // Assuming at least one candidate is always present if successful.
                        JSONObject firstCandidate = candidates.getJSONObject(0);
                        JSONObject content = firstCandidate.getJSONObject("content");
                        JSONArray responseParts = content.getJSONArray("parts");
                        // Assuming the first part contains the text response.
                        String text = responseParts.getJSONObject(0).getString("text");
                        callback.onResponse(text);
                    } else {
                        // Handle unsuccessful HTTP responses.
                        assert response.body() != null;
                        callback.onError("HTTP Error: " + response.code() + "\n" + response.body().string());
                    }
                } catch (IOException | JSONException e) {
                    // Handle exceptions during network I/O or JSON parsing
                    callback.onError("Exception: " + e.getMessage());
                }
            }).start();

        } catch (JSONException e) {
            // Handle exceptions during the initial JSON request body construction.
            Log.e("GeminiClient", "JSONException during request body construction", e);
            callback.onError("Build Error: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions during setup.
            Log.e("GeminiClient", "Unexpected exception during request setup", e);
            callback.onError("Build Error: " + e.getMessage());
        }
    }

    /**
     * Callback interface for handling responses from the Gemini API.
     */
    public interface GeminiCallback {

        /**
         * Called when the API request is successful and a response is received.
         * @param result The text content generated by the Gemini model.
         */
        void onResponse(String result);

        /**
         * Called when an error occurs during the API request or response processing.
         * @param error A message describing the error.
         */
        void onError(String error);
    }
}
