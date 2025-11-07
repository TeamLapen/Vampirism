package de.teamlapen.vampirism.common.entity.hunter;

import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.common.entity.VampirismEntity;
import de.teamlapen.vampirism.common.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class DummyHunterTrainerEntity extends VampirismEntity implements ICaptureIgnore {
    private static final int MOVE_TO_RESTRICT_PRIO = 3;

    public DummyHunterTrainerEntity(EntityType<? extends DummyHunterTrainerEntity> type, Level world) {
        super(type, world);
        saveHome = true;
        hasArms = true;
        this.getNavigation().setCanOpenDoors(true);

        this.setDontDropEquipment();
        this.peaceful = true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() != null;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void setHome(AABB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (Helper.isHunter(player)) {
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.trainer_disabled_hunter"));
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.trainer_disabled"));
                }
            }
            return InteractionResult.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }


}
