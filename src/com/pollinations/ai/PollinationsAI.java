package com.pollinations.ai;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.AsynchUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@DesignerComponent(
    version = 1,
    description = "Pollinations.ai integration extension for MIT App Inventor & Kodular. Generate text, images, and speech with Bring Your Own Pollen (BYOP) support.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "https://raw.githubusercontent.com/pollinations/pollinations/main/packages/ui/src/brand/favicon-32.png"
)
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET")
public class PollinationsAI extends AndroidNonvisibleComponent {

    private final Activity activity;
    private final Handler handler;
    private String defaultApiKey = "";
    private String defaultTextModel = "openai/gpt-5.4-nano";
    private String defaultImageModel = "flux";

    public PollinationsAI(ComponentContainer container) {
        super(container.$form());
        this.activity = container.$context();
        this.handler = new Handler(Looper.getMainLooper());
    }

    @DesignerProperty(editorType = "string", defaultValue = "")
    @SimpleProperty(description = "Default secret or BYOP API key from enter.pollinations.ai")
    public void DefaultApiKey(String key) {
        this.defaultApiKey = key;
    }

    @SimpleProperty(category = PropertyCategory.BEHAVIOR)
    public String DefaultApiKey() {
        return this.defaultApiKey;
    }

    @DesignerProperty(editorType = "string", defaultValue = "openai/gpt-5.4-nano")
    @SimpleProperty(description = "Default text model for completions")
    public void DefaultTextModel(String model) {
        this.defaultTextModel = model;
    }

    @SimpleProperty(category = PropertyCategory.BEHAVIOR)
    public String DefaultTextModel() {
        return this.defaultTextModel;
    }

    @DesignerProperty(editorType = "string", defaultValue = "flux")
    @SimpleProperty(description = "Default image model for generation")
    public void DefaultImageModel(String model) {
        this.defaultImageModel = model;
    }

    @SimpleProperty(category = PropertyCategory.BEHAVIOR)
    public String DefaultImageModel() {
        return this.defaultImageModel;
    }

    /**
     * Generate text asynchronously using Pollinations Chat Completions.
     */
    @SimpleFunction(description = "Generate text from a prompt using Pollinations AI.")
    public void GenerateText(final String prompt, final String model, final String apiKey) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    String activeModel = (model == null || model.trim().isEmpty()) ? defaultTextModel : model.trim();
                    String activeKey = (apiKey == null || apiKey.trim().isEmpty()) ? defaultApiKey : apiKey.trim();

                    URL url = new URL("https://gen.pollinations.ai/v1/chat/completions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    if (!activeKey.isEmpty()) {
                        conn.setRequestProperty("Authorization", "Bearer " + activeKey);
                    }
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);

                    String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
                    String jsonInput = "{\"model\":\"" + activeModel + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}]}";

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonInput.getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    if (code == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        // Simple JSON parsing for content
                        String resStr = response.toString();
                        String marker = "\"content\":\"";
                        int start = resStr.indexOf(marker);
                        final String output;
                        if (start != -1) {
                            start += marker.length();
                            int end = resStr.indexOf("\"", start);
                            output = (end != -1) ? resStr.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"") : resStr;
                        } else {
                            output = resStr;
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextGenerated(output);
                            }
                        });
                    } else {
                        final String errorMsg = "HTTP Error " + code + ": " + conn.getResponseMessage();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ErrorOccurred("GenerateText", errorMsg);
                            }
                        });
                    }
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ErrorOccurred("GenerateText", e.getMessage() != null ? e.getMessage() : e.toString());
                        }
                    });
                }
            }
        });
    }

    /**
     * Get direct Image URL from prompt and parameters.
     */
    @SimpleFunction(description = "Build a Pollinations image generation URL with custom dimensions and seed.")
    public String GetImageUrl(String prompt, String model, int width, int height, int seed, String apiKey) {
        try {
            String activeModel = (model == null || model.trim().isEmpty()) ? defaultImageModel : model.trim();
            String activeKey = (apiKey == null || apiKey.trim().isEmpty()) ? defaultApiKey : apiKey.trim();
            int w = width > 0 ? width : 1024;
            int h = height > 0 ? height : 1024;

            String encodedPrompt = URLEncoder.encode(prompt, "UTF-8");
            StringBuilder sb = new StringBuilder("https://gen.pollinations.ai/image/");
            sb.append(encodedPrompt).append("?");
            sb.append("model=").append(activeModel);
            sb.append("&width=").append(w);
            sb.append("&height=").append(h);
            sb.append("&nologo=true&nofeed=true&private=true");
            if (seed > 0) {
                sb.append("&seed=").append(seed);
            }
            if (!activeKey.isEmpty()) {
                sb.append("&key=").append(activeKey);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get Text-to-Speech audio URL.
     */
    @SimpleFunction(description = "Get a Text-to-Speech audio stream URL using Pollinations.")
    public String GetSpeechUrl(String text, String voice, String apiKey) {
        try {
            String activeVoice = (voice == null || voice.trim().isEmpty()) ? "alloy" : voice.trim();
            String activeKey = (apiKey == null || apiKey.trim().isEmpty()) ? defaultApiKey : apiKey.trim();
            String encodedText = URLEncoder.encode(text, "UTF-8");

            StringBuilder sb = new StringBuilder("https://gen.pollinations.ai/audio/");
            sb.append(encodedText).append("?model=openai/tts-1&voice=").append(activeVoice);
            if (!activeKey.isEmpty()) {
                sb.append("&key=").append(activeKey);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @SimpleEvent(description = "Event fired when text generation completes successfully.")
    public void TextGenerated(String text) {
        EventDispatcher.dispatchEvent(this, "TextGenerated", text);
    }

    @SimpleEvent(description = "Event fired when an error occurs during an API call.")
    public void ErrorOccurred(String functionName, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", functionName, errorMessage);
    }
}
