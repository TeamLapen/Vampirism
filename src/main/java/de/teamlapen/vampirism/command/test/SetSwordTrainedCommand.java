package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

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

    private static int setSwordCharged(CommandSource commandSource, ServerPlayerEntity asPlayer, float train) {
        ItemStack held = asPlayer.getHeldItemMainhand();

        if (held.getItem() instanceof VampirismVampireSword) {
            ((VampirismVampireSword) held.getItem()).setTrained(held, asPlayer, train);
            asPlayer.setHeldItem(Hand.MAIN_HAND, held);
        } else {
            commandSource.sendFeedback(new StringTextComponent("You have to hold a vampire sword in your main hand"), true);
        }
        return 0;
    }
}
