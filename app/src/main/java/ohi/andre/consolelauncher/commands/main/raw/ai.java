package ohi.andre.consolelauncher.commands.main.raw;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;
import ohi.andre.consolelauncher.commands.main.MainPack;
import ohi.andre.consolelauncher.managers.AppsManager;
import ohi.andre.consolelauncher.tuils.Tuils;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * "ai" command (also reachable with the ".ai" / ".AI" shortcut, see MainManager).
 *
 * Sends the given text to a random AI model from {@link #PRIMARY_MODELS}. If that model
 * fails (network error, non-2xx response, bad body, ...) it retries once with a different
 * random model from the same provider, and if that also fails it falls back to the
 * secondary provider ({@link #FALLBACK_URL}) before giving up.
 *
 * "ai open <app>" is handled as a special case: instead of contacting any AI backend,
 * Rootix tries to find and launch the named app directly.
 */
public class ai implements CommandAbstraction {

    private static final String PRIMARY_URL = "https://chatbot.codexapi.workers.dev/v1/chat/completions";
    private static final String FALLBACK_URL = "https://copilot-api-delta.vercel.app/v1/chat/completions";
    private static final String FALLBACK_MODEL = "copilot";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String[] PRIMARY_MODELS = new String[] {
            "gpt-5.2",
            "gpt-5.1",
            "gpt-5",
            "anthropic/claude-sonnet-4",
            "mercury-coder",
            "Olmo-3.1-32B-Instruct",
            "chatgpt-4o-latest",
            "google/gemini-2.5-pro-preview-05-06",
            "x-ai/grok-4",
            "deepseek-ai/deepseek-v3.2",
            "deepseek-ai/deepseek-v3.1-terminus",
            "deepseek-ai/deepseek-R1-0528",
            "o1-preview",
            "o3-mini",
            "qwen/qwen3.5-397b-a17b",
            "qwen/qwen3-coder-480b-a35b-instruct",
            "moonshotai/kimi-k2.5",
            "moonshotai/kimi-k2-thinking",
            "moonshotai/kimi-k2-instruct-0905",
            "openai/gpt-oss-120b",
            "openai/gpt-oss-20b",
            "meta/llama-3.1-405b-instruct",
            "meta/llama-4-maverick-17b-128e-instruct",
            "meta/llama-4-scout-17b-16e-instruct",
            "meta-llama-3.3-70b-instruct",
            "meta-llama-3.1-8b-instruct",
            "google/gemma-3-27b-it",
            "nvidia/nemotron-3-nano-30b-a3b",
            "qwen/qwq-32b",
            "qwen/qwen3-235b-a22b",
            "minimaxai/minimax-m2",
            "accounts/fireworks/models/glm-4p7",
            "meta-llama/Llama-3.1-8B-Instruct",
            "mistralai/mistral-large-3-675b-instruct-2512",
            "mistralai/magistral-small-2506",
            "mistralai/mistral-small-3.1-24b-instruct-2503",
            "mistralai/ministral-14b-instruct-2512"
    };

    private static final Pattern OPEN_APP_PATTERN = Pattern.compile("^open\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    private static final Random RANDOM = new Random();

    @Override
    public String exec(ExecutePack pack) throws Exception {
        MainPack info = (MainPack) pack;
        String text = pack.getString();

        if (text == null) {
            return info.context.getString(helpRes());
        }

        text = text.trim();
        if (text.length() == 0) {
            return info.context.getString(helpRes());
        }

        // "ai open <app>" -> try to launch the app directly, no AI call needed
        Matcher openMatcher = OPEN_APP_PATTERN.matcher(text);
        if (openMatcher.matches()) {
            String appName = openMatcher.group(1).trim();
            String openResult = tryOpenApp(info, appName);
            if (openResult != null) {
                return openResult.length() == 0 ? null : openResult;
            }
            //  no matching app found -> fall through and just ask the AI normally
        }

        if (!Tuils.hasInternetAccess()) {
            return info.context.getString(R.string.no_internet);
        }

        return queryAI(info, text);
    }

    /**
     * Tries to find an installed app whose label (or package name) matches {@code appName}
     * and launches it.
     *
     * @return "" if the app was found and launched, an error message if launching failed,
     *         or null if no matching app was found at all (caller should fall back to AI).
     */
    private String tryOpenApp(MainPack info, String appName) {
        if (appName.length() == 0) return null;

        AppsManager.LaunchInfo launchInfo = info.appsManager.findLaunchInfoWithLabel(appName, AppsManager.SHOWN_APPS);
        if (launchInfo == null) {
            launchInfo = info.appsManager.findLaunchInfoWithLabel(appName, AppsManager.HIDDEN_APPS);
        }
        if (launchInfo == null) return null;

        try {
            Intent intent = info.appsManager.getIntent(launchInfo);
            info.context.startActivity(intent);
            return Tuils.EMPTYSTRING;
        } catch (Exception e) {
            return info.context.getString(R.string.output_error) + Tuils.SPACE + e.getMessage();
        }
    }

    private String queryAI(MainPack info, String text) {
        //  1st try: random model on the primary provider
        String firstModel = randomModel(null);
        String result = callChatCompletions(info, PRIMARY_URL, firstModel, text);
        if (result != null) return result;

        //  2nd try: a *different* random model on the primary provider
        String secondModel = randomModel(firstModel);
        result = callChatCompletions(info, PRIMARY_URL, secondModel, text);
        if (result != null) return result;

        //  3rd try: fallback provider
        result = callChatCompletions(info, FALLBACK_URL, FALLBACK_MODEL, text);
        if (result != null) return result;

        return info.context.getString(R.string.output_error) + Tuils.SPACE + "all AI models failed to respond, try again";
    }

    private String randomModel(String excluding) {
        if (excluding == null) {
            return PRIMARY_MODELS[RANDOM.nextInt(PRIMARY_MODELS.length)];
        }

        String model;
        do {
            model = PRIMARY_MODELS[RANDOM.nextInt(PRIMARY_MODELS.length)];
        } while (model.equals(excluding) && PRIMARY_MODELS.length > 1);
        return model;
    }

    /**
     * @return the model's reply text, or null if this call failed for any reason
     *         (so the caller can move on to the next fallback).
     */
    private String callChatCompletions(MainPack info, String url, String model, String text) {
        try {
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", text);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject body = new JSONObject();
            body.put("model", model);
            body.put("messages", messages);
            body.put("stream", false);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = info.client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            String rawBody = response.body().string();
            JSONObject json = new JSONObject(rawBody);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.length() == 0) {
                return null;
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject choiceMessage = firstChoice.optJSONObject("message");
            if (choiceMessage == null) return null;

            String content = choiceMessage.optString("content", null);
            if (content == null || content.trim().length() == 0) return null;

            return content.trim();
        } catch (Exception e) {
            Tuils.log(e);
            return null;
        }
    }

    @Override
    public int[] argType() {
        return new int[]{CommandAbstraction.PLAIN_TEXT};
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_ai;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return ((MainPack) pack).context.getString(helpRes());
    }
}
