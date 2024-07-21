package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.items.VampireSwordItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class VampireSwordCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("sword")
                .then(Commands.literal("charge")
                        .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                        .then(Commands.argument("charge_amount", FloatArgumentType.floatArg(0, 1))
                                .executes(context -> setSwordCharged(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), FloatArgumentType.getFloat(context, "charge_amount")))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setSwordCharged(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "charge_amount"))))))
                .then(Commands.literal("trained")
                        .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                        .then(Commands.argument("train_amount", FloatArgumentType.floatArg(0, 1))
                                .executes(context -> setSwordTrained(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), FloatArgumentType.getFloat(context, "train_amount")))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setSwordTrained(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "train_amount"))))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int setSwordCharged(@NotNull CommandSourceStack commandSource, @NotNull Collection<ServerPlayer> players, float charge) {
        for (ServerPlayer player : players) {
            ItemStack held = player.getMainHandItem().copy();

            if (held.getItem() instanceof VampireSwordItem sword) {
                sword.setCharged(held, charge);
                player.setItemInHand(InteractionHand.MAIN_HAND, held);
            } else {
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.swordcharged.nosword"), false);
            }
        }
        return 0;
    }

    private static int setSwordTrained(@NotNull CommandSourceStack commandSource, @NotNull Collection<ServerPlayer> players, float train) {
        for (ServerPlayer player : players) {
            ItemStack held = player.getMainHandItem().copy();

            if (held.getItem() instanceof VampireSwordItem sword) {
                sword.setTrained(held, player, train);
                player.setItemInHand(InteractionHand.MAIN_HAND, held);
            } else {
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.swordtrained.nosword"), false);
            }
        }
        return 0;
    }
}
