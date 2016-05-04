package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Exists in {@link EntityBasicHunter#MAX_LEVEL}+1 different levels
 */
public class EntityBasicHunter extends EntityHunterBase implements IBasicHunter {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityBasicHunter.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 3;
    private final int MOVE_TO_RESTRICT_PRIO = 3;


    public EntityBasicHunter(World world) {
        super(world, true);

        saveHome = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.8F);


        this.setDontDropEquipment();
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag && this.getHeldItemMainhand() == null) {
            this.swingArm(EnumHand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            this.updateEntityAttributes();
            if (level == 3) {
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 1));
            }
            getDataManager().set(LEVEL, level);
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public boolean isLookingForHome() {
        return !hasHome();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        if (tagCompund.hasKey("level")) {
            setLevel(tagCompund.getInteger("level"));
        }

    }

    @Override
    public void setCampArea(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO);
    }

    @Override
    public int suggestLevel(Difficulty d) {
        switch (this.rand.nextInt(5)) {
            case 0:
                return (int) (d.minPercLevel / 100F * MAX_LEVEL);
            case 1:
                return (int) (d.avgPercLevel / 100F * MAX_LEVEL);
            case 2:
                return (int) (d.maxPercLevel / 100F * MAX_LEVEL);
            default:
                return this.rand.nextInt(MAX_LEVEL + 1);
        }

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getLevel());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected boolean canDespawn() {
        return isLookingForHome() && super.canDespawn();
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (recentlyHit) {
            if (this.rand.nextInt(3) == 0) {
                this.dropItem(ModItems.humanHeart, 1);
            }
            }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(LEVEL, -1);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 6 + getLevel();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0, false));

        this.tasks.addTask(6, new EntityAIWander(this, 0.7, 50));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityVampireBase.class, 17F));
        this.tasks.addTask(8, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_SPEED);
    }
}
