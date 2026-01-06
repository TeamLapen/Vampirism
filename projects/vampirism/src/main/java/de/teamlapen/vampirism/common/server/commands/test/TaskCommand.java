package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.server.commands.BasicCommand;
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

                .then(Commands.literal("clear").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(context -> clearTasks(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> clearTasks(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("refreshTaskList").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(context -> refreshTasksList(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> refreshTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetTaskList").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(context -> resetTasksList(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetLordTasks").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(context -> resetLordTasks(Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetLordTasks(EntityArgument.getPlayers(context, "players"))))
                );

    }

    @SuppressWarnings("SameReturnValue")
    private static int refreshTasksList(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.get(player).getTaskManager().ifPresent(ITaskManager::updateTaskLists);
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetTasksList(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.get(player).getTaskManager().ifPresent(ITaskManager::resetTaskLists);
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int clearTasks(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.get(player).getTaskManager().ifPresent(ITaskManager::reset);
        }

        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int resetLordTasks(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.resetLordTasks();
        }
        return 0;
    }
}
