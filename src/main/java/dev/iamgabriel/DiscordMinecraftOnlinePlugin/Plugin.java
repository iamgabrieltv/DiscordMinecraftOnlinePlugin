package dev.iamgabriel.DiscordMinecraftOnlinePlugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class Plugin extends JavaPlugin implements Listener, EventListener {
    private JDA jda;
    private TextChannel channel;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        jda = JDABuilder.createDefault(getConfig().getString("discord-token"))
                .addEventListeners(this)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        int pluginId = 28738;
        Metrics metrics = new Metrics(this, pluginId);

        getComponentLogger().info(Component.text("DiscordMinecraftOnlinePlugin was enabled successfully!", NamedTextColor.GREEN));
    }

    public void updateMessage(Player leavingPlayer) {
        assert channel != null;
        String playerList = getOnlinePlayers().stream()
                .filter(player -> {
                    if (leavingPlayer != null) {
                        return !player.getUniqueId().equals(leavingPlayer.getUniqueId());
                    }
                    return true;
                })
                .map(Player::getName)
                .collect(Collectors.joining("\n"));
        String finalPlayerList = playerList.isEmpty() ? "No players online." : playerList;

        channel.getHistoryFromBeginning(10).queue(messageHistory -> {
            Message botMessage = messageHistory.getRetrievedHistory().stream()
                    .filter(message -> message.getAuthor().getId().equals(jda.getSelfUser().getId()))
                    .findFirst()
                    .orElse(null);

            if (botMessage == null) {
                channel.sendMessage(finalPlayerList).queue();
            } else {
                botMessage.editMessage(finalPlayerList).queue();
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updateMessage(event.getPlayer());
    }

    @Override
    public void onEvent(@NonNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            getComponentLogger().info(Component.text("Discord API is ready!", NamedTextColor.GREEN));
            event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
            String activityText = getConfig().getString("activity-text");
            if (activityText != null && !activityText.isEmpty()) {
                event.getJDA().getPresence().setActivity(Activity.playing(activityText));
            }
            channel = jda.getTextChannelById(Objects.requireNonNull(getConfig().getString("discord-channel")));
            updateMessage(null);
        }
        if (event instanceof ShutdownEvent) {
            event.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
        }
    }
}
