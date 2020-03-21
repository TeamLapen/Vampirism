package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class TaskMasterEntity extends VampirismEntity {

    public TaskMasterEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected boolean processInteract(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return true;
        playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskMasterContainer(containerId, playerInventory), new TranslationTextComponent("container.taskmaster")));
        return true;
    }
}
