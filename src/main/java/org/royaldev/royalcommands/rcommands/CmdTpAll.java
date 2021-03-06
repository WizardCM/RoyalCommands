package org.royaldev.royalcommands.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

@ReflectCommand
public class CmdTpAll implements CommandExecutor {

    public RoyalCommands plugin;

    public CmdTpAll(RoyalCommands instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tpall")) {
            if (!plugin.ah.isAuthorized(cs, "rcmds.tpall")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            for (Player t : plugin.getServer().getOnlinePlayers()) {
                if (!RUtils.isTeleportAllowed(t) && !plugin.ah.isAuthorized(p, "rcmds.tpoverride"))
                    continue;
                if (t.equals(p)) continue;
                String error = RUtils.teleport(t, p);
                if (!error.isEmpty()) {
                    p.sendMessage(MessageColor.NEGATIVE + error);
                    return true;
                }
            }
            p.sendMessage(MessageColor.POSITIVE + "All players teleported to you.");
            return true;
        }
        return false;
    }

}
