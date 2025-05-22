package me.icegames.iglanguages.listener;

import me.icegames.iglanguages.IGLanguages;
import me.icegames.iglanguages.manager.ActionsManager;
import me.icegames.iglanguages.manager.LangManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            langManager.setPlayerLang(uuid, plugin.getConfig().getString("defaultLang"));
            langManager.savePlayerLang(uuid);
            // Só executa se realmente for o primeiro join (não existe no players.yml)
            actionsManager.executeActionsPath(player, "firstJoinActions");
        }
        // Não executa firstJoinActions se já existir no players.yml
    }
}
