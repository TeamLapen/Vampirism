package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.TaskMasterEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class HunterTaskMasterEntity extends HunterBaseEntity implements TaskMasterEntity {

    public HunterTaskMasterEntity(EntityType<? extends HunterBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    protected boolean processInteract(@Nonnull PlayerEntity playerEntity, @Nonnull Hand hand) {
        if (this.world.isRemote) return true;
        this.processInteraction(playerEntity, Helper.isHunter(playerEntity), Task.Variant.REPEATABLE);
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.7F, false, 300, () -> false));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7, 50));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }
}
