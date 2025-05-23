// src/main/java/me/icegames/iglanguages/command/LangCommand.java
package me.icegames.iglanguages.command;

import me.icegames.iglanguages.manager.ActionsManager;
import me.icegames.iglanguages.manager.LangManager;
import me.icegames.iglanguages.IGLanguages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class LangCommand implements CommandExecutor {

    private final LangManager langManager;
    private final ActionsManager actionsManager;
    private final FileConfiguration messageConfig;
    private final IGLanguages plugin;

    public LangCommand(LangManager langManager, FileConfiguration messageConfig, ActionsManager actionsManager, IGLanguages plugin) {
        this.langManager = langManager;
        this.messageConfig = messageConfig;
        this.actionsManager = actionsManager;
        this.plugin = plugin;
    }

    private String getMessage(String path, String... placeholders) {
        Object messageObj = messageConfig.get(path);
        String message;
        String finalMessage;
        String prefix = messageConfig.getString("prefix");

        if (messageObj instanceof String) {
            message = (String) messageObj;
        } else if (messageObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> messageList = (List<String>) messageObj;
            message = String.join("\n", messageList);
        } else {
            message = "&cMessage '" + path + "' not found in messages.yml.";
            return message;
        }

        for (int i = 0; i < placeholders.length; i += 2) {
            String key = placeholders[i];
            String value = (i + 1 < placeholders.length && placeholders[i + 1] != null) ? placeholders[i + 1] : "";
            message = message.replace(key, value);
        }

        finalMessage = prefix + message;
        return finalMessage.replace("&", "§");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player) && !sender.hasPermission("iglanguages.admin")) {
            sender.sendMessage(getMessage("no_permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Object helpObj = messageConfig.get("help");
            if (helpObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> helpList = (List<String>) helpObj;
                for (String line : helpList) {
                    sender.sendMessage(line.replace("&", "§"));
                }
            } else if (helpObj instanceof String) {
                sender.sendMessage(((String) helpObj).replace("&", "§"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            langManager.loadAll();
            langManager.clearCache();
            String consolePrefix = "\u001B[1;30m[\u001B[0m\u001B[36mI\u001B[1;36mG\u001B[0m\u001B[1;37m" + "Languages" + "\u001B[1;30m]\u001B[0m ";
            System.out.println(consolePrefix + "Reloaded " + langManager.getAvailableLangs().size() + " languages! " + langManager.getAvailableLangs());
            System.out.println(consolePrefix + "Reloaded " + langManager.getTotalTranslationsCount() + " total translations!");
            sender.sendMessage(getMessage("reload_success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(getMessage("player_not_found"));
                    return true;
                }
                String lang = args[2];
                List<String> availableLangs = langManager.getAvailableLangs();
                if (!availableLangs.contains(lang)) {
                    sender.sendMessage(getMessage("invalid_lang", "{lang}", lang, "{langs}", String.join(", ", availableLangs)));
                    return true;
                }
                langManager.setPlayerLang(target.getUniqueId(), lang);
                langManager.savePlayerLang(target.getUniqueId());
                sender.sendMessage(getMessage("set_success", "{player}", target.getName(), "{lang}", lang));
                actionsManager.executeActionsOnSet(target, lang);
            } else {
                sender.sendMessage(getMessage("set_usage"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(getMessage("player_not_found"));
                    return true;
                }
                String lang = langManager.getPlayerLang(target.getUniqueId());
                sender.sendMessage(getMessage("get_success", "{player}", target.getName(), "{lang}", lang));
            } else {
                sender.sendMessage(getMessage("get_usage"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<String> langs = langManager.getAvailableLangs();
            sender.sendMessage(getMessage("list_languages", "{langs}", String.join(", ", langs)));
            return true;
        }

        sender.sendMessage(getMessage("unknown_subcommand"));
        return true;
    }
}
