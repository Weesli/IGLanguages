package me.icegames.iglanguages.listener;

import me.icegames.iglanguages.IGLanguages;
import me.icegames.iglanguages.manager.ActionsManager;
import me.icegames.iglanguages.manager.LangManager;
import me.icegames.iglanguages.util.LangEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final IGLanguages plugin;
    private final LangManager langManager;
    private final ActionsManager actionsManager;

    public PlayerJoinListener(LangManager langManager, ActionsManager actionsManager, IGLanguages plugin) {
        this.langManager = langManager;
        this.actionsManager = actionsManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!langManager.hasPlayerLang(uuid)) {
            plugin.LogDebug("Player " + player.getName() + " has no language set, setting default language.");
            String playerLocale = player.getLocale();
            String selectedLang = plugin.getConfig().getString("defaultLang");
            plugin.LogDebug("Player locale: " + playerLocale);
            if (playerLocale.isEmpty()) {
                playerLocale = plugin.getConfig().getString("defaultLang");
            }
            if (LangEnum.isValidCode(playerLocale)) {
                File langsFolder = new File(plugin.getDataFolder(), "langs");
                File langFolder = new File(langsFolder, playerLocale);
                if (langFolder.exists()) {
                    selectedLang = playerLocale;
                }
            }

            selectedLang = selectedLang.toLowerCase();
            plugin.LogDebug("Selected language: " + selectedLang);
            langManager.setPlayerLang(uuid, selectedLang);
            langManager.savePlayerLang(uuid);
            actionsManager.executeActionsPath(player, "firstJoinActions");
        } else {
            String playerLang = langManager.getPlayerLang(uuid);
            plugin.LogDebug("Player " + player.getName() + " language is: " + playerLang);
        }
    }
}
