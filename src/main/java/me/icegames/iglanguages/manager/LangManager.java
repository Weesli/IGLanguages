package me.icegames.iglanguages.manager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LangManager {
    private final JavaPlugin plugin;
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private final Map<UUID, String> playerLang = new HashMap<>();
    private final File playerFile;
    private YamlConfiguration playerConfig;
    private final String defaultLang = "pt_br";

    public LangManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerFile = new File(plugin.getDataFolder(), "languages.yml");
        this.playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void loadAll() {
        File langsFolder = new File(plugin.getDataFolder(), "langs");
        if (!langsFolder.exists()) langsFolder.mkdirs();

        for (File langDir : Objects.requireNonNull(langsFolder.listFiles(File::isDirectory))) {
            String langCode = langDir.getName().toLowerCase();
            Map<String, String> map = new HashMap<>();
            for (File file : Objects.requireNonNull(langDir.listFiles((f, n) -> n.endsWith(".yml")))) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                for (String key : cfg.getKeys(false)) {
                    ConfigurationSection sec = cfg.getConfigurationSection(key);
                    flattenSection(sec, key, map);
                }
            }
            translations.put(langCode, map);
        }
        loadPlayerLanguages();
    }

    private void flattenSection(ConfigurationSection section, String path, Map<String, String> map) {
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            String fullPath = path + "." + key;
            if (value instanceof ConfigurationSection) {
                flattenSection((ConfigurationSection) value, fullPath, map);
            } else {
                map.put(fullPath.toLowerCase(), value.toString());
            }
        }
    }

    public String getTranslation(UUID playerId, String key) {
        String lang = playerLang.getOrDefault(playerId, defaultLang);
        return translations.getOrDefault(lang, translations.get(defaultLang))
                .getOrDefault(key.toLowerCase(), "§c<?>§r");
    }

    public void setPlayerLang(UUID playerId, String lang) {
        playerLang.put(playerId, lang.toLowerCase());
    }

    public String getPlayerLang(UUID playerId) {
        return playerLang.getOrDefault(playerId, defaultLang);
    }

    private void loadPlayerLanguages() {
        if (playerConfig.isConfigurationSection("players")) {
            for (String key : playerConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String lang = playerConfig.getString("players." + key, defaultLang);
                    playerLang.put(uuid, lang);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void savePlayerLanguages() {
        playerConfig.set("players", null);
        for (Map.Entry<UUID, String> e : playerLang.entrySet()) {
            playerConfig.set("players." + e.getKey().toString(), e.getValue());
        }
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error on saving languages.yml");
        }
    }

    public Collection<Object> getTranslations() {
        return Collections.singleton(translations.values());
    }
}