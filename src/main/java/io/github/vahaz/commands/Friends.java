package io.github.vahaz.commands;

import io.github.vahaz.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Friends implements CommandExecutor {

    private final Utils utils = new Utils();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Command command, @Nullable String label, @Nullable String[] args) {
        if (sender instanceof Player player){
            String playerUUID = player.getUniqueId().toString();

            if (args.length == 0) {
                utils.sendMessageToPlayer(player, "friend.help", "error", null);
                return true;
            }

            if (args.length == 1) {
                // /friends list
                if (args[0] != null && args[0].equalsIgnoreCase("list")) {
                    List<String> friendList = (List<String>) utils.getPlayerData(player, "social.friends");

                    if(friendList == null) {
                        utils.sendMessageToPlayer(player, "friend.no_friends", "error", null);
                        return true;
                    }

                    if(friendList.isEmpty()) {
                        utils.sendMessageToPlayer(player, "friend.no_friends", "error", null);
                        return true;
                    }

                    utils.sendMessageToPlayer(player, "friend.list_header", "success", null);
                    for (String uuid : friendList) {
                        Player friend = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (friend == null) continue;
                        utils.sendMessageToPlayer(player, "friend.list_content", null, friend);
                    }
                    utils.sendMessageToPlayer(player, "friend.list_footer", null, null);
                    return true;
                }
            }

            if (args.length == 2) {
                // /friends add <player>
                if ((args[0] != null && args[0].equalsIgnoreCase("add")) && args[1] != null) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }

                    if (target == player) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }

                    String targetUUID = target.getUniqueId().toString();

                    if (!target.isOnline()) {
                        utils.sendMessageToPlayer(player, "friend.player_disconnected", "error", target);
                        return true;
                    }

                    if (utils.isContainedPlayerData(player, "social.friends", targetUUID)) {
                        utils.sendMessageToPlayer(player, "friend.already_friend", "error", target);
                        return true;
                    }

                    if (utils.isContainedPlayerData(player, "social.request", targetUUID) || utils.isContainedPlayerData(target, "social.request", playerUUID)) {
                        utils.sendMessageToPlayer(player, "friend.request_pending", "error", target);
                        return true;
                    }

                    if (utils.getPlayerData(player, "social.friends") == null || !utils.isContainedPlayerData(player, "social.friends", targetUUID)) {
                        utils.addPlayerData(player, "social.request", targetUUID);
                        utils.addPlayerData(target, "social.request", playerUUID);

                        utils.sendMessageToPlayer(player, "friend.request_sent", "success", target);
                        utils.sendMessageToPlayer(target, "friend.request_received", "success", player);

                        TextComponent acceptComponent = new TextComponent("[ACCEPT]");
                        acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/friends accept %s", player.getDisplayName())));
                        acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("/friends accept %s", player.getDisplayName()))));
                        acceptComponent.setColor(ChatColor.GREEN);
                        acceptComponent.addExtra(" / ");
                        TextComponent denyComponent = new TextComponent("[DENY]");
                        denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/friends deny %s", player.getDisplayName())));
                        denyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("/friends deny %s", player.getDisplayName()))));
                        denyComponent.setColor(ChatColor.RED);
                        acceptComponent.addExtra(denyComponent);
                        target.spigot().sendMessage(acceptComponent);

                        return true;
                    }
                    return false;
                }

                // /friends remove <player>
                if ((args[0] != null && args[0].equalsIgnoreCase("remove")) && args[1] != null) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }

                    if (target == player) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }

                    String targetUUID = target.getUniqueId().toString();
                    if (!utils.isContainedPlayerData(player, "social.friends", targetUUID)) {
                        utils.sendMessageToPlayer(player, "friend.not_friend", "error", target);
                        return true;
                    }

                    utils.removePlayerData(player, "social.friends", targetUUID);
                    utils.removePlayerData(target, "social.friends", playerUUID);
                    utils.sendMessageToPlayer(player, "friend.removed", "success", target);
                    return true;
                }

                // /friends accept <player>
                if ((args[0] != null && args[0].equalsIgnoreCase("accept")) && args[1] != null) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }
                    if (target == player) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }
                    String targetUUID = target.getUniqueId().toString();
                    if(utils.isContainedPlayerData(player, "social.request", targetUUID)) {
                        utils.removePlayerData(player,"social.request", targetUUID);
                        utils.removePlayerData(target,"social.request", playerUUID);
                        utils.addPlayerData(player, "social.friends", targetUUID);
                        utils.addPlayerData(target, "social.friends", playerUUID);
                        utils.sendMessageToPlayer(player, "friend.you_accept_request", "success", target);
                        utils.sendMessageToPlayer(target, "friend.request_accepted", "success", player);
                    } else utils.sendMessageToPlayer(player, "friend.not_requested", "error", target);
                    return true;
                }

                // /friends deny <player>
                if ((args[0] != null && args[0].equalsIgnoreCase("deny")) && args[1] != null) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }
                    if (target == player) {
                        utils.sendMessageToPlayer(player, "friend.help", "error", null);
                        return true;
                    }
                    String targetUUID = target.getUniqueId().toString();
                    if(utils.isContainedPlayerData(player, "social.request", targetUUID)) {
                        utils.removePlayerData(player,"social.request", targetUUID);
                        utils.removePlayerData(target,"social.request", playerUUID);
                        utils.sendMessageToPlayer(player, "friend.you_refuse_request", "success", target);
                        utils.sendMessageToPlayer(target, "friend.request_denied", "error", player);
                    } else utils.sendMessageToPlayer(player, "friend.not_requested", "error", target);
                    return true;
                }
            }

            utils.sendMessageToPlayer(player, "friend.help", "error", null);
            return true;
        }
        return false;
    }


}
