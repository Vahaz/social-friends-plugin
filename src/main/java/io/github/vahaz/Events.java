package io.github.vahaz;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        utils.createPlayerData(event.getPlayer());
        long unixTime = System.currentTimeMillis();
        utils.setDefaultValues(event.getPlayer());
        utils.setPlayerData(event.getPlayer(), "social.last_connected_date", String.valueOf(unixTime));
    }
}
