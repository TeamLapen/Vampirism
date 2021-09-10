package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class HunterTrainerEntity extends HunterBaseEntity implements ForceLookEntityGoal.TaskOwner, ICaptureIgnore {
    private static final ITextComponent name = new TranslationTextComponent("container.huntertrainer");

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.ATTACK_DAMAGE, 19)
                .add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.FOLLOW_RANGE, 5);
    }
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private PlayerEntity trainee;
    private boolean shouldCreateHome;

    public HunterTrainerEntity(EntityType<? extends HunterTrainerEntity> type, World world) {
        super(type, world, false);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigator) this.getNavigation()).setCanOpenDoors(true);
        this.peaceful = true;
        this.setDontDropEquipment();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("createHome", this.shouldCreateHome);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (trainee != null && !(trainee.containerMenu instanceof HunterTrainerContainer)) {
            this.trainee = null;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    /**
     * @return The player which has the trainings gui open.
     */
    @Nonnull
    @Override
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(trainee);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("createHome") && (this.shouldCreateHome = nbt.getBoolean("createHome"))) {
            if (this.getRestrictCenter().equals(BlockPos.ZERO)) {
                restrictTo(this.blockPosition(), 5);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() == null;
    }

    @Override
    public void setHome(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (tryCureSanguinare(player)) return ActionResultType.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isShiftKeyDown() && hand == Hand.MAIN_HAND) {
            int lvl = VampirismPlayerAttributes.get(player).hunterLevel;
            if (!this.level.isClientSide && lvl > 0) {
                int levelCorrect = HunterLevelingConf.instance().isLevelValidForTrainer(lvl + 1);
                if (levelCorrect == 0) {
                    if (trainee == null) {
                        player.openMenu(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new HunterTrainerContainer(id, playerInventory, this), name));
                        this.trainee = player;
                        this.getNavigation().stop();
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.i_am_busy_right_now"), Util.NIL_UUID);
                    }

                } else if (levelCorrect == -1) {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.hunter_trainer.trainer_level_wrong"), Util.NIL_UUID);
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.hunter_trainer.trainer_level_to_high"), Util.NIL_UUID);
                }

            }

            return ActionResultType.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }


}
