package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.EntityVillagerVampirism;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveIndoorsDay;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * Vampire Villager
 */
public class EntityConvertedVillager extends EntityVillagerVampirism implements IConvertedCreature<EntityVillager> {

    private EnumGarlicStrength garlicCache;
    private boolean sundamageCache;
    private boolean addedAdditionalRecipes = false;

    public EntityConvertedVillager(World worldIn) {
        super(worldIn);
    }

    @Override
    public void consumeBlood(int amt, float saturationMod) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
    }

    @Override
    public boolean doesResistGarlic(EnumGarlicStrength strength) {
        return false;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        MerchantRecipeList list = super.getRecipes(player);
        if (!addedAdditionalRecipes) {
            addAdditionalRecipes(list);
            Collections.shuffle(list);
            addedAdditionalRecipes = true;
        }
        return list;
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage() {
        return isGettingGarlicDamage(false);
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage(boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.gettingGarlicDamage(this);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this));
    }

    @Override
    public boolean isGettingSundamage() {
        return isGettingSundamage(false);
    }

    @Override
    public void onLivingUpdate() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!worldObj.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                this.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 42));
            }
            //TODO handle garlic
        }
        super.onLivingUpdate();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("addedAdditionalRecipes")) {
            addedAdditionalRecipes = compound.getBoolean("addedAdditionalRecipes");
        }
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("addedAdditionalRecipes", addedAdditionalRecipes);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        Iterator<EntityAITasks.EntityAITaskEntry> it = this.tasks.taskEntries.iterator();
        while (it.hasNext()) {
            EntityAITasks.EntityAITaskEntry entry = it.next();
            if (entry.action instanceof EntityAIMoveIndoors || entry.action instanceof EntityAIVillagerMate) {
                it.remove();
            }
        }

        tasks.addTask(0, new EntityAIRestrictSun(this));
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityCreature.class, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, true, false, VReference.HUNTER_FACTION), 10, 0.5F, 0.6F));
        tasks.addTask(2, new EntityAIMoveIndoorsDay(this));
        tasks.addTask(5, new VampireAIFleeSun(this, 0.6F, true));
        tasks.addTask(6, new EntityAIAttackMelee(this, 0.6, false));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

    }

    private void addAdditionalRecipes(MerchantRecipeList list) {
        if (list.size() > 0) {
            list.remove(rand.nextInt(list.size()));
        }
        addRecipe(list, new ItemStack(ModItems.humanHeart, 9), 2, this.getRNG(), 0.5F);
        addRecipe(list, 3, new ItemStack(ModItems.humanHeart, 9), this.getRNG(), 0.5F);
        addRecipe(list, 1, new ItemStack(ModItems.bloodBottle, 3, ItemBloodBottle.AMOUNT), rand, 0.9F);
    }

    /**
     * Add a recipe to BUY something for emeralds
     */
    private void addRecipe(MerchantRecipeList list, int emeralds, ItemStack stack, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeralds), stack));
        }
    }

    /**
     * Add a recipe to SELL something for emeralds
     */
    private void addRecipe(MerchantRecipeList list, ItemStack stack, int emeralds, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeralds), stack));
        }
    }

    public static class ConvertingHandler implements IConvertingHandler<EntityVillager> {

        @Override
        public IConvertedCreature<EntityVillager> createFrom(EntityVillager entity) {
            NBTTagCompound nbt = new NBTTagCompound();
            entity.writeToNBT(nbt);
            EntityConvertedVillager converted = new EntityConvertedVillager(entity.worldObj);
            converted.readFromNBT(nbt);
            converted.setUniqueId(MathHelper.getRandomUuid(converted.rand));
            return converted;
        }
    }
}
