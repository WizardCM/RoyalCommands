package org.royaldev.royalcommands.rcommands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

@ReflectCommand
public class CmdLore implements CommandExecutor {

    private final RoyalCommands plugin;

    public CmdLore(RoyalCommands instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lore")) {
            if (!plugin.ah.isAuthorized(cs, "rcmds.lore")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            String loreText = RoyalCommands.getFinalArg(args, 0);
            ItemStack is = p.getItemInHand();
            if (is == null || is.getType() == Material.AIR) {
                cs.sendMessage(MessageColor.NEGATIVE + "You can't set lore on air!");
                return true;
            }
            if (loreText.equalsIgnoreCase("clear")) {
                is = RUtils.clearLore(is);
                cs.sendMessage(MessageColor.POSITIVE + "Reset the lore on your " + MessageColor.NEUTRAL + RUtils.getItemName(is) + MessageColor.POSITIVE + ".");
                return true;
            }
            is = RUtils.addLore(is, RUtils.colorize(loreText));
            p.setItemInHand(is);
            cs.sendMessage(MessageColor.POSITIVE + "Set the lore on your " + MessageColor.NEUTRAL + RUtils.getItemName(is) + MessageColor.POSITIVE + ".");
            return true;
        }
        return false;
    }

}
