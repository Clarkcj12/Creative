package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.show.Show;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;

/**
 * Created by Marc on 12/11/15
 */
@CommandMeta(description = "Show Manager")
@CommandPermission(rank = Rank.SETTLER)
public class Commandshow extends CoreCommand {
    private FormattedMessage msg = new FormattedMessage("[Show] ").color(ChatColor.BLUE)
            .then("Purchase the Show Creator in the Creative Shop to use this! ").color(ChatColor.YELLOW)
            .then("Click here to open the Shop").color(ChatColor.AQUA).style(ChatColor.BOLD).tooltip(ChatColor.GREEN +
                    "Open the Creative Shop").command("/shop");

    public Commandshow() {
        super("show");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (!data.hasShowCreator()) {
            msg.send(player);
            return;
        }
        if (args.length == 0) {
            helpMenu(player);
            return;
        }
        String action = args[0];
        switch (action.toLowerCase()) {
            case "start": {
                Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Show show = Creative.getInstance().getShowManager().startShow(player);
                        if (show != null && show.getNameColored() != null) {
                            Creative.getInstance().getShowManager().messagePlayer(player, "Your show " + ChatColor.AQUA + show.getNameColored() +
                                    ChatColor.GREEN + " has started!");
                        } else {
                            Creative.getInstance().getShowManager().messagePlayer(player, "Error starting your show! (Did you create one yet?)");
                        }
                    }
                });
                return;
            }
            case "stop": {
                if (Creative.getInstance().getShowManager().stopShow(player.getBukkitPlayer())) {
                    Creative.getInstance().getShowManager().messagePlayer(player, "Your show has stopped!");
                } else {
                    Creative.getInstance().getShowManager().messagePlayer(player, ChatColor.RED +
                            "There was an error stopping your show! (Maybe it wasn't running?)");
                }
                return;
            }
            case "name": {
                if (args.length == 1) {
                    helpMenu(player);
                    return;
                }
                StringBuilder name = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    name.append(args[i]);
                    if (i < (args.length - 1)) {
                        name.append(" ");
                    }
                }
                Creative.getInstance().getShowManager().setShowName(player, name.toString());
                Creative.getInstance().getShowManager().messagePlayer(player, "Your Show's name has been set to " +
                        ChatColor.translateAlternateColorCodes('&', name.toString()));
                return;
            }
            case "edit": {
                try {
                    Creative.getInstance().getShowManager().editShow(player);
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "There was an error editing your current Show! Please contact a Cast Member. (Error Code 111)");
                }
                return;
            }
            case "reload": {
                if (player.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
                    helpMenu(player);
                    return;
                }
                Creative.getInstance().getShowManager().loadTracks();
                player.sendMessage(ChatColor.GREEN + "Audio tracks reloaded!");
                return;
            }
            default:
                helpMenu(player);
        }
    }

    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Show Commands:");
        player.sendMessage(ChatColor.GREEN + "/show start " + ChatColor.AQUA + "- Start your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show stop " + ChatColor.AQUA + "- Stop your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show name [Name] " + ChatColor.AQUA + "- Name your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show edit " + ChatColor.AQUA + "- Edit your Show");
        if (player.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/show reload " + ChatColor.AQUA + "- Reload track list");
        }
    }
}