package org.royaldev.royalcommands.rcommands;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

import static org.royaldev.royalcommands.Converters.toInt;

@ReflectCommand
public class CmdFirework implements CommandExecutor {

    private final RoyalCommands plugin;

    public CmdFirework(RoyalCommands instance) {
        plugin = instance;
    }

    /**
     * Takes an array of tags and applies them to a FireworkMeta.
     *
     * @param args Array of tags
     * @param fm   FireworkMeta to apply tags to
     * @return Modified FireworkMeta
     * @throws IllegalArgumentException If one tag is invalid (message is error to be displayed to user)
     */
    private FireworkMeta applyEffect(String[] args, FireworkMeta fm) throws IllegalArgumentException {
        FireworkEffect.Builder feb = FireworkEffect.builder();
        for (String arg : args) {
            if (arg.startsWith("fade:")) {
                arg = arg.substring(5);
                Color c = getColor(arg);
                if (c == null) throw new IllegalArgumentException("Invalid fade color!");
                feb.withFade(c);
            } else if (arg.startsWith("power:")) {
                arg = arg.substring(6);
                int power = toInt(arg);
                if (power < 0 || power > 128) throw new IllegalArgumentException("Power must be between 0 and 128!");
                fm.setPower(power);
            } else if (arg.startsWith("color:")) {
                arg = arg.substring(6);
                Color c = getColor(arg);
                if (c == null) throw new IllegalArgumentException("Invalid color!");
                feb.withColor(c);
            } else if (arg.startsWith("shape:")) {
                FireworkEffect.Type t = getShape(arg.substring(6));
                if (t == null) throw new IllegalArgumentException("Invalid shape!");
                feb.with(t);
            } else if (arg.startsWith("effect:")) {
                arg = arg.substring(7);
                if (arg.equalsIgnoreCase("flicker") || arg.equalsIgnoreCase("sparkle") || arg.equalsIgnoreCase("twinkle"))
                    feb.withFlicker();
                else if (arg.equalsIgnoreCase("trail")) feb.withTrail();
                else throw new IllegalArgumentException("Invalid effect!");
            }
        }
        FireworkEffect fe;
        try {
            fe = feb.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error: " + MessageColor.NEUTRAL + e.getMessage());
        }
        fm.addEffect(fe);
        return fm;
    }

    private FireworkEffect.Type getShape(String c) {
        if (c.equalsIgnoreCase("ball") || c.equalsIgnoreCase("small_ball") || c.equalsIgnoreCase("ball_small")) {
            return FireworkEffect.Type.BALL;
        } else if (c.equalsIgnoreCase("large_ball") || c.equalsIgnoreCase("ball_large") || c.equalsIgnoreCase("big_ball") || c.equalsIgnoreCase("ball_big")) {
            return FireworkEffect.Type.BALL_LARGE;
        } else if (c.equalsIgnoreCase("star")) {
            return FireworkEffect.Type.STAR;
        } else if (c.equalsIgnoreCase("head") || c.equalsIgnoreCase("creeper") || c.equalsIgnoreCase("creeper_head")) {
            return FireworkEffect.Type.CREEPER;
        } else if (c.equalsIgnoreCase("burst")) {
            return FireworkEffect.Type.BURST;
        }
        return null;
    }

    private Color getColor(String c) {
        if (c.startsWith("rgb:")) {
            c = c.substring(4);
            if (c.contains(",")) {
                String[] colors = c.split(",");
                if (colors.length < 3) return Color.WHITE;
                int r = toInt(colors[0]);
                int g = toInt(colors[1]);
                int b = toInt(colors[2]);
                if (r < 0 || g < 0 || b < 0) {
                    return null;
                }
                return Color.fromRGB(r, g, b);
            }
            return Color.fromRGB(toInt(c.substring(4)));
        } else if (c.startsWith("bgr:")) {
            c = c.substring(4);
            if (c.contains(",")) {
                String[] colors = c.split(",");
                if (colors.length < 3) return Color.WHITE;
                int b = toInt(colors[0]);
                int g = toInt(colors[1]);
                int r = toInt(colors[2]);
                if (r < 0 || g < 0 || b < 0) {
                    return null;
                }
                return Color.fromBGR(b, g, r);
            }
            return Color.fromBGR(toInt(c.substring(4)));
        }
        if (c.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        } else if (c.equalsIgnoreCase("red")) {
            return Color.RED;
        } else if (c.equalsIgnoreCase("green")) {
            return Color.GREEN;
        } else if (c.equalsIgnoreCase("white")) {
            return Color.WHITE;
        } else if (c.equalsIgnoreCase("yellow")) {
            return Color.YELLOW;
        } else if (c.equalsIgnoreCase("aqua")) {
            return Color.AQUA;
        } else if (c.equalsIgnoreCase("black")) {
            return Color.BLACK;
        } else if (c.equalsIgnoreCase("teal")) {
            return Color.TEAL;
        } else if (c.equalsIgnoreCase("purple")) {
            return Color.PURPLE;
        } else if (c.equalsIgnoreCase("silver")) {
            return Color.SILVER;
        } else if (c.equalsIgnoreCase("fuchsia")) {
            return Color.FUCHSIA;
        } else if (c.equalsIgnoreCase("orange")) {
            return Color.ORANGE;
        } else if (c.equalsIgnoreCase("gray")) {
            return Color.GRAY;
        } else if (c.equalsIgnoreCase("olive")) {
            return Color.OLIVE;
        } else if (c.equalsIgnoreCase("maroon")) {
            return Color.MAROON;
        } else if (c.equalsIgnoreCase("lime")) {
            return Color.LIME;
        } else if (c.equalsIgnoreCase("navy")) {
            return Color.NAVY;
        }
        return null;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("firework")) {
            if (!plugin.ah.isAuthorized(cs, "rcmds.firework")) {
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
            ItemStack is = p.getItemInHand();
            if (is.getType() != Material.FIREWORK) {
                cs.sendMessage(MessageColor.NEGATIVE + "The item in hand is not a firework!");
                return true;
            }
            FireworkMeta fm = (FireworkMeta) is.getItemMeta();
            if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("remove")) {
                if (args.length > 1) {
                    int effectToRemove;
                    try {
                        effectToRemove = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        cs.sendMessage(MessageColor.NEGATIVE + "The specified effect was not a number!");
                        return true;
                    }
                    effectToRemove--; // the first effect is really 0, but users will enter 1, so remove 1 from user input
                    if (effectToRemove < 0 || effectToRemove >= fm.getEffectsSize()) {
                        cs.sendMessage(MessageColor.NEGATIVE + "No such effect!");
                        return true;
                    }
                    fm.removeEffect(effectToRemove);
                    is.setItemMeta(fm);
                    cs.sendMessage(MessageColor.POSITIVE + "Removed effect " + MessageColor.NEUTRAL + (effectToRemove + 1) + MessageColor.POSITIVE + ".");
                    return true;
                }
                fm.clearEffects();
                is.setItemMeta(fm);
                cs.sendMessage(MessageColor.POSITIVE + "Cleared all firework effects.");
                return true;
            }
            try {
                fm = applyEffect(args, fm);
            } catch (IllegalArgumentException e) {
                cs.sendMessage(MessageColor.NEGATIVE + e.getMessage());
                return true;
            }
            is.setItemMeta(fm);
            cs.sendMessage(MessageColor.POSITIVE + "Added effect to firework!");
            return true;
        }
        return false;
    }

}
