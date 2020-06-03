package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.command.arguments.TaskArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

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

}
