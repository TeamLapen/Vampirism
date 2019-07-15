package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.goals.BiteNearbyEntityVampireGoal;
import de.teamlapen.vampirism.entity.goals.FleeSunVampireGoal;
import de.teamlapen.vampirism.entity.goals.MoveToBiteableVampireGoal;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements IConvertedCreature<VillagerEntity> {

    private EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (!world.isRemote && entity instanceof PlayerEntity && !UtilLib.canReallySee((LivingEntity) entity, this, true) && rand.nextInt(Balance.mobProps.VAMPIRE_BITE_ATTACK_CHANCE) == 0) {
            int amt = VampirePlayer.get((PlayerEntity) entity).onBite(this);
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
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
        bloodTimer = -1200 - rand.nextInt(1200);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
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
    public void livingTick() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 42));
            }
            if (isGettingGarlicDamage() != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), this.ticksExisted);
            }
        }
        bloodTimer++;
        super.livingTick();
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        bloodTimer = 0;
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return bloodTimer > 0;
    }

    @Override
    protected Brain<?> createBrain(Dynamic<?> p_213364_1_) {
        Brain<?> brain = super.createBrain(p_213364_1_);
        return brain;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new RestrictSunGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<CreatureEntity>(this, CreatureEntity.class, 10, 0.45F, 0.55F, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, true, false, false, VReference.HUNTER_FACTION)));
        this.goalSelector.addGoal(5, new FleeSunVampireGoal<>(this, 0.6F, true));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 0.6F, false));
        this.goalSelector.addGoal(7, new BiteNearbyEntityVampireGoal<>(this));
        this.goalSelector.addGoal(9, new MoveToBiteableVampireGoal<>(this, 0.55F));


        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

    }

    private void addAdditionalRecipes(MerchantOffers offers) {
        if (offers.size() > 0) {
            offers.remove(rand.nextInt(offers.size()));
        }
        List<MerchantOffer> trades = Lists.newArrayList();
        addRecipe(trades, new ItemStack(ModItems.human_heart, 9), 2, this.getRNG(), 0.5F);
        addRecipe(trades, 3, new ItemStack(ModItems.human_heart, 9), this.getRNG(), 0.5F);
        ItemStack bottle = new ItemStack(ModItems.blood_bottle, 3);
        bottle.setDamage(9);
        addRecipe(trades, 1, bottle, rand, 0.9F);

        offers.addAll(trades);
    }

    /**
     * Add a recipe to BUY something for emeralds
     */
    private void addRecipe(List list, int emeralds, ItemStack stack, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), stack, 8, 2, 0.2F));
        }
    }

    /**
     * Add a recipe to SELL something for emeralds
     */
    private void addRecipe(List list, ItemStack stack, int emeralds, Random rnd, float prop) {
        if (rnd.nextFloat() < prop) {
            list.add(new MerchantOffer(stack, new ItemStack(Items.EMERALD, emeralds), 8, 2, 0.2F));
        }
    }

    public static class ConvertingHandler implements IConvertingHandler<VillagerEntity> {

        @Override
        public IConvertedCreature<VillagerEntity> createFrom(VillagerEntity entity) {
            CompoundNBT nbt = new CompoundNBT();
            entity.writeWithoutTypeId(nbt);
            ConvertedVillagerEntity converted = ModEntities.villager_converted.create(entity.world);
            converted.read(nbt);
            converted.setUniqueId(MathHelper.getRandomUUID(converted.rand));
            converted.addAdditionalRecipes(converted.getOffers());
            return converted;
        }
    }
}
