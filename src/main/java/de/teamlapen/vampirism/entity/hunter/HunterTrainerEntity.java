package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class HunterTrainerEntity extends HunterBaseEntity implements ForceLookEntityGoal.TaskOwner, ICaptureIgnore {
    private static final ITextComponent name = new TranslationTextComponent("container.huntertrainer");
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private PlayerEntity trainee;
    private boolean shouldCreateHome;

    public HunterTrainerEntity(EntityType<? extends HunterTrainerEntity> type, World world) {
        super(type, world, false);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);

        this.setDontDropEquipment();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
//        if(VampirismMod.inDev){
        ServerLifecycleHooks.getCurrentServer().sendMessage(new StringTextComponent("Damage: " + amount + " from " + source.toString()), Util.DUMMY_UUID);
//        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer) && getHome() == null;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
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
    public void livingTick() {
        super.livingTick();
        if (trainee != null && !(trainee.openContainer instanceof HunterTrainerContainer)) {
            this.trainee = null;
        }
    }

    @Override
    public void setHome(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .createMutableAttribute(SharedMonsterAttributes.MAX_HEALTH, 300)
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 19)
                .createMutableAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.17)
                .createMutableAttribute(SharedMonsterAttributes.FOLLOW_RANGE, 5);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putBoolean("createHome", this.shouldCreateHome);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("createHome") && (this.shouldCreateHome = nbt.getBoolean("createHome"))) {
            if (this.getHomePosition().equals(BlockPos.ZERO)) {
                setHomePosAndDistance(this.getPosition(), 5);
            }
        }
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (tryCureSanguinare(player)) return ActionResultType.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isSneaking()) {
            int lvl=HunterPlayer.getOpt(player).map(VampirismPlayer::getLevel).orElse(0);
            if (!this.world.isRemote && lvl>0) {
                int levelCorrect = HunterLevelingConf.instance().isLevelValidForTrainer(lvl+ 1);
                if (levelCorrect == 0) {
                    if (trainee == null) {
                        player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new HunterTrainerContainer(id, playerInventory, this), name));
                        this.trainee = player;
                        this.getNavigator().clearPath();
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.i_am_busy_right_now"), Util.DUMMY_UUID);
                    }

                } else if (levelCorrect == -1) {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.hunter_trainer.trainer_level_wrong"), Util.DUMMY_UUID);
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.hunter_trainer.trainer_level_to_high"), Util.DUMMY_UUID);
                }

            }

            return ActionResultType.SUCCESS;
        }


        return super.func_230254_b_(player, hand);
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
