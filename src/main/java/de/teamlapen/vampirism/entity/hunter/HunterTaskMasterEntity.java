package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.TaskMasterEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HunterTaskMasterEntity extends HunterBaseEntity implements TaskMasterEntity {

    public HunterTaskMasterEntity(EntityType<? extends HunterBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    protected boolean processInteract(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return true;
        this.processInteraction(playerEntity, Helper.isHunter(playerEntity), Task.Variant.REPEATABLE);
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

}
