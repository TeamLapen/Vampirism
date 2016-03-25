package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.ai.HunterAILookAtTrainee;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.network.ModGuiHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class EntityHunterTrainer extends EntityHunterBase {
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private EntityPlayer trainee;

    public EntityHunterTrainer(World world) {
        super(world, false);
        saveHome = true;
        hasArms = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.8F);


        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(3, new HunterAILookAtTrainee(this));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityVampireBase.class, 17F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, null)));
        this.setEquipmentDropChance(0, 0);
    }


    public void setHome(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO);
    }

    @Override
    protected boolean canDespawn() {
        return !hasHome() && super.canDespawn();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(40);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(5);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !player.isSneaking()) {
            if (!this.worldObj.isRemote) {
                if (HunterLevelingConf.instance().isLevelValidForTrainer(FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION))) {
                    this.trainee = player;
                    player.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_TRAINER, player.worldObj, getPosition().getX(), getPosition().getY(), getPosition().getZ());
                } else {
                    player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.trainer_level_wrong"));
                }

            }

            return true;
        } else {
            return super.interact(player);
        }
    }

    /**
     * @return The player which has the trainings gui open. Can be null
     */
    public EntityPlayer getTrainee() {
        return trainee;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (trainee != null && trainee.openContainer == null) {
            this.trainee = null;
        }
    }
}
