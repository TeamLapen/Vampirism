package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;

public class SetSwordTrainedCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("setSwordTrained")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("train", FloatArgumentType.floatArg(0))
                        .executes(context -> {
                            return setSwordCharged(context.getSource(), Collections.singleton(context.getSource().asPlayer()), FloatArgumentType.getFloat(context, "train"));
                        })
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> {
                                    return setSwordCharged(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "charge"));
                                })));
    }

    private static int setSwordCharged(CommandSource commandSource, Collection<ServerPlayerEntity> players, float train) {
        for (ServerPlayerEntity player : players) {
            ItemStack held = player.getHeldItemMainhand();

            if (held.getItem() instanceof VampirismVampireSword) {
                ((VampirismVampireSword) held.getItem()).setTrained(held, player, train);
                player.setHeldItem(Hand.MAIN_HAND, held);
            } else {
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.swordtrained.nosword"), false);
            }
        }
        return 0;
    }
}
