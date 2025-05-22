package me.icegames.iglanguages.manager;

import me.icegames.iglanguages.IGLanguages;
import me.icegames.iglanguages.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public class ActionsManager {
    private final IGLanguages plugin;

    public ActionsManager(IGLanguages plugin) {
        this.plugin = plugin;
    }

    public void executeActionsOnSet(Player player, String lang) {
        List<String> actions = plugin.getConfig().getStringList("actionsOnSet." + lang.toLowerCase());
        for (String action : actions) {
            executeAction(action, player);
        }
    }

    public void executeActionsPath(Player player, String path) {
        List<String> actions = plugin.getConfig().getStringList(path);
        for (String action : actions) {
            executeAction(action, player);
        }
    }

    private void executeAction(String action, Player player) {
        String type;
        String data;
        int idx = action.indexOf(':');
        if (idx == -1) return;
        type = action.substring(0, idx).trim().toLowerCase();
        data = action.substring(idx + 1).trim();

        if (data.contains("%player%")) {
            data = data.replace("%player%", player.getName());
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            data = PlaceholderAPI.setPlaceholders(player, data);
        }
        switch (type) {
            case "message":
                String msg = data.replace("&", "§");
                player.sendMessage(msg);
                break;
            case "playsound":
                String[] parts = data.split(";");
                try {
                    Sound sound = Sound.valueOf(parts[0]);
                    float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                    float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                    player.playSound(player.getLocation(), sound, volume, pitch);
                } catch (Exception ignored) {}
                break;
            case "playsound_resource_pack":
                processResourceSound(data, player);
            case "console_command":
                String cmd = data;
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, cmd);
                break;
            case "centered_message":
                StringUtil.centeredMessage(data.replace("%player%", player.getName()));
                break;
            case "title":
                showTitle(player, data);
            default:
                break;
        }
    }

    private void showTitle(Player player, String data) {
        String[] parts = data.split(";");
        if (parts.length < 3) return;

        String title = ChatColor.translateAlternateColorCodes('&', parts[0]);
        String subtitle = ChatColor.translateAlternateColorCodes('&', parts[1]);

        player.sendTitle(title, subtitle);
    }

    private void processResourceSound(String content, Player player) {
        String[] parts = content.split(";");

        if (parts.length >= 3) {
            try {
                String sound = parts[0];
                float volume = Float.parseFloat(parts[1]);
                float pitch = Float.parseFloat(parts[2]);

                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
