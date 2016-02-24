package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.factions.PredicateFactionHostile;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Exists in {@link EntityBasicHunter#MAX_LEVEL}+1 different levels
 */
public class EntityBasicHunter extends EntityHunterBase implements IBasicHunter {
    private final int ID_LEVEL = 16;
    private final int MAX_LEVEL = 3;
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    /**
     * True after the datawatcher has been initialized.
     */
    private boolean datawatcher_init = false;

    public EntityBasicHunter(World world) {
        super(world, true);
        getDataWatcher().addObject(ID_LEVEL, -1);
        datawatcher_init = true;
        saveHome = true;
        hasArms = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.8F);


        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0, false));

        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityVampireBase.class, 17F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, new PredicateFactionHostile(getFaction(), true, false, false)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, new PredicateFactionHostile(getFaction(), false, true, false)));
        this.setEquipmentDropChance(0, 0);
    }

    @Override
    public int getLevel() {
        return datawatcher_init ? getDataWatcher().getWatchableObjectInt(ID_LEVEL) : -1;
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            this.updateEntityAttributes();
            if (level == 3) {
                this.addPotionEffect(new PotionEffect(Potion.resistance.id, 1000000, 1));
            }
            getDataWatcher().updateObject(ID_LEVEL, level);
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
        if (d.maxPercLevel == 100) {
            if (this.rand.nextInt((d.maxPercLevel - d.avgPercLevel) / 10 + 2) == 0) {
                return MAX_LEVEL;
            }
        }
        return this.rand.nextInt(MAX_LEVEL);
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

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_SPEED);
    }
}
