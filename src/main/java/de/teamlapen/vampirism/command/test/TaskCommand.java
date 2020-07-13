package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class TaskCommand extends BasicCommand {



    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("tasks")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            return clearTasks(context.getSource().asPlayer());
                        }))
                .then(Commands.literal("refreshTaskList")
                        .executes(context -> {
                            return refreshTasksList(context.getSource().asPlayer());
                        }))
                .then(Commands.literal("resetTaskList")
                        .executes(context -> {
                            return resetTasksList(context.getSource().asPlayer());
                        }));
    }

    private static int refreshTasksList(ServerPlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(player -> player.getTaskManager().updateTaskLists()));
        return 0;
    }

    private static int resetTasksList(ServerPlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(player -> player.getTaskManager().resetTaskLists()));
        return 0;
    }

    private static int clearTasks(ServerPlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(player -> {
            player.getTaskManager().reset();
        }));
        return 0;
    }

}
