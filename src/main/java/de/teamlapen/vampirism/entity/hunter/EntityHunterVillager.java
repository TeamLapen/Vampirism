package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.EntityVillagerVampirism;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.Iterator;

/**
 * Villager that is equipped with a fork and hunts vampires
 */
public class EntityHunterVillager extends EntityVillagerVampirism implements IHunterMob {
    public static EntityHunterVillager makeHunter(EntityVillager villager) {
        EntityHunterVillager hunter = new EntityHunterVillager(villager.worldObj);
        NBTTagCompound nbt = new NBTTagCompound();
        villager.writeToNBT(nbt);
        hunter.readFromNBT(nbt);
        hunter.setUniqueId(MathHelper.getRandomUuid(hunter.rand));
        hunter.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.pitchfork));
        return hunter;
    }

    public static EntityVillager makeNormal(EntityHunterVillager hunter) {
        EntityVillager villager = new EntityVillager(hunter.worldObj);
        NBTTagCompound nbt = new NBTTagCompound();
        hunter.writeToNBT(nbt);
        villager.readFromNBT(nbt);
        villager.setUniqueId(MathHelper.getRandomUuid(villager.getRNG()));

        return villager;
    }

    public EntityHunterVillager(World worldIn) {
        super(worldIn);
    }

    @Override
    public IFaction getFaction() {
        return VReference.HUNTER_FACTION;
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
    protected void initEntityAI() {
        super.initEntityAI();
        Iterator<EntityAITasks.EntityAITaskEntry> it = this.tasks.taskEntries.iterator();
        while (it.hasNext()) {
            EntityAITasks.EntityAITaskEntry entry = it.next();
            if (entry.action instanceof EntityAITradePlayer || entry.action instanceof EntityAILookAtTradePlayer || entry.action instanceof EntityAIVillagerMate) {
                it.remove();
            }
        }
        this.tasks.addTask(6, new EntityAIAttackMelee(this, 0.6, false));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }
}
