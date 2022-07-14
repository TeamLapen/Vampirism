package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.player.FactionBasePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class BloodBarCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("bloodBar")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("fill")
                        .executes(context -> setBloodBar(Integer.MAX_VALUE, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> setBloodBar(Integer.MAX_VALUE, EntityArgument.getPlayers(context, "player")))))
                .then(Commands.literal("empty")
                        .executes(context -> setBloodBar(0, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> setBloodBar(0, EntityArgument.getPlayers(context, "player")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> setBloodBar(IntegerArgumentType.getInteger(context, "amount"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(context -> setBloodBar(IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "player"))))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int setBloodBar(int amount, Collection<ServerPlayer> player) {
        player.stream().map(VampirePlayer::getOpt).filter(player1 -> player1.map(FactionBasePlayer::getLevel).orElse(0) > 0).forEach(player1 -> player1.ifPresent(vampire -> {
            vampire.useBlood(Integer.MAX_VALUE, true);
            vampire.drinkBlood(amount, 0, false);
        }));
        return 0;
    }
}
