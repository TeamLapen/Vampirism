package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.EntityVillagerVampirism;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveThroughVillageCustom;
import de.teamlapen.vampirism.entity.ai.HunterAIDefendVillage;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.Iterator;

/**
 * Villager that is equipped with a fork and hunts vampires
 */
public class EntityHunterVillager extends EntityVillagerVampirism implements IHunterMob, HunterAIDefendVillage.IVillageHunterCreature {
    /**
     * Creates a hunter villager as an copy to the given villager
     *
     * @param villager Is not modified
     * @return
     */
    public static EntityHunterVillager makeHunter(EntityVillager villager) {
        EntityHunterVillager hunter = new EntityHunterVillager(villager.world);
        NBTTagCompound nbt = new NBTTagCompound();
        villager.writeToNBT(nbt);
        hunter.readFromNBT(nbt);
        hunter.setUniqueId(MathHelper.getRandomUUID(hunter.rand));
        hunter.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.pitchfork));
        return hunter;
    }

    /**
     * Creates a villager as an copy to the given hunter
     *
     * @param hunter Is not modified
     * @return
     */
    public static EntityVillager makeNormal(EntityHunterVillager hunter) {
        EntityVillager villager = new EntityVillager(hunter.world);
        NBTTagCompound nbt = new NBTTagCompound();
        hunter.writeToNBT(nbt);
        villager.readFromNBT(nbt);
        villager.setUniqueId(MathHelper.getRandomUUID(villager.getRNG()));

        return villager;
    }

    public EntityHunterVillager(World worldIn) {
        super(worldIn);
        ((PathNavigateGround) getNavigator()).setEnterDoors(true);
    }

    @Override
    public IFaction getFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public EntityCreature getRepresentingCreature() {
        return this;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.pitchfork));
        return data;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        Iterator<EntityAITasks.EntityAITaskEntry> it = this.tasks.taskEntries.iterator();
        while (it.hasNext()) {
            EntityAITasks.EntityAITaskEntry entry = it.next();
            if (entry.action instanceof EntityAITradePlayer || entry.action instanceof EntityAILookAtTradePlayer || entry.action instanceof EntityAIVillagerMate || entry.action instanceof EntityAIFollowGolem) {
                it.remove();
            }
        }
        this.tasks.addTask(6, new EntityAIAttackMelee(this, 0.6, false));
        this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.55, false, 400));


        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetTasks.addTask(3, new HunterAIDefendVillage(this));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }
}
