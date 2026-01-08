package dev.iamgabriel.DiscordMinecraftOnlinePlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.purpurmc.purpur.event.PlayerAFKEvent;

import static org.bukkit.Bukkit.getScheduler;

public class PurpurAFKListener implements Listener {
    private final Plugin plugin;

    public PurpurAFKListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAFK(PlayerAFKEvent event) {
        // Wait for 2 ticks to allow Purpur AFK to update name
        getScheduler().runTaskLater(this.plugin, () -> plugin.updateMessage(null), 2);
    }
}
