// src/main/java/me/icegames/iglanguages/command/LangCommand.java
package me.icegames.iglanguages.command;

import me.icegames.iglanguages.manager.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LangCommand implements CommandExecutor {

    private final LangManager langManager;

    public LangCommand(LangManager langManager) {
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player) && !sender.hasPermission("iglanguages.admin")) {
            sender.sendMessage("§cYou do not have permission to use this.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§eUse: /lang reload ou /lang set <jogador> <idioma>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            langManager.loadAll();
            sender.sendMessage("§aLanguages reloaded successfully!");

            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cJogador não encontrado.");
                    return true;
                }
                String lang = args[2];
                langManager.setPlayerLang(target.getUniqueId(), lang);
                sender.sendMessage("§aIdioma do jogador " + target.getName() + " definido como " + lang);
            } else {
                sender.sendMessage("§cUso correto: /lang set <jogador> <idioma>");
            }
            return true;
        }

        sender.sendMessage("§cSubcomando desconhecido.");
        return true;
    }
}