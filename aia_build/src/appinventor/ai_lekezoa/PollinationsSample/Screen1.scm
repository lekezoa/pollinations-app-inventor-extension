# JSON-formatted Scheme file for App Inventor Screen1
{
  "Version": 32,
  "Properties": {
    "$Name": "Screen1",
    "$Type": "Form",
    "$Version": "32",
    "AppName": "Pollinations AI Demo",
    "Title": "Pollinations AI Demo",
    "$Components": [
      {
        "$Name": "PollinationsAI1",
        "$Type": "com.pollinations.ai.PollinationsAI",
        "$Version": "1",
        "DefaultTextModel": "openai/gpt-5.4-nano",
        "DefaultImageModel": "flux"
      },
      {
        "$Name": "TextBoxPrompt",
        "$Type": "TextBox",
        "$Version": "6",
        "Hint": "Enter prompt here..."
      },
      {
        "$Name": "ButtonGenerate",
        "$Type": "Button",
        "$Version": "7",
        "Text": "Generate with Pollinations"
      },
      {
        "$Name": "LabelResult",
        "$Type": "Label",
        "$Version": "5",
        "Text": "Result will appear here"
      }
    ]
  }
}
