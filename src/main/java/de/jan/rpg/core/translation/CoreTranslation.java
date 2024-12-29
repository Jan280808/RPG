package de.jan.rpg.core.translation;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.translation.Translation;
import de.jan.rpg.core.Core;
import lombok.Getter;
import lombok.Synchronized;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
public class CoreTranslation implements Translation {

    private boolean isLoading = false;
    private final Map<Language, Map<String, Component>> translationMap;

    public CoreTranslation() {
        this.translationMap = new HashMap<>();
        loadTranslationCache();
    }

    @Override
    public Component getTranslation(Language language, String key) {
        Map<String, Component> translations = translationMap.get(language);
        if(translations.containsKey(key)) return translations.get(key);

        if(isLoading) return null;

        Component defaultComponent = ComponentSerializer.deserialize("<red>" + key);
        translationMap.forEach((lang, langTranslations) -> langTranslations.put(key, defaultComponent));

        try {
            updateJSONWithNewTranslation(key, key);
        } catch (Exception exception) {
            Core.LOGGER.error("translation {} cant not be added", key, exception);
        }

        return defaultComponent;
    }

    @Synchronized
    public void loadTranslationCache() {
        if(isLoading) return;
        isLoading = true;
        String filePath = "./plugins/core/translation.json";
        try {
            File file = new File(filePath);

            if(!file.exists()) {
                JSONObject defaultJson = new JSONObject();
                JSONObject languageJson = new JSONObject();
                Arrays.stream(Language.values()).forEach(language -> languageJson.put(language.name(), new JSONObject()));
                defaultJson.put("language", languageJson);
                file.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(defaultJson.toString(4));
                } catch (Exception exception) {
                    Core.LOGGER.error("could not create translation.json", exception);
                }
            }

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);

            if(jsonObject.has("language")) {
                JSONObject language = jsonObject.getJSONObject("language");
                Iterator<String> keys = language.keys();

                while(keys.hasNext()) {
                    String languageKey = keys.next();
                    JSONObject subLanguage = language.getJSONObject(languageKey);
                    Map<String, Component> translations = new HashMap<>();

                    flattenJSON(subLanguage, "", translations);

                    translationMap.put(Language.fromString(languageKey), translations);
                }
            }

        } catch (Exception exception) {
            Core.LOGGER.error("could not load translation.json", exception);
        }
        isLoading = false;
    }

    private void updateJSONWithNewTranslation(String key, String defaultValue) throws Exception {
        String filePath = "./plugins/core/translation.json";
        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        JSONObject jsonObject = new JSONObject(content);
        JSONObject languageObject = jsonObject.getJSONObject("language");

        for(String langKey : languageObject.keySet()) {
            JSONObject langObject = languageObject.getJSONObject(langKey);
            addNestedKey(langObject, key.split("\\."), defaultValue);
        }

        try(FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonObject.toString(4));
        }
    }

    private void addNestedKey(JSONObject jsonObject, String[] keys, String value) {
        JSONObject current = jsonObject;

        for(int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if(!current.has(key)) current.put(key, new JSONObject());
            current = current.getJSONObject(key);
        }

        String lastKey = keys[keys.length - 1];
        if(!current.has(lastKey)) current.put(lastKey, value);
    }

    private void flattenJSON(JSONObject jsonObject, String parentKey, Map<String, Component> translations) {
        for(String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;
            if(value instanceof JSONObject) flattenJSON((JSONObject) value, fullKey, translations);
            else if(value instanceof String) translations.put(fullKey, ComponentSerializer.deserialize((String) value));
        }
    }
}
