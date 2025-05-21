package me.icegames.iglanguages.listener;

import me.icegames.iglanguages.manager.LangManager;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String lang = langManager.getPlayerLang(uuid);

        // Se o jogador não tem linguagem definida (usa o default), pede para escolher
        if (lang == null || lang.equalsIgnoreCase("pt_br")) {
            player.sendMessage("§eBem-vindo! Por favor, selecione seu idioma:");
            player.sendMessage("§aDigite: /lang set <seu_nome> <idioma>");
            player.sendMessage("§eExemplo: /lang set " + player.getName() + " en_us");
            // Aqui você pode melhorar para abrir um menu ou usar mensagens clicáveis
        }
    }
}