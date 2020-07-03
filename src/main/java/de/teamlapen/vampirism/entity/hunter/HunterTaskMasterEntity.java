package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.TaskMasterEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class HunterTaskMasterEntity extends HunterBaseEntity implements TaskMasterEntity {

    @Nullable
    private PlayerEntity interactor;

    public HunterTaskMasterEntity(EntityType<? extends HunterBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected ActionResultType func_230254_b_(@Nonnull PlayerEntity playerEntity, @Nonnull Hand hand) {
        if (this.world.isRemote) return ActionResultType.SUCCESS;
        if (Helper.isHunter(playerEntity) && interactor == null) {
            if (this.processInteraction(playerEntity, this)) {
                this.getNavigator().clearPath();
                this.interactor = playerEntity;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (interactor != null && !(interactor.isAlive() && interactor.openContainer instanceof TaskBoardContainer)){
            this.interactor = null;
        }
    }

    @Nonnull
    @Override
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(this.interactor);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return Helper.isHunter(Minecraft.getInstance().player);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.7F, false, 300, () -> false));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .createMutableAttribute(SharedMonsterAttributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH)
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE)
                .createMutableAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }
}
