package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.ai.HunterAILookAtTrainee;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class EntityHunterTrainer extends EntityHunterBase implements HunterAILookAtTrainee.ITrainer {
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private EntityPlayer trainee;

    public EntityHunterTrainer(World world) {
        super(ModEntities.hunter_trainer, world, false);
        saveHome = true;
        hasArms = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.95F);
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
    public EntityPlayer getTrainee() {
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
    public boolean canDespawn() {
        return !hasHome() && super.canDespawn();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(5, new HunterAILookAtTrainee(this));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityVampireBase.class, 17F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }


    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof ItemSpawnEgg;

        if (!flag && this.isAlive() && !player.isSneaking()) {
            if (!this.world.isRemote) {
                if (HunterLevelingConf.instance().isLevelValidForTrainer(FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION) + 1)) {
                    if (trainee == null) {
                        this.trainee = player;
                        //player.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_TRAINER, player.getEntityWorld(), getPosition().getX(), getPosition().getY(), getPosition().getZ());//TODO 1.14
                    } else {
                        player.sendMessage(new TextComponentTranslation("text.vampirism.i_am_busy_right_now"));
                    }

                } else {
                    player.sendMessage(new TextComponentTranslation("text.vampirism.trainer_level_wrong"));
                }

            }

            return true;
        }


        return super.processInteract(player, hand);
    }


}
