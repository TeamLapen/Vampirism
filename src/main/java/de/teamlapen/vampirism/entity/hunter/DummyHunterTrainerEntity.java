package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
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
public class DummyHunterTrainerEntity extends VampirismEntity {
    private final int MOVE_TO_RESTRICT_PRIO = 3;

    public DummyHunterTrainerEntity(EntityType<? extends DummyHunterTrainerEntity> type, World world) {
        super(type, world);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigator) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.95F);
        this.setDontDropEquipment();
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
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
    public boolean canDespawn() {
        return !hasHome() && super.canDespawn();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new OpenDoorGoal(this, true));
        this.tasks.addTask(4, new MeleeAttackGoal(this, 1.0, false));
        this.tasks.addTask(6, new RandomWalkingGoal(this, 0.7));
        this.tasks.addTask(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.tasks.addTask(9, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.tasks.addTask(10, new LookRandomlyGoal(this));

        this.targetTasks.addTask(1, new HurtByTargetGoal(this, false));
    }


    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isSneaking()) {
            if (!this.world.isRemote) {
            	if(Helper.isHunter(player)) {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.trainer_disabled_hunter"));
            	}else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.trainer_disabled"));
            	}
            }
            return true;
        }


        return super.processInteract(player, hand);
    }


}
