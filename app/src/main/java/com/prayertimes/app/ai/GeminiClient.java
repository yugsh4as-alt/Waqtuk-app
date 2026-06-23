package com.prayertimes.app.ai;

import android.os.Handler;
import android.os.Looper;

import com.prayertimes.app.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiClient {

    public interface Callback {
        void onResponse(String text);
        void onError(String error);
    }

    private static final String ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";

    private static final String SYSTEM_PROMPT =
        "أنت مساعد إسلامي متخصص في القرآن الكريم والسنة النبوية وعلوم الشريعة الإسلامية. " +
        "أجب دائماً بالعربية الفصحى. كن دقيقاً وأميناً في نقل المعلومات الشرعية. " +
        "إذا سُئلت عن آية قرآنية فاستشهد بها كاملة مع ذكر السورة والآية. " +
        "إذا لم تعرف إجابة سؤال فقل ذلك صراحةً ولا تخترع معلومات.";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient   httpClient;
    private final ExecutorService executor;
    private final Handler         mainHandler;

    public GeminiClient() {
        this.httpClient  = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        this.executor    = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void sendMessage(String userMessage, Callback callback) {
        executor.execute(() -> {
            try {
                String apiKey = BuildConfig.GEMINI_API_KEY;
                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                    mainHandler.post(() -> callback.onError(
                        "مفتاح API غير موجود. أضف GEMINI_API_KEY في ملف local.properties"));
                    return;
                }

                JSONObject systemPart = new JSONObject()
                    .put("text", SYSTEM_PROMPT);
                JSONObject systemContent = new JSONObject()
                    .put("role", "user")
                    .put("parts", new JSONArray().put(systemPart));

                JSONObject userPart = new JSONObject().put("text", userMessage);
                JSONObject userContent = new JSONObject()
                    .put("role", "user")
                    .put("parts", new JSONArray().put(userPart));

                JSONObject body = new JSONObject()
                    .put("contents", new JSONArray()
                        .put(systemContent)
                        .put(userContent))
                    .put("generationConfig", new JSONObject()
                        .put("temperature", 0.7)
                        .put("maxOutputTokens", 1024)
                        .put("topP", 0.9));

                Request request = new Request.Builder()
                    .url(ENDPOINT + "?key=" + apiKey)
                    .post(RequestBody.create(body.toString(), JSON))
                    .addHeader("Content-Type", "application/json")
                    .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        int code = response.code();
                        mainHandler.post(() -> callback.onError(
                            "خطأ في الاتصال بـ Gemini (كود: " + code + ")"));
                        return;
                    }

                    String responseBody = response.body() != null ? response.body().string() : "";
                    JSONObject json = new JSONObject(responseBody);
                    String text = json
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                    mainHandler.post(() -> callback.onResponse(text));
                }

            } catch (IOException e) {
                mainHandler.post(() -> callback.onError("خطأ في الشبكة: " + e.getMessage()));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("خطأ غير متوقع: " + e.getMessage()));
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
