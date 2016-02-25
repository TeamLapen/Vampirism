package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ai.VampireAIBiteNearbyEntity;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeGarlic;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.ai.VampireAIMoveToBiteable;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;


public class EntityBasicVampire extends EntityVampireBase implements IBasicVampire {

    private final int ID_LEVEL = 16;
    private final int MAX_LEVEL = 2;
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private int bloodtimer = 100;
    /**
     * True after the datawatcher has been initialized.
     */
    private boolean datawatcher_init = false;

    public EntityBasicVampire(World world) {
        super(world, true);

        getDataWatcher().addObject(ID_LEVEL, -1);
        datawatcher_init = true;
        hasArms = true;

        this.setSize(0.6F, 1.8F);
        if (world.getDifficulty() == EnumDifficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new EntityAIBreakDoor(this));
            ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks.addTask(2, new EntityAIAvoidEntity<>(this, EntityCreature.class, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, VReference.HUNTER_FACTION), 10, 1.0, 1.1));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
        this.tasks.addTask(3, new VampireAIFleeGarlic(this, 0.9, false));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(5, new VampireAIBiteNearbyEntity(this));
        this.tasks.addTask(6, new VampireAIMoveToBiteable(this, 0.75));
        this.tasks.addTask(7, new EntityAIMoveThroughVillage(this, 0.6, true));
        this.tasks.addTask(8, new EntityAIWander(this, 0.7));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, null)));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, null)));//TODO maybe make them not attack hunters, although it looks interesting

    }

    @Override
    public void consumeBlood(int amt, float saturationMod) {
        super.consumeBlood(amt, saturationMod);
        bloodtimer += amt * 40 + this.getRNG().nextInt(1000);
    }

    @Override
    public int getLevel() {
        return datawatcher_init ? getDataWatcher().getWatchableObjectInt(ID_LEVEL) : -1;
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            this.updateEntityAttributes();
            if (level == 2) {
                this.addPotionEffect(new PotionEffect(Potion.resistance.id, 1000000, 1));
            }
            if (level == 1) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
            } else {
                this.setCurrentItemOrArmor(0, null);
            }
            getDataWatcher().updateObject(ID_LEVEL, level);
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getTalkInterval() {
        return 400;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (bloodtimer > 0) {
            bloodtimer--;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        if (tagCompund.hasKey("level")) {
            setLevel(tagCompund.getInteger("level"));
        }

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
    public boolean wantsBlood() {
        return bloodtimer == 0;
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
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (recentlyHit) {
            if (this.rand.nextInt(3) == 0) {
                this.dropItem(ModItems.vampireFang, 1);
            }
        }
    }

    @Override
    protected String getLivingSound() {
        return REFERENCE.MODID + ":entity.vampire.scream";
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Balance.mobProps.VAMPIRE_MAX_HEALTH + Balance.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Balance.mobProps.VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Balance.mobProps.VAMPIRE_SPEED);
    }

}
