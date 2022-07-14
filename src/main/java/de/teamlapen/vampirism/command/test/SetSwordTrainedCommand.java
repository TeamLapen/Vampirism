package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class SetSwordTrainedCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setSwordTrained")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("train", FloatArgumentType.floatArg(0))
                        .executes(context -> setSwordCharged(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), FloatArgumentType.getFloat(context, "train")))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> setSwordCharged(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "charge")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int setSwordCharged(CommandSourceStack commandSource, Collection<ServerPlayer> players, float train) {
        for (ServerPlayer player : players) {
            ItemStack held = player.getMainHandItem();

            if (held.getItem() instanceof VampirismVampireSword) {
                ((VampirismVampireSword) held.getItem()).setTrained(held, player, train);
                player.setItemInHand(InteractionHand.MAIN_HAND, held);
            } else {
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.swordtrained.nosword"), false);
            }
        }
        return 0;
    }
}
