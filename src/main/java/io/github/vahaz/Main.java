package io.github.vahaz;

import io.github.vahaz.commands.Friends;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("[Social] Enabling social plugin");
        Objects.requireNonNull(this.getCommand("friends")).setExecutor(new Friends());
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[Social] Disabling social plugin");
    }

}
