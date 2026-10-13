# 🌸 Pollinations AI Extension for MIT App Inventor & Kodular

A powerful, plug-and-play extension (`.aix`) that brings **Pollinations.ai** text, image, and speech generation models directly into MIT App Inventor and Kodular projects.

[![Made with pollinations.ai](https://raw.githubusercontent.com/pollinations/pollinations/main/packages/ui/src/brand/badge-made-with.svg)](https://pollinations.ai/?ref=badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## ✨ Features

- 💬 **Text Generation**: Connect to frontier LLMs (`openai/gpt-5.4-nano`, `claude-haiku-5.5`, `mistral`, `deepseek`) with asynchronous callbacks.
- 🎨 **Image Generation**: Build instant 1024x1024 or custom aspect-ratio image URLs (`flux`, `turbo`, `sana`) without external webview wrappers.
- 🎙️ **Text-to-Speech**: Synthesize clear, natural AI voices directly with `openai/tts-1`.
- 🔑 **Bring Your Own Pollen (BYOP)**: Support user keys from [enter.pollinations.ai](https://enter.pollinations.ai) so your app users pay with their own Pollen without your key running dry!

---

## 📦 Downloads

- **Extension File**: [`dist/com.pollinations.ai.aix`](dist/com.pollinations.ai.aix)
- **Sample App Project**: [`sample/PollinationsSample.aia`](sample/PollinationsSample.aia)

---

## 🚀 Getting Started

### 1. Import Extension into App Inventor or Kodular
1. Open your project on [MIT App Inventor](https://ai2.appinventor.mit.edu/) or [Kodular Creator](https://creator.kodular.io/).
2. On the left palette, go to **Extension** > **Import extension**.
3. Choose `com.pollinations.ai.aix` and drag the **PollinationsAI** component onto your viewer.

### 2. Configure Properties
- `DefaultApiKey`: Your Pollinations API key (`sk_...`) or leave blank to pass dynamically from user input.
- `DefaultTextModel`: Default model for chat completions (default: `openai/gpt-5.4-nano`).
- `DefaultImageModel`: Default image model (default: `flux`).

---

## 🧱 Blocks Reference

### Methods
- **`call PollinationsAI.GenerateText(prompt, model, apiKey)`**
  Generates text asynchronously in the background. Triggers `when PollinationsAI.TextGenerated` on completion.
- **`call PollinationsAI.GetImageUrl(prompt, model, width, height, seed, apiKey)`**
  Returns a direct high-speed image stream URL suitable for setting as `Image.Picture`.
- **`call PollinationsAI.GetSpeechUrl(text, voice, apiKey)`**
  Returns an audio stream URL for playback with `Sound` or `Player`.

### Events
- **`when PollinationsAI.TextGenerated(text)`**
  Fired when text generation succeeds.
- **`when PollinationsAI.ErrorOccurred(functionName, errorMessage)`**
  Fired if an HTTP or network error occurs.

---

## 📄 License
MIT License. Created for the Pollinations Community Quest.
