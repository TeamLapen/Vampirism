package de.teamlapen.factions.common.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ResetActionsCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("resetActions")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .executes(context -> resetActions(context.getSource(), Lists.newArrayList(context.getSource().getPlayerOrException())))
                .then(Commands.argument("players", EntityArgument.entities())
                        .executes(context -> resetActions(context.getSource(), Lists.newArrayList(EntityArgument.getPlayers(context, "players")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetActions(CommandSourceStack commandSource, List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            if (!player.isAlive()) continue;
            FactionPlayerHandler.get(player).getActionHandler().ifPresent(handler -> {
                handler.resetTimers();
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.resetactions"), false);
            });
        }
        return 0;
    }
}
