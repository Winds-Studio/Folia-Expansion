/*
 *
 * Folia-Expansion
 * Copyright (C) 2024 Dreeam__
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package org.dreeam.expansion.folia;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.threadedregions.commands.CommandUtil;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class FoliaExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

    private FoliaUtils foliaUtils = null;

    private final Cache<String, Integer> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public void clear() {
        foliaUtils = null;
        cache.invalidateAll();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "folia";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dreeam__";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.putIfAbsent("tps_color.high", "&a");
        defaults.putIfAbsent("tps_color.medium", "&e");
        defaults.putIfAbsent("tps_color.low", "&c");
        return defaults;
    }

    private @Nullable String getCached(String key, Callable<Integer> callable) {
        try {
            return String.valueOf(cache.get(key, callable));
        } catch (ExecutionException e) {
            if (getPlaceholderAPI().getPlaceholderAPIConfig().isDebugMode()) {
                getPlaceholderAPI().getLogger().log(Level.SEVERE, "[server] Could not access cache key " + key, e);
            }
            return "";
        }
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String identifier) {
        if (foliaUtils == null) {
            foliaUtils = new FoliaUtils();
            foliaUtils.checkFolia();
        }

        if (!foliaUtils.isFolia) return null;

        if (p == null | !p.isOnline()) return "";
        Player player = p.getPlayer();
        if (player == null) return "";

        switch (identifier) {
            case "global_tps":
                return getFoliaGlobalTPS(null);
            case "global_mspt":
                return getFoliaGlobalMSPT(null);
            case "global_util":
                return getFoliaGlobalUtil(null);
            case "tps":
                return getFoliaTPS(null, player.getLocation());
            case "mspt":
                return getFoliaMSPT(null, player.getLocation());
            case "util":
                return getFoliaUtil(null, player.getLocation());
        }

        if (identifier.startsWith("global_tps_")) {
            identifier = identifier.replace("global_tps_", "");

            return getFoliaGlobalTPS(identifier);
        }

        if (identifier.startsWith("global_mspt_")) {
            identifier = identifier.replace("global_mspt_", "");

            return getFoliaGlobalMSPT(identifier);
        }

        if (identifier.startsWith("global_util_")) {
            identifier = identifier.replace("global_util_", "");

            return getFoliaGlobalUtil(identifier);
        }

        if (identifier.startsWith("tps_")) {
            identifier = identifier.replace("tps_", "");

            return getFoliaTPS(identifier, p.getPlayer().getLocation());
        }

        if (identifier.startsWith("mspt_")) {
            identifier = identifier.replace("mspt_", "");

            return getFoliaMSPT(identifier, player.getLocation());
        }

        if (identifier.startsWith("util_")) {
            identifier = identifier.replace("util_", "");

            return getFoliaUtil(identifier, player.getLocation());
        }

        return null;
    }

    public String getFoliaGlobalTPS(String arg) {
        if (arg == null || arg.isEmpty()) {
            StringJoiner joiner = new StringJoiner(toLegacy(Component.text(", ", NamedTextColor.GRAY)));
            for (double tps : foliaUtils.getGlobalTPS()) {
                joiner.add(getColoredTPS(tps));
            }
            return joiner.toString();
        }
        return switch (arg) {
            case "5s" -> fixTPS(foliaUtils.getGlobalTPS().get(0));
            case "15s" -> fixTPS(foliaUtils.getGlobalTPS().get(1));
            case "1m" -> fixTPS(foliaUtils.getGlobalTPS().get(2));
            case "5m" -> fixTPS(foliaUtils.getGlobalTPS().get(3));
            case "15m" -> fixTPS(foliaUtils.getGlobalTPS().get(4));
            case "5s_colored" -> getColoredTPS(foliaUtils.getGlobalTPS().get(0));
            case "15s_colored" -> getColoredTPS(foliaUtils.getGlobalTPS().get(1));
            case "1m_colored" -> getColoredTPS(foliaUtils.getGlobalTPS().get(2));
            case "5m_colored" -> getColoredTPS(foliaUtils.getGlobalTPS().get(3));
            case "15m_colored" -> getColoredTPS(foliaUtils.getGlobalTPS().get(4));
            default -> null;
        };
    }

    public String getFoliaGlobalMSPT(String arg) {
        if (arg == null || arg.isEmpty()) {
            StringJoiner joiner = new StringJoiner(toLegacy(Component.text(", ", NamedTextColor.GRAY)));
            for (double mspt : foliaUtils.getGlobalMSPT()) {
                joiner.add(getColoredMSPT(mspt));
            }
            return joiner.toString();
        }
        return switch (arg) {
            case "5s" -> fixMSPT(foliaUtils.getGlobalMSPT().get(0));
            case "15s" -> fixMSPT(foliaUtils.getGlobalMSPT().get(1));
            case "1m" -> fixMSPT(foliaUtils.getGlobalMSPT().get(2));
            case "5m" -> fixMSPT(foliaUtils.getGlobalMSPT().get(3));
            case "15m" -> fixMSPT(foliaUtils.getGlobalMSPT().get(4));
            case "5s_colored" -> getColoredMSPT(foliaUtils.getGlobalMSPT().get(0));
            case "15s_colored" -> getColoredMSPT(foliaUtils.getGlobalMSPT().get(1));
            case "1m_colored" -> getColoredMSPT(foliaUtils.getGlobalMSPT().get(2));
            case "5m_colored" -> getColoredMSPT(foliaUtils.getGlobalMSPT().get(3));
            case "15m_colored" -> getColoredMSPT(foliaUtils.getGlobalMSPT().get(4));
            default -> null;
        };
    }

    public String getFoliaGlobalUtil(String arg) {
        if (arg == null || arg.isEmpty()) {
            return fixUtil(foliaUtils.getGlobalUtil());
        } else if (arg.equals("colored")) {
            return getColoredUtil(foliaUtils.getGlobalUtil());
        } else {
            return null;
        }
    }

    public String getFoliaTPS(String arg, Location location) {
        if (arg == null || arg.isEmpty()) {
            StringJoiner joiner = new StringJoiner(toLegacy(Component.text(", ", NamedTextColor.GRAY)));
            for (double tps : foliaUtils.getTPS(location)) {
                joiner.add(getColoredTPS(tps));
            }
            return joiner.toString();
        }
        return switch (arg) {
            case "5s" -> fixTPS(foliaUtils.getTPS(location).get(0));
            case "15s" -> fixTPS(foliaUtils.getTPS(location).get(1));
            case "1m" -> fixTPS(foliaUtils.getTPS(location).get(2));
            case "5m" -> fixTPS(foliaUtils.getTPS(location).get(3));
            case "15m" -> fixTPS(foliaUtils.getTPS(location).get(4));
            case "5s_colored" -> getColoredTPS(foliaUtils.getTPS(location).get(0));
            case "15s_colored" -> getColoredTPS(foliaUtils.getTPS(location).get(1));
            case "1m_colored" -> getColoredTPS(foliaUtils.getTPS(location).get(2));
            case "5m_colored" -> getColoredTPS(foliaUtils.getTPS(location).get(3));
            case "15m_colored" -> getColoredTPS(foliaUtils.getTPS(location).get(4));
            default -> null;
        };
    }

    public String getFoliaMSPT(String arg, Location location) {
        if (arg == null || arg.isEmpty()) {
            StringJoiner joiner = new StringJoiner(toLegacy(Component.text(", ", NamedTextColor.GRAY)));
            for (double mspt : foliaUtils.getMSPT(location)) {
                joiner.add(getColoredMSPT(mspt));
            }
            return joiner.toString();
        }
        return switch (arg) {
            case "5s" -> fixMSPT(foliaUtils.getMSPT(location).get(0));
            case "15s" -> fixMSPT(foliaUtils.getMSPT(location).get(1));
            case "1m" -> fixMSPT(foliaUtils.getMSPT(location).get(2));
            case "5m" -> fixMSPT(foliaUtils.getMSPT(location).get(3));
            case "15m" -> fixMSPT(foliaUtils.getMSPT(location).get(4));
            case "5s_colored" -> getColoredMSPT(foliaUtils.getMSPT(location).get(0));
            case "15s_colored" -> getColoredMSPT(foliaUtils.getMSPT(location).get(1));
            case "1m_colored" -> getColoredMSPT(foliaUtils.getMSPT(location).get(2));
            case "5m_colored" -> getColoredMSPT(foliaUtils.getMSPT(location).get(3));
            case "15m_colored" -> getColoredMSPT(foliaUtils.getMSPT(location).get(4));
            default -> null;
        };
    }

    public String getFoliaUtil(String arg, Location location) {
        if (arg == null || arg.isEmpty()) {
            return fixUtil(foliaUtils.getUtil(location).get(0));
        } else if (arg.equals("colored")) {
            return getColoredUtil(foliaUtils.getUtil(location).get(0));
        } else {
            return null;
        }
    }

    private String toLegacy(Component component) {
        // Convert adventure's hex color to legacy string, and replace color code for PAPI to handle
        return LegacyComponentSerializer.legacyAmpersand().serialize(component).replaceAll("&", "§");
    }

    // start - Round and colorize TPS/mspt
    private String fixTPS(double tps) {
        String finalTPS = String.format("%.2f", tps);

        return (tps > 20.00 ? "*" : "") + finalTPS;
    }

    private String getColoredTPS(double tps) {
        return toLegacy(Component.text(fixTPS(tps), CommandUtil.getColourForTPS(tps)));
    }

    private String getColoredTPSPercent(double tps) {
        return toLegacy(Component.text(getPercent(tps), CommandUtil.getColourForTPS(tps)));
    }

    private String getPercent(double tps) {
        double finalPercent = Math.min(Math.round(100 / 20.00 * tps), 100.0);

        return (tps > 20.0 ? "*" : "") + finalPercent + "%";
    }

    private String fixMSPT(double mspt) {
        return String.format("%.2f", mspt);
    }

    private String getColoredMSPT(double mspt) {
        return toLegacy(Component.text(fixMSPT(mspt), CommandUtil.getColourForMSPT(mspt)));
    }

    private String fixUtil(double util) {
        return String.format("%.2f", util * 100.0);
    }

    private String getColoredUtil(double util) {
        return toLegacy(Component.text(fixUtil(util), CommandUtil.getUtilisationColourRegion(util / foliaUtils.maxThreadsCount())));
    }
    // end
}
