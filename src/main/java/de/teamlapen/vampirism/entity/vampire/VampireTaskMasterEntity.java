package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.ITaskMaster;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.TaskManager;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.OptionalInt;

public class VampireTaskMasterEntity extends VampireBaseEntity implements ITaskMaster {

    public VampireTaskMasterEntity(EntityType<? extends VampireBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    protected boolean processInteract(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return true;
        if (Helper.isVampire(playerEntity)) {
            if (FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(ITaskManager::hasAvailableTasks).orElse(false)) {
                OptionalInt containerIdOpt = playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskMasterContainer(containerId, playerInventory), CONTAINERNAME.deepCopy()));
                if (containerIdOpt.isPresent()) {
                    VampirismMod.dispatcher.sendTo(new TaskStatusPacket(TaskManager.getTasks(playerEntity, ITaskManager::getCompletableTasks), TaskManager.getTasks(playerEntity, ITaskManager::getCompletedTasks), containerIdOpt.getAsInt()), (ServerPlayerEntity) playerEntity);
                }
            } else {
                playerEntity.sendStatusMessage(NOTASK, true);
            }
        }
        return true;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

}
