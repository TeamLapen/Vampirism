package de.teamlapen.vampirism.command.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public class ResetActionsCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("resetActions")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> resetActions(context.getSource(), Lists.newArrayList(context.getSource().getPlayerOrException())))
                .then(Commands.argument("players", EntityArgument.entities())
                        .executes(context -> resetActions(context.getSource(), Lists.newArrayList(EntityArgument.getPlayers(context, "players")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetActions(CommandSourceStack commandSource, List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            if (!player.isAlive()) continue;
            FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty).ifPresent(factionPlayer -> {
                IActionHandler<?> handler = factionPlayer.getActionHandler();
                if (handler != null) {
                    handler.resetTimers();
                    commandSource.sendSuccess(Component.translatable("command.vampirism.test.resetactions"), false);
                }
            });
        }
        return 0;
    }
}
