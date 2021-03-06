package org.royaldev.royalcommands.rcommands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PConfManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReflectCommand
public class CmdKit implements CommandExecutor {

    private final RoyalCommands plugin;

    public CmdKit(RoyalCommands instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("kit")) {
            if (!plugin.ah.isAuthorized(cs, "rcmds.kit")) {
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
            final FileConfiguration c = plugin.getConfig();
            Player p = (Player) cs;
            final PConfManager pcm = PConfManager.getPConfManager(p);
            if (!c.isSet("kits") || !c.isSet("kits.list")) {
                cs.sendMessage(MessageColor.NEGATIVE + "No kits defined!");
                return true;
            }
            String kitname = args[0];
            if (!c.isSet("kits.list." + kitname)) {
                cs.sendMessage(MessageColor.NEGATIVE + "That kit does not exist!");
                return true;
            }
            List<String> kits = c.getStringList("kits.list." + kitname + ".items");
            List<String> enchants = c.getStringList("kits.list." + kitname + ".enchantments");
            List<String> names = c.getStringList("kits.list." + kitname + ".names");
            List<String> lore = c.getStringList("kits.list." + kitname + ".lore");
            if (kits == null) {
                cs.sendMessage(MessageColor.NEGATIVE + "That kit does not exist!");
                return true;
            }
            if (enchants == null) enchants = new ArrayList<String>();
            if (names == null) names = new ArrayList<String>();
            if (lore == null) lore = new ArrayList<String>();
            if (Config.kitPerms && !plugin.ah.isAuthorized(cs, "rcmds.kit." + kitname)) {
                cs.sendMessage(MessageColor.NEGATIVE + "You don't have permission for that kit!");
                plugin.log.warning("[RoyalCommands] " + cs.getName() + " was denied access to the command!");
                return true;
            }
            if (pcm.isSet("kits.list." + kitname + ".cooldown") && pcm.getLong("kits.list." + kitname + ".cooldown") < 0D) {
                cs.sendMessage(MessageColor.NEGATIVE + "That kit was a one-time kit.");
                return true;
            }
            if (RUtils.isTimeStampValid(p, "kits.list." + kitname + ".cooldown") && !plugin.ah.isAuthorized(cs, "rcmds.exempt.cooldown.kits")) {
                long ts = RUtils.getTimeStamp(p, "kits.list." + kitname + ".cooldown");
                if (ts > 0) {
                    p.sendMessage(MessageColor.NEGATIVE + "You can't use that kit for" + MessageColor.NEUTRAL + RUtils.formatDateDiff(ts) + MessageColor.NEGATIVE + ".");
                    return true;
                }
            }
            if (c.isSet("kits.list." + kitname + ".cooldown")) {
                long cd = c.getLong("kits.list." + kitname + ".cooldown");
                RUtils.setTimeStamp(p, cd, "kits.list." + kitname + ".cooldown");
            }
            if (kits.size() < 1) {
                cs.sendMessage(MessageColor.NEGATIVE + "That kit was configured wrong!");
                return true;
            }
            for (String s : kits) {
                int index = kits.indexOf(s);
                String enchantmentString;
                try {
                    enchantmentString = enchants.get(index);
                } catch (IndexOutOfBoundsException e) {
                    enchantmentString = null;
                }
                Map<Enchantment, Integer> enchant = (enchantmentString == null) ? null : RUtils.getEnchantments(enchantmentString);
                String[] kit = s.trim().split(":");
                if (kit.length < 2) {
                    cs.sendMessage(MessageColor.NEGATIVE + "That kit was configured wrong!");
                    return true;
                }
                int id;
                int amount;
                int data = -1;
                try {
                    id = Integer.parseInt(kit[0]);
                } catch (Exception e) {
                    cs.sendMessage(MessageColor.NEGATIVE + "That kit was configured wrong!");
                    return true;
                }
                try {
                    amount = Integer.parseInt(kit[1]);
                } catch (Exception e) {
                    cs.sendMessage(MessageColor.NEGATIVE + "That kit was configured wrong!");
                    return true;
                }
                try {
                    data = Integer.parseInt(kit[2]);
                } catch (Exception ignored) {
                }
                if (id < 1 || amount < 1) {
                    cs.sendMessage(MessageColor.NEGATIVE + "That kit was configured wrong!");
                    return true;
                }
                if (Material.getMaterial(id) == null) {
                    cs.sendMessage(MessageColor.NEGATIVE + "Invalid item ID in kit: " + MessageColor.NEUTRAL + id);
                    return true;
                }
                ItemStack item;
                if (data > -1) {
                    item = new ItemStack(id, amount, (short) data);
                } else {
                    item = new ItemStack(id, amount);
                }
                if (enchant != null) item.addUnsafeEnchantments(enchant);
                try {
                    String name = names.get(index);
                    if (!name.isEmpty()) item = RUtils.renameItem(item, name);
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    String lo = lore.get(index);
                    if (!lo.isEmpty()) item = RUtils.addLore(item, lo);
                } catch (IndexOutOfBoundsException ignored) {
                }
                HashMap<Integer, ItemStack> left = p.getInventory().addItem(item);
                if (!left.isEmpty()) {
                    for (ItemStack is : left.values()) {
                        p.getWorld().dropItemNaturally(p.getLocation(), is);
                    }
                }
            }
            p.sendMessage(MessageColor.POSITIVE + "Giving you the kit \"" + MessageColor.NEUTRAL + kitname + MessageColor.POSITIVE + ".\"");
            return true;
        }
        return false;
    }
}
