package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.ai.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;

public interface IDefaultTaskMasterEntity extends ForceLookEntityGoal.TaskOwner, ITaskMasterEntity {

    Component CONTAINER_NAME = Component.translatable("container.vampirism.taskmaster");
    Component NO_TASK = Component.translatable("text.vampirism.taskmaster.no_tasks");

    /**
     * @return The biome type based on where this entity was spawned
     */
    VillagerType getBiomeType();

    default boolean processInteraction(@NotNull Player playerEntity, @NotNull Entity entity) {
        if (FactionPlayerHandler.getCurrentFactionPlayer(playerEntity).map(IFactionPlayer::getTaskManager).map(taskManager -> taskManager.hasAvailableTasks(entity.getUUID())).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openMenu(new SimpleMenuProvider((containerId, playerInventory, player) -> new TaskBoardMenu(containerId, playerInventory), entity.getDisplayName().plainCopy()));
            if (containerIdOpt.isPresent()) {
                FactionPlayerHandler.getCurrentFactionPlayer(playerEntity).ifPresent(iFactionPlayer -> {
                    iFactionPlayer.getTaskManager().openTaskMasterScreen(entity.getUUID());
                });
                return true;
            }
        } else {
            playerEntity.displayClientMessage(NO_TASK, true);
        }
        return false;
    }

}
