package io.github.vahaz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private final File folder = new File(Main.getInstance().getDataFolder(), "player_data" + File.separator);

    /**
     * Send a message to a player using Bundle feature.
     * @param   player   Receiver/Sender
     * @param   mention   Target Player
     * @param   soundType   Sound "error" or "success"
     * @param   bundleKey   Translation key
     */
    public void sendMessageToPlayer(@NotNull Player player, @NotNull String bundleKey, @Nullable String soundType, @Nullable Player mention) {
        ResourceBundle bundle = ResourceBundle.getBundle("language", Locale.of(player.getLocale()));
        String bundleString = bundle.containsKey(bundleKey) ? bundle.getString(bundleKey) : "";
        bundleString = bundleString.replace("%player%", player.getDisplayName());

        if (mention != null) {
            if (mention.isOnline()) bundleString = bundleString.replace("%mention%", mention.getDisplayName());
            else bundleString = bundleString.replace("%mention%", Objects.requireNonNull(Bukkit.getOfflinePlayer(mention.getUniqueId()).getName()));
        }
        if (soundType != null) {
            if (soundType.equalsIgnoreCase("error")) { player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F); }
            if (soundType.equalsIgnoreCase("success")) { player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F); }
        }
        if (bundleString.contains("%last_connected_date%") && mention != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            String date = dateFormat.format(new Date(Long.parseLong((String) getPlayerData(mention, "social.last_connected_date"))));
            bundleString = bundleString.replace("%last_connected_date%", date);
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', bundleString));
    }

    /* -- Constructors -- */

    /**
     * Create a {@code .yml file} named with player's {@code UUID} in {@code player_data folder}.
     * @param   player  Player
     */
    public void createPlayerData(@NotNull Player player) {
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Bukkit.getLogger().warning("[SOCIAL] Warning : Can not open 'player_data' folder");
                return;
            }
            Bukkit.getLogger().info("[SOCIAL] Info : 'player_data' folder do not exist/found");
            return;
        }

        File file = new File(folder, player.getUniqueId() + ".yml");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) { Bukkit.getLogger().warning("[SOCIAL] Warning : player's data file can not be created"); }
            } catch (Exception exception) {
                Bukkit.getLogger().warning("[SOCIAL] Warning : player data file can not be created (" + exception + ")");
            }
        }
    }

    public void setDefaultValues(@NotNull Player player) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (yml.getStringList("social.request").isEmpty()) yml.set("social.request", new ArrayList<>());
        if (yml.getStringList("social.friends").isEmpty()) yml.set("social.friends", new ArrayList<>());
        try { yml.save(file); }
        catch (Exception exception) { Bukkit.getLogger().warning("[SOCIAL] Warning : can not save player's data (" + exception + ")"); }
    }

    /**
     * Return a value from a key, from player's config (yml).
     * <p> Can return a {@code List<String>} or a {@code String}.
     * @param   player  Player
     * @param   key   Key
     */
    public Object getPlayerData(@NotNull Player player, @NotNull String key) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.isSet(key)) { return null; }
        return yml.isList(key) ? yml.getStringList(key) : yml.get(key);
    }

    /**
     * Set a value for a key, in player's config (yml).
     * <p> {@code Throw} exception if the file can not be saved.
     * @param   player  Player
     * @param   key   Key
     * @param   value Value
     */
    public void setPlayerData(@NotNull Player player, @NotNull String key, @NotNull String value) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set(key, value);
        try { yml.save(file);}
        catch (Exception exception) { Bukkit.getLogger().warning("[SOCIAL] Warning : can not save player's data (" + exception + ")"); }
    }

    /**
     * Add a value to a list, in player's config (yml).
     * @param   player   Player
     * @param   key   Key
     * @param   value Value
     */
    public void addPlayerData(@NotNull Player player, @NotNull String key, @NotNull String value) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if(!yml.isSet(key) || !yml.isList(key)) yml.set(key, new ArrayList<>());
        List<String> list = yml.getStringList(key);
        list.add(value);
        yml.set(key, list);
        try { yml.save(file); }
        catch (Exception exception) { Bukkit.getLogger().warning("[SOCIAL] Warning : can not save player's data (" + exception + ")"); }
    }

    /**
     * Remove a value from a list, from player's config (yml).
     * @param   player   Player
     * @param   key   Key
     * @param   value Value
     */
    public void removePlayerData(@NotNull Player player, @NotNull String key, @NotNull String value) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if(!yml.isList(key)) yml.set(key, new ArrayList<>());
        List<String> list = yml.getStringList(key);
        list.remove(value);
        yml.set(key, list);
        try { yml.save(file);}
        catch (Exception exception) { Bukkit.getLogger().warning("[SOCIAL] Warning : can not save player's data (" + exception + ")"); }
    }

    /**
     * Return if a list contains a value, in player's data file (yml).
     * <p>Return true of false depending on the result, if the list is empty or non-existent it returns false.
     * @param   player   Player
     * @param   key   Key
     * @param   value Value
     */
    public boolean isContainedPlayerData(@NotNull Player player, @NotNull String key, @NotNull String value) {
        File file = new File(folder, player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.isList(key) || !yml.isSet(key)) { return false; }
        return yml.getStringList(key).contains(value);
    }
}
