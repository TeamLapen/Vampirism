package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class TaskCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("tasks")

                .then(Commands.literal("clear").requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> clearTasks(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> clearTasks(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("refreshTaskList").requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> refreshTasksList(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> refreshTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetTaskList").requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> resetTasksList(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetLordTasks").requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> resetLordTasks(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetLordTasks(EntityArgument.getPlayers(context, "players"))))
                );

    }

    @SuppressWarnings("SameReturnValue")
    private static int refreshTasksList(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.getCurrentFactionPlayer(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().updateTaskLists());
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetTasksList(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.getCurrentFactionPlayer(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().resetTaskLists());
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int clearTasks(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.getCurrentFactionPlayer(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().reset());
        }

        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetLordTasks(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.resetLordTasks(handler.getLordLevel());
        }
        return 0;
    }
}
