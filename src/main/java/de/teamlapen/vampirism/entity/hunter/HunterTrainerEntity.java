package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.ai.LookAtTrainerHunterGoal;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class HunterTrainerEntity extends HunterBaseEntity implements LookAtTrainerHunterGoal.ITrainer {
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private PlayerEntity trainee;

    public HunterTrainerEntity(EntityType<? extends HunterTrainerEntity> type, World world) {
        super(type, world, false);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);

        this.setDontDropEquipment();
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    /**
     * @return The player which has the trainings gui open. Can be null
     */
    @Override
    public PlayerEntity getTrainee() {
        return trainee;
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

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(19);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(5);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer) && getHome() != null;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new LookAtTrainerHunterGoal(this));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }


    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isSneaking()) {
            if (!this.world.isRemote) {
                if (HunterLevelingConf.instance().isLevelValidForTrainer(FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION) + 1)) {
                    if (trainee == null) {
                        this.trainee = player;
                        //player.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_TRAINER, player.getEntityWorld(), getPosition().getX(), getPosition().getY(), getPosition().getZ());//TODO 1.14
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.i_am_busy_right_now"));
                    }

                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.trainer_level_wrong"));
                }

            }

            return true;
        }


        return super.processInteract(player, hand);
    }


}
