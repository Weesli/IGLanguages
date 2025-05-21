package me.icegames.iglanguages.listener;

import me.icegames.iglanguages.manager.LangManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final LangManager langManager;

    public PlayerJoinListener(LangManager langManager) {
        this.langManager = langManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // Garante default ao entrar
        langManager.setPlayerLang(uuid, langManager.getPlayerLang(uuid));
    }
}

//public class PlayerJoinListener implements org.bukkit.event.Listener {
//    private final me.icegames.iglanguages.manager.LangManager langManager;
//
//    public PlayerJoinListener(me.icegames.iglanguages.manager.LangManager langManager) {
//        this.langManager = langManager;
//    }
//
//    @org.bukkit.event.EventHandler
//    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
//        org.bukkit.entity.Player player = event.getPlayer();
//        String lang = langManager.getPlayerLang(player.getUniqueId());
//        player.sendMessage(langManager.getTranslation(player.getUniqueId(), "welcome.message"));
//    }
//}