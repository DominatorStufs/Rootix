package com.rootix.launcher.managers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Handles the ".ai" / ".AI" command.
 *
 * Two OpenAI-compatible backends are pooled together:
 *  - https://chatbot.codexapi.workers.dev (many models: gpt, claude, gemini, grok, deepseek, qwen, kimi, llama, mistral...)
 *  - https://copilot-api-delta.vercel.app (single "copilot" model)
 *
 * A random model is picked from the pool for every query. If it fails (network error,
 * bad/empty response, non-2xx status) another random model is tried automatically,
 * up to MAX_ATTEMPTS times, before giving up.
 */
public class AiManager {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_ATTEMPTS = 6;

    public interface AiCallback {
        void onSuccess(String model, String response);
        void onError(String error);
    }

    private static class Provider {
        final String endpoint;
        final String[] models;

        Provider(String endpoint, String[] models) {
            this.endpoint = endpoint;
            this.models = models;
        }
    }

    private static class ModelChoice {
        final Provider provider;
        final String model;

        ModelChoice(Provider provider, String model) {
            this.provider = provider;
            this.model = model;
        }
    }

    private static final Provider CODEX_API = new Provider(
            "https://chatbot.codexapi.workers.dev/v1/chat/completions",
            new String[]{
                    "gpt-5.2", "gpt-5.1", "gpt-5", "anthropic/claude-sonnet-4", "mercury-coder",
                    "Olmo-3.1-32B-Instruct", "chatgpt-4o-latest", "google/gemini-2.5-pro-preview-05-06",
                    "x-ai/grok-4", "deepseek-ai/deepseek-v3.2", "deepseek-ai/deepseek-v3.1-terminus",
                    "deepseek-ai/deepseek-R1-0528", "o1-preview", "o3-mini", "qwen/qwen3.5-397b-a17b",
                    "qwen/qwen3-coder-480b-a35b-instruct", "moonshotai/kimi-k2.5", "moonshotai/kimi-k2-thinking",
                    "moonshotai/kimi-k2-instruct-0905", "openai/gpt-oss-120b", "openai/gpt-oss-20b",
                    "meta/llama-3.1-405b-instruct", "meta/llama-4-maverick-17b-128e-instruct",
                    "meta/llama-4-scout-17b-16e-instruct", "meta-llama-3.3-70b-instruct", "meta-llama-3.1-8b-instruct",
                    "google/gemma-3-27b-it", "nvidia/nemotron-3-nano-30b-a3b", "qwen/qwq-32b", "qwen/qwen3-235b-a22b",
                    "minimaxai/minimax-m2", "accounts/fireworks/models/glm-4p7", "meta-llama/Llama-3.1-8B-Instruct",
                    "mistralai/mistral-large-3-675b-instruct-2512", "mistralai/magistral-small-2506",
                    "mistralai/mistral-small-3.1-24b-instruct-2503", "mistralai/ministral-14b-instruct-2512"
            }
    );

    private static final Provider COPILOT_API = new Provider(
            "https://copilot-api-delta.vercel.app/v1/chat/completions",
            new String[]{"copilot"}
    );

    private static final Provider[] PROVIDERS = new Provider[]{CODEX_API, COPILOT_API};

    /**
     * Runs synchronously - call this from a background thread only.
     */
    public static void ask(OkHttpClient client, String prompt, AiCallback callback) {
        List<ModelChoice> pool = new ArrayList<>();
        for (Provider p : PROVIDERS) {
            for (String m : p.models) {
                pool.add(new ModelChoice(p, m));
            }
        }
        Collections.shuffle(pool);

        int attempts = Math.min(MAX_ATTEMPTS, pool.size());
        StringBuilder errorLog = new StringBuilder();

        for (int i = 0; i < attempts; i++) {
            ModelChoice choice = pool.get(i);
            try {
                String result = callModel(client, choice, prompt);
                if (result != null && result.trim().length() > 0) {
                    callback.onSuccess(choice.model, result.trim());
                    return;
                }
                errorLog.append(choice.model).append(": empty response\n");
            } catch (Exception e) {
                errorLog.append(choice.model).append(": ").append(e.getMessage()).append("\n");
            }
        }

        callback.onError(errorLog.toString());
    }

    private static String callModel(OkHttpClient client, ModelChoice choice, String prompt) throws IOException {
        JSONObject body = new JSONObject();
        try {
            body.put("model", choice.model);
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            body.put("messages", messages);
            body.put("stream", false);
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }

        RequestBody requestBody = RequestBody.create(body.toString(), JSON);
        Request request = new Request.Builder()
                .url(choice.provider.endpoint)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }

            String raw = response.body() != null ? response.body().string() : null;
            if (raw == null || raw.trim().length() == 0) {
                throw new IOException("empty body");
            }

            return parseContent(raw.trim());
        }
    }

    private static String parseContent(String raw) throws IOException {
        // some backends stream Server-Sent-Events even when "stream": false is requested
        if (raw.startsWith("data:")) {
            StringBuilder sb = new StringBuilder();
            for (String line : raw.split("\n")) {
                line = line.trim();
                if (!line.startsWith("data:")) continue;

                String data = line.substring(5).trim();
                if (data.length() == 0 || data.equals("[DONE]")) continue;

                try {
                    JSONObject chunk = new JSONObject(data);
                    JSONArray choices = chunk.optJSONArray("choices");
                    if (choices != null && choices.length() > 0) {
                        JSONObject delta = choices.getJSONObject(0).optJSONObject("delta");
                        if (delta != null) {
                            sb.append(delta.optString("content", ""));
                        }
                    }
                } catch (JSONException ignored) {
                }
            }

            if (sb.length() == 0) {
                throw new IOException("empty stream");
            }
            return sb.toString();
        }

        try {
            JSONObject json = new JSONObject(raw);

            if (json.has("error")) {
                Object err = json.get("error");
                throw new IOException(err.toString());
            }

            JSONArray choices = json.optJSONArray("choices");
            if (choices != null && choices.length() > 0) {
                JSONObject first = choices.getJSONObject(0);
                if (first.has("message")) {
                    return first.getJSONObject("message").optString("content", null);
                } else if (first.has("delta")) {
                    return first.getJSONObject("delta").optString("content", null);
                } else if (first.has("text")) {
                    return first.optString("text", null);
                }
            }

            throw new IOException("unrecognised response format");
        } catch (JSONException e) {
            throw new IOException("bad json: " + e.getMessage());
        }
    }
}
