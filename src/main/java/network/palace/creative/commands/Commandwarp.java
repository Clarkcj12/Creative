package network.palace.creative.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Warp to a location")
@CommandPermission(rank = Rank.SETTLER)
public class Commandwarp extends CoreCommand {

    public Commandwarp() {
        super("warp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "/warp [Warp] [Player]");
                return;
            }
            Warp warp = Creative.getInstance().getWarp(args[0]);
            if (warp == null) {
                sender.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            Player tp = Bukkit.getPlayer(args[1]);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            Creative.getInstance().getTeleportUtil().log(tp, tp.getLocation());
            tp.teleport(warp.getLocation());
            tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
        if (player == null)
            return;
        if (player.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            if (args.length == 0) {
                listWarps(player, 1);
                return;
            }
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return;
            }
            Warp warp = Creative.getInstance().getWarp(args[0]);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (player.getRank().getRankId() < Rank.DWELLER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be a " + Rank.DWELLER.getFormattedName() +
                            ChatColor.RED + " or higher to go here!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("share")) {
                if (player.getRank().getRankId() < Rank.HONORABLE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be a " + Rank.HONORABLE.getFormattedName() +
                            ChatColor.RED + " or higher to go here!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (player.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be an " + Rank.SQUIRE.getFormattedName() +
                            ChatColor.RED + " or higher to go here!");
                    return;
                }
            }
            Creative.getInstance().getTeleportUtil().log(player.getBukkitPlayer(), player.getLocation());
            player.teleport(warp.getLocation());
            player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return;
        }
        if (args.length == 0) {
            listWarps(player, 1);
            return;
        }
        if (args.length == 1) {
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return;
            }
            Warp warp = Creative.getInstance().getWarp(args[0]);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            Creative.getInstance().getTeleportUtil().log(player.getBukkitPlayer(), player.getLocation());
            player.teleport(warp.getLocation());
            player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return;
        }
        if (isInt(args[0])) {
            listWarps(player, Integer.parseInt(args[0]));
            return;
        }
        Warp warp = Creative.getInstance().getWarp(args[0]);
        Player tp = Bukkit.getPlayer(args[1]);
        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Warp not found!");
            return;
        }
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        Creative.getInstance().getTeleportUtil().log(tp, tp.getLocation());
        tp.teleport(warp.getLocation());
        tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                warp.getName() + ChatColor.WHITE + "]");
        player.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN
                + warp.getName() + ChatColor.WHITE + "]");
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void listWarps(CPlayer player, int page) {
        List<Warp> warps = Creative.getInstance().getWarps();
        List<String> nlist = warps.stream().map(Warp::getName).collect(Collectors.toList());
        Collections.sort(nlist);
        if (nlist.size() < (page - 1) * 20 && page != 1) {
            listWarps(player, 1);
            return;
        }
        int max = page * 20;
        List<String> names = nlist.subList(20 * (page - 1), nlist.size() < max ? nlist.size() : max);
        FormattedMessage msg = new FormattedMessage("Warps (Page " + page + "):\n").color(ChatColor.GRAY);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i == (names.size() - 1)) {
                msg.then(warp).color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                        "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
                continue;
            }
            msg.then(warp + ", ").color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                    "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }
}