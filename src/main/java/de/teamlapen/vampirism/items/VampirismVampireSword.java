package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class VampirismVampireSword extends VampirismItemWeapon implements IBloodChargeable, IFactionExclusiveItem, IFactionLevelItem {


    public static final String DO_NOT_NAME_STRING = "DO_NOT_NAME";
    /**
     * Minimal strength modifier
     */
    private final float minStrength = 0.2f;
    /**
     * Minimal speed modifier
     */
    private final float trainedAttackSpeed;
    private final float untrainedAttackSpeed;


    public VampirismVampireSword(ItemTier material, int attackDamage, float untrainedAttackSpeed, float trainedAttackSpeed, Properties prop) {
        super(material, attackDamage, untrainedAttackSpeed, prop);
        this.trainedAttackSpeed = trainedAttackSpeed;
        this.untrainedAttackSpeed = untrainedAttackSpeed;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        float charged = getCharged(stack);
        float trained = getTrained(stack, VampirismMod.proxy.getClientPlayer());
        tooltip.add(new TranslationTextComponent("text.vampirism.sword_charged").append(new StringTextComponent(" " + ((int) Math.ceil(charged * 100f)) + "%")).withStyle(TextFormatting.DARK_AQUA));
        tooltip.add(new TranslationTextComponent("text.vampirism.sword_trained").append(new StringTextComponent(" " + ((int) Math.ceil(trained * 100f)) + "%")).withStyle(TextFormatting.DARK_AQUA));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        this.addFactionLevelToolTip(stack, worldIn, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.FIRE_ASPECT) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean canBeCharged(ItemStack stack) {
        return getCharged(stack) < 1f;
    }

    @Override
    public int charge(ItemStack stack, int amount) {
        float factor = getChargingFactor(stack);
        float charge = getCharged(stack);
        float actual = Math.min(factor * amount, 1f - charge);
        this.setCharged(stack, charge + actual);
        return (int) (actual / factor);
    }

    /**
     * Prevent the player from being asked to name this item
     */
    public void doNotName(ItemStack stack) {
        stack.addTagElement("dont_name", ByteNBT.valueOf(Byte.MAX_VALUE));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Nullable
    @Override
    public IPlayableFaction getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!(entityLiving instanceof PlayerEntity)) return stack;
        VReference.VAMPIRE_FACTION.getPlayerCapability((PlayerEntity) entityLiving).ifPresent(vampire -> {
            int amount = (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.BLOOD_CHARGE_SPEED.get()) ? VampirismConfig.BALANCE.vrBloodChargeSpeedMod.get() : 2);
            if (((PlayerEntity) entityLiving).isCreative() || vampire.useBlood(amount, false)) {
                this.charge(stack, amount * VReference.FOOD_TO_FLUID_BLOOD);
            }
        });
        if (getCharged(stack) == 1) {
            tryName(stack, (PlayerEntity) entityLiving);
        }
        return stack;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //Vampire Finisher skill
        if (attacker instanceof PlayerEntity && !Helper.isVampire(target)) {
            double relTh = VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * VampirePlayer.getOpt((PlayerEntity) attacker).map(VampirePlayer::getSkillHandler).map(h -> h.isSkillEnabled(VampireSkills.SWORD_FINISHER.get()) ? (h.isRefinementEquipped(ModRefinements.SWORD_FINISHER.get()) ? VampirismConfig.BALANCE.vrSwordFinisherThresholdMod.get() : 1d) : 0d).orElse(0d);
            if (relTh > 0 && target.getHealth() <= target.getMaxHealth() * relTh) {
                DamageSource dmg = DamageSource.playerAttack((PlayerEntity) attacker).bypassArmor();
                target.hurt(dmg, 10000F);
                Vector3d center = Vector3d.atLowerCornerOf(target.blockPosition());
                center.add(0, target.getBbHeight() / 2d, 0);
                ModParticles.spawnParticlesServer(target.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "effect_4"), 12, 0xE02020), center.x, center.y, center.z, 15, 0.5, 0.5, 0.5, 0);
            }
        }
        //Update training on kill
        if (target.getHealth() <= 0.0f && Helper.isVampire(attacker)) {
            float trained = getTrained(stack, attacker);
            int exp = target instanceof PlayerEntity ? 10 : (attacker instanceof PlayerEntity ? (Helper.getExperiencePoints(target, (PlayerEntity) attacker)) : 5);
            float newTrained = exp / 5f * (1.0f - trained) / 15f;
            if (attacker instanceof PlayerEntity && VampirePlayer.getOpt(((PlayerEntity) attacker)).map(VampirePlayer::getSkillHandler).map(handler -> handler.isRefinementEquipped(ModRefinements.SWORD_TRAINED_AMOUNT.get())).orElse(false)) {
                newTrained *= VampirismConfig.BALANCE.vrSwordTrainingSpeedMod.get();
            }
            trained += newTrained;
            setTrained(stack, attacker, trained);
        }
        //Consume blood
        float charged = getCharged(stack);
        charged -= getChargeUsage();
        setCharged(stack, charged);
        attacker.setItemInHand(Hand.MAIN_HAND, stack);

        return super.hurtEnemy(stack, target, attacker);
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getCharged(stack) == 1f;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return !Helper.isVampire(entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //Try to minimize execution time, but tricky since off hand selection is not directly available, but it can only be off hand if itemSlot 0
        if (worldIn.isClientSide && (isSelected || itemSlot == 0)) {
            float charged = getCharged(stack);
            if (charged > 0 && entityIn.tickCount % ((int) (20 + 100 * (1f - charged))) == 0 && entityIn instanceof LivingEntity) {
                boolean secondHand = !isSelected && ((LivingEntity) entityIn).getItemInHand(Hand.OFF_HAND).equals(stack);
                if (isSelected || secondHand) {
                    spawnChargedParticle((LivingEntity) entityIn, isSelected);
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.getCommandSenderWorld().isClientSide) {
            if (count % 3 == 0) {
                spawnChargingParticle(player, player.getMainHandItem().equals(stack));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        return !Helper.isVampire(player);
    }

    /**
     * Might want to use {@link #charge(ItemStack, int)} instead to charge it with mB of blood
     *
     * @param value Is clamped between 0 and 1
     */
    public void setCharged(@Nonnull ItemStack stack, float value) {
        stack.addTagElement("charged", FloatNBT.valueOf(MathHelper.clamp(value, 0f, 1f)));
    }

    /**
     * Sets the stored trained value for the given player
     *
     * @param value Clamped between 0 and 1
     */
    public void setTrained(@Nonnull ItemStack stack, @Nonnull LivingEntity player, float value) {
        CompoundNBT nbt = stack.getOrCreateTagElement("trained");
        nbt.putFloat(player.getUUID().toString(), MathHelper.clamp(value, 0f, 1f));
    }

    /**
     * If the stack is not named and the player hasn't been named before, ask the player to name this stack
     */
    public void tryName(ItemStack stack, PlayerEntity player) {
        if (!stack.hasCustomHoverName() && player.level.isClientSide() && (!stack.hasTag() || !stack.getTag().getBoolean("dont_name"))) {
            VampirismMod.proxy.displayNameSwordScreen(stack);
            player.level.playLocalSound((player).getX(), (player).getY(), (player).getZ(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f, false);
        }
    }

    /**
     * Updated during {@link net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent}
     *
     * @param stack
     * @param player
     * @return True if the cached value was updated
     */
    public boolean updateTrainedCached(@Nonnull ItemStack stack, @Nonnull LivingEntity player) {
        float cached = getTrained(stack);
        float trained = getTrained(stack, player);
        if (cached != trained) {
            stack.addTagElement("trained-cache", FloatNBT.valueOf(trained));
            return true;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        return VampirePlayer.getOpt(playerIn).map(vampire -> {
            if (vampire.getLevel() == 0) return new ActionResult<>(ActionResultType.PASS, stack);

            if (this.canBeCharged(stack) && playerIn.isShiftKeyDown() && vampire.getSkillHandler().isSkillEnabled(VampireSkills.BLOOD_CHARGE.get()) && (playerIn.isCreative() || vampire.getBloodLevel() >= (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.BLOOD_CHARGE_SPEED.get()) ? VampirismConfig.BALANCE.vrBloodChargeSpeedMod.get() : 2))) {
                playerIn.startUsingItem(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }

            return new ActionResult<>(ActionResultType.PASS, stack);
        }).orElse(new ActionResult<>(ActionResultType.PASS, stack));
    }

    @Override
    protected final float getAttackDamage(ItemStack stack) {
        return super.getAttackDamage(stack) * getAttackDamageModifier(stack);
    }

    protected float getAttackDamageModifier(ItemStack stack) {
        return getCharged(stack) > 0 ? 1f : minStrength;
    }

    @Override
    protected final float getAttackSpeed(ItemStack stack) {
        return untrainedAttackSpeed + (trainedAttackSpeed - untrainedAttackSpeed) * getTrained(stack);
    }

    /**
     * //TODO 1.17 make abstract
     *
     * @return The amount of charge consumed per hit
     */
    protected float getChargeUsage() {
        return 0;
    }

    /**
     * Gets the charged value from the tag compound
     *
     * @param stack
     * @return Value between 0 and 1
     */
    protected float getCharged(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag().getFloat("charged");
        }
        return 0.0f;
    }

    /**
     * @return Charging factor multiplied with amount to get charge percentage
     */
    protected abstract float getChargingFactor(ItemStack stack);

    /**
     * Gets a cached trained value from the tag compound
     *
     * @param stack
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbt = stack.getTag();
            if (nbt.contains("trained-cache")) {
                return nbt.getFloat("trained-cache");
            }
        }
        return 0.0f;
    }

    /**
     * Gets the trained value from the tag compound
     *
     * @param stack
     * @param player
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@Nonnull ItemStack stack, @Nullable LivingEntity player) {
        if (player == null) return getTrained(stack);
        UUID id = player.getUUID();
        CompoundNBT nbt = stack.getTagElement("trained");
        if (nbt != null) {
            if (nbt.contains(id.toString())) {
                return nbt.getFloat(id.toString());
            }
        }
        return 0f;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargedParticle(LivingEntity player, boolean mainHand) {
        Vector3d mainPos = UtilLib.getItemPosition(player, mainHand);
        for (int j = 0; j < 3; ++j) {
            Vector3d pos = mainPos.add((player.getRandom().nextFloat() - 0.5f) * 0.1f, (player.getRandom().nextFloat() - 0.3f) * 0.9f, (player.getRandom().nextFloat() - 0.5f) * 0.1f);
            ModParticles.spawnParticleClient(player.getCommandSenderWorld(), new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (player.getRandom().nextFloat() * 0.9F + 0.1F)), true, pos.x + (player.getRandom().nextFloat() - 0.5D) * 0.1D, pos.y + (player.getRandom().nextFloat() - 0.5D) * 0.1D, pos.z + (player.getRandom().nextFloat() - 0.5D) * 0.1D, new ResourceLocation("minecraft", "glitter_1")), pos.x, pos.y, pos.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargingParticle(LivingEntity player, boolean mainHand) {
        Vector3d pos = UtilLib.getItemPosition(player, mainHand);
        if (player.getAttackAnim(1f) > 0f) return;
        pos = pos.add((player.getRandom().nextFloat() - 0.5f) * 0.1f, (player.getRandom().nextFloat() - 0.3f) * 0.9f, (player.getRandom().nextFloat() - 0.5f) * 0.1f);
        Vector3d playerPos = new Vector3d((player).getX(), (player).getY() + player.getEyeHeight() - 0.2f, (player).getZ());
        ModParticles.spawnParticleClient(player.getCommandSenderWorld(), new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (player.getRandom().nextFloat() * 0.6F + 0.1F)), true, pos.x, pos.y, pos.z), playerPos.x, playerPos.y, playerPos.z);
    }
}
