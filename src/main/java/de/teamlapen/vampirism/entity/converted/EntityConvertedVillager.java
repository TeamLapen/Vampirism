package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityVillagerVampirism;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveIndoorsDay;
import de.teamlapen.vampirism.entity.ai.VampireAIBiteNearbyEntity;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.ai.VampireAIMoveToBiteable;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Random;

/**
 * Vampire Villager
 */
public class EntityConvertedVillager extends EntityVillagerVampirism implements IConvertedCreature<EntityVillager> {

    private EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private boolean addedAdditionalRecipes = false;
    private int bloodTimer = 0;

    public EntityConvertedVillager(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (!world.isRemote && entity instanceof EntityPlayer && !UtilLib.canReallySee((EntityLivingBase) entity, this, true) && rand.nextInt(Balance.mobProps.VAMPIRE_BITE_ATTACK_CHANCE) == 0) {
            int amt = VampirePlayer.get((EntityPlayer) entity).onBite(this);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            return true;
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
        bloodTimer = -1200 - rand.nextInt(1200);
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
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                this.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 42));
            }
            if (isGettingGarlicDamage() != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), this.ticksExisted);
            }
        }
        bloodTimer++;
        super.onLivingUpdate();
    }

    @Override
    public void readAdditional(NBTTagCompound compound) {
        super.readAdditional(compound);
        if (compound.contains("addedAdditionalRecipes")) {
            addedAdditionalRecipes = compound.getBoolean("addedAdditionalRecipes");
        }
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, amt * 20));
        bloodTimer = 0;
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return bloodTimer > 0;
    }

    @Override
    public void writeAdditional(NBTTagCompound compound) {
        super.writeAdditional(compound);
        compound.putBoolean("addedAdditionalRecipes", addedAdditionalRecipes);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof EntityAIMoveIndoors || entry.action instanceof EntityAIVillagerMate || entry.action instanceof EntityAIFollowGolem);

        tasks.addTask(0, new EntityAIRestrictSun(this));
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityCreature.class, 10, 0.45F, 0.55F, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, true, false, false, VReference.HUNTER_FACTION)));
        tasks.addTask(2, new EntityAIMoveIndoorsDay(this));
        tasks.addTask(5, new VampireAIFleeSun(this, 0.6F, true));
        tasks.addTask(6, new EntityAIAttackMelee(this, 0.6F, false));
        tasks.addTask(7, new VampireAIBiteNearbyEntity(this));
        tasks.addTask(9, new VampireAIMoveToBiteable(this, 0.55F));


        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

    }

    private void addAdditionalRecipes(MerchantRecipeList list) {
        if (list.size() > 0) {
            list.remove(rand.nextInt(list.size()));
        }
        addRecipe(list, new ItemStack(ModItems.human_heart, 9), 2, this.getRNG(), 0.5F);
        addRecipe(list, 3, new ItemStack(ModItems.human_heart, 9), this.getRNG(), 0.5F);
        addRecipe(list, 1, new ItemStack(ModItems.blood_bottle, 3, ItemBloodBottle.AMOUNT), rand, 0.9F);
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
            entity.writeWithoutTypeId(nbt);
            EntityConvertedVillager converted = new EntityConvertedVillager(entity.world);
            converted.read(nbt);
            converted.setUniqueId(MathHelper.getRandomUUID(converted.rand));
            return converted;
        }
    }
}
