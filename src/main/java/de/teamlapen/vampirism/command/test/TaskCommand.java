package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;
import java.util.Collections;

public class TaskCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("tasks")

                .then(Commands.literal("clear").requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> clearTasks(Collections.singleton(context.getSource().asPlayer())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> clearTasks(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("refreshTaskList").requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> refreshTasksList(Collections.singleton(context.getSource().asPlayer())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> refreshTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetTaskList").requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> resetTasksList(Collections.singleton(context.getSource().asPlayer())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetTasksList(EntityArgument.getPlayers(context, "players")))))
                .then(Commands.literal("resetLordTasks").requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                        .executes(context -> resetLordTasks(Collections.singleton(context.getSource().asPlayer())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> resetLordTasks(EntityArgument.getPlayers(context, "players"))))
                );

    }

    private static int refreshTasksList(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> factionPlayer.getTaskManager().updateTaskLists()));
        }
        return 0;
    }

    private static int resetTasksList(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> factionPlayer.getTaskManager().resetTaskLists()));
        }
        return 0;
    }

    private static int clearTasks(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> factionPlayer.getTaskManager().reset()));
        }

        return 0;
    }

    private static int resetLordTasks(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(fph -> fph.resetLordTasks(fph.getLordLevel()));
        }
        return 0;
    }
}
