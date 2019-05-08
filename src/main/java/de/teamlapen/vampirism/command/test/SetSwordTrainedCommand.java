package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.items.VampirismVampireSword;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class SetSwordTrainedCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("setSwordTrained")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("train", FloatArgumentType.floatArg(0))
                        .executes(context -> {
                            return setSwordCharged(context.getSource(), context.getSource().asPlayer(), FloatArgumentType.getFloat(context, "train"));
                        }));
    }

    private static int setSwordCharged(CommandSource commandSource, EntityPlayerMP asPlayer, float train) {
        ItemStack held = asPlayer.getHeldItemMainhand();

        if (held.getItem() instanceof VampirismVampireSword) {
            ((VampirismVampireSword) held.getItem()).setTrained(held, asPlayer, train);
            asPlayer.setHeldItem(EnumHand.MAIN_HAND, held);
        } else {
            commandSource.sendFeedback(new TextComponentString("You have to hold a vampire sword in your main hand"), true);
        }
        return 0;
    }
}
