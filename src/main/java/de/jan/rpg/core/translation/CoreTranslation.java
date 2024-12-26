package de.jan.rpg.core.translation;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.translation.Translation;
import de.jan.rpg.core.Core;
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
                    String key = keys.next();
                    JSONObject subLanguage = language.getJSONObject(key);
                    Map<String, Component> translations = new HashMap<>();

                    Iterator<String> subKeys = subLanguage.keys();

                    while(subKeys.hasNext()) {
                        String subKey = subKeys.next();
                        Component value = ComponentSerializer.deserialize(subLanguage.getString(subKey));
                        translations.put(subKey, value);
                    }
                    translationMap.put(Language.fromString(key), translations);
                }
            }

        } catch (Exception exception) {
            Core.LOGGER.error("could not load translation.json", exception);
        }
        Core.LOGGER.info("The following translations were loaded");
        translationMap.forEach((language, stringComponentMap) -> Core.LOGGER.info("Language: {}, size: {}", language.name(), stringComponentMap.size()));
        isLoading = false;
    }

    private void updateJSONWithNewTranslation(String key, String defaultValue) throws Exception {
        String filePath = "./plugins/core/translation.json";

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonObject = new JSONObject(content);

        JSONObject languageObject = jsonObject.getJSONObject("language");

        for(String langKey : languageObject.keySet()) {
            JSONObject langObject = languageObject.getJSONObject(langKey);
            if(!langObject.has(key)) langObject.put(key, defaultValue);
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonObject.toString(4));
        }
    }
}
