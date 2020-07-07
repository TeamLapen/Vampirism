package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.command.arguments.TaskArgument;
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
                .then(Commands.literal("add")
                    .then(Commands.argument("task", TaskArgument.tasks())
                        .executes(context -> {
                            return completeTask(context.getSource().asPlayer(), TaskArgument.getTask(context, "task"));
                        })))
                .then(Commands.literal("list")
                    .then(Commands.literal("completed")
                        .executes(context -> {
                            return showCompleted(context.getSource().asPlayer());
                        }))
                    .then(Commands.literal("available")
                        .executes(context -> {
                            return showAvailable(context.getSource().asPlayer());
                        }))
                    .then(Commands.literal("completable")
                        .executes(context -> {
                            return showCompletable(context.getSource().asPlayer());
                        })));
    }

    private static int completeTask(ServerPlayerEntity playerEntity, Task task) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(player -> player.getTaskManager().completeTask(task)));
        return 0;
    }

    private static int clearTasks(ServerPlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(player -> {
            player.getTaskManager().reset();
            player.getTaskManager().init();
        }));
        return 0;
    }

    private static int showCompleted(ServerPlayerEntity playerEntity) {
        return show(playerEntity,ITaskManager::getCompletedTasks);
    }

    private static int showAvailable(ServerPlayerEntity playerEntity) {
        return show(playerEntity,ITaskManager::getAvailableTasks);
    }

    private static int showCompletable(ServerPlayerEntity playerEntity) {
        return show(playerEntity, ITaskManager::getCompletableTasks);
    }

    private static int show(ServerPlayerEntity playerEntity, NonNullFunction<ITaskManager, Set<Task>> mapping) {
        LazyOptional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(playerEntity);
        handler.map(FactionPlayerHandler::getCurrentFactionPlayer).map(Optional::get).map(IFactionPlayer::getTaskManager).map(mapping).ifPresent(tasks -> tasks.forEach(task -> playerEntity.sendMessage(new StringTextComponent(Objects.requireNonNull(task.getRegistryName()).toString()))));
        return 0;
    }

}
