package me.icegames.iglanguages.placeholder;

import me.icegames.iglanguages.manager.LangManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LangExpansion extends PlaceholderExpansion {
    private final LangManager langManager;

    public LangExpansion(LangManager langManager) {
        this.langManager = langManager;
    }

    @Override public String getIdentifier() { return "lang"; }
    @Override public String getAuthor()     { return "IceGames"; }
    @Override public String getVersion()    { return "1.0.0"; }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (params.equalsIgnoreCase("player")) {
            return langManager.getPlayerLang(p.getUniqueId());
        }
        return langManager.getTranslation(p.getUniqueId(), params);
    }
}