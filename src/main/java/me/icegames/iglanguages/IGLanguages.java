package me.icegames.iglanguages;

import me.icegames.iglanguages.command.LangCommand;
import me.icegames.iglanguages.manager.LangManager;
import me.icegames.iglanguages.placeholder.LangExpansion;
import me.icegames.iglanguages.listener.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class IGLanguages extends JavaPlugin {

    private LangManager langManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.langManager = new LangManager(this);
        langManager.loadAll();
        getLogger().info("Loaded all languages!");
        getLogger().info("Loaded " + langManager.getTranslations().size() + " languages!");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LangExpansion(langManager).register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            getLogger().warning("Disabling IGLanguages...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("lang").setExecutor(new LangCommand(langManager));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(langManager), this);
    }

    @Override
    public void onDisable() {
        langManager.savePlayerLanguages();
    }
}
