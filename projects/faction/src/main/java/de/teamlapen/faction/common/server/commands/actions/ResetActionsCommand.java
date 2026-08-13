package de.teamlapen.faction.common.server.commands.actions;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ResetActionsCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reset")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .executes(context -> resetActions(context.getSource(), Lists.newArrayList(context.getSource().getPlayerOrException())))
                .then(Commands.argument("players", EntityArgument.entities())
                        .executes(context -> resetActions(context.getSource(), Lists.newArrayList(EntityArgument.getPlayers(context, "players")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetActions(CommandSourceStack commandSource, List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            if (!player.isAlive()) continue;
            IActionHandler.get(player).ifPresent(handler -> {
                handler.resetTimers();
                commandSource.sendSuccess(() -> Component.translatable("command.factionapi.test.resetactions"), false);
            });
        }
        return 0;
    }
}
