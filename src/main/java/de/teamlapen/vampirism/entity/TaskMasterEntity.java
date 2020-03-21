package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.OptionalInt;

public interface TaskMasterEntity {

    ITextComponent CONTAINERNAME = new TranslationTextComponent("container.vampirism.taskmaster");
    ITextComponent NOTASK = new TranslationTextComponent("text.vampirism.taskmaster.no_tasks");

    default void processInteraction(PlayerEntity playerEntity, boolean should, Task.Variant variant) {
        if (should) {
            if (FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(iTaskManager -> iTaskManager.hasAvailableTasks(Task.Variant.REPEATABLE)).orElse(false)) {
                OptionalInt containerIdOpt = playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskMasterContainer(containerId, playerInventory, variant), CONTAINERNAME.deepCopy()));
                if (containerIdOpt.isPresent()) {
                    VampirismMod.dispatcher.sendTo(new TaskStatusPacket(TaskManager.getTasks(playerEntity, iTaskManager -> iTaskManager.getCompletableTasks(Task.Variant.REPEATABLE)), TaskManager.getTasks(playerEntity, iTaskManager -> iTaskManager.getCompletedTasks(Task.Variant.REPEATABLE)), containerIdOpt.getAsInt()), (ServerPlayerEntity) playerEntity);
                }
            } else {
                playerEntity.sendStatusMessage(NOTASK, true);
            }
        }
    }

    default boolean hasCustomName() {
        return true;
    }
}
