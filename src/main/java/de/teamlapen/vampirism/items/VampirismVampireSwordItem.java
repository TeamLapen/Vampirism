package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class VampirismVampireSwordItem extends VampirismSwordItem implements IBloodChargeable, IFactionExclusiveItem, IFactionLevelItem<IVampirePlayer> {


    public static final String DO_NOT_NAME_STRING = "DO_NOT_NAME";
    /**
     * Minimal strength modifier
     */
    private static final float minStrength = 0.2f;
    /**
     * Minimal speed modifier
     */
    private final float trainedAttackSpeed;
    private final float untrainedAttackSpeed;


    public VampirismVampireSwordItem(@NotNull Tiers material, int attackDamage, float untrainedAttackSpeed, float trainedAttackSpeed, @NotNull Properties prop) {
        super(material, attackDamage, untrainedAttackSpeed, prop);
        this.trainedAttackSpeed = trainedAttackSpeed;
        this.untrainedAttackSpeed = untrainedAttackSpeed;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        this.addFactionLevelToolTip(stack, worldIn, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
        float charged = getCharged(stack);
        float trained = getTrained(stack, VampirismMod.proxy.getClientPlayer());
        tooltip.add(Component.translatable("text.vampirism.sword_charged").append(Component.literal(" " + ((int) Math.ceil(charged * 100f)) + "%")).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("text.vampirism.sword_trained").append(Component.literal(" " + ((int) Math.ceil(trained * 100f)) + "%")).withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.FIRE_ASPECT) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean canBeCharged(@NotNull ItemStack stack) {
        return getCharged(stack) < 1f;
    }

    @Override
    public int charge(@NotNull ItemStack stack, int amount) {
        float factor = getChargingFactor(stack);
        float charge = getCharged(stack);
        float actual = Math.min(factor * amount, 1f - charge);
        this.setCharged(stack, charge + actual);
        return (int) (actual / factor);
    }

    /**
     * Prevent the player from being asked to name this item
     */
    public void doNotName(@NotNull ItemStack stack) {
        stack.addTagElement("dont_name", ByteTag.valueOf(Byte.MAX_VALUE));
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public int getMinLevel(@NotNull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public ISkill<IVampirePlayer> getRequiredSkill(@NotNull ItemStack stack) {
        return null;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 40;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (!(entityLiving instanceof Player)) return stack;
        VReference.VAMPIRE_FACTION.getPlayerCapability((Player) entityLiving).ifPresent(vampire -> {
            int amount = (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.BLOOD_CHARGE_SPEED.get()) ? VampirismConfig.BALANCE.vrBloodChargeSpeedMod.get() : 2);
            if (((Player) entityLiving).isCreative() || vampire.useBlood(amount, false)) {
                this.charge(stack, amount * VReference.FOOD_TO_FLUID_BLOOD);
            }
        });
        if (getCharged(stack) == 1) {
            tryName(stack, (Player) entityLiving);
        }
        return stack;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        //Vampire Finisher skill
        if (attacker instanceof Player && !Helper.isVampire(target)) {
            double relTh = VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * VampirePlayer.getOpt((Player) attacker).map(VampirePlayer::getSkillHandler).map(h -> h.isSkillEnabled(VampireSkills.SWORD_FINISHER.get()) ? (h.isRefinementEquipped(ModRefinements.SWORD_FINISHER.get()) ? VampirismConfig.BALANCE.vrSwordFinisherThresholdMod.get() : 1d) : 0d).orElse(0d);
            if (relTh > 0 && target.getHealth() <= target.getMaxHealth() * relTh) {
                DamageSource dmg = DamageSource.playerAttack((Player) attacker).bypassArmor();
                target.hurt(dmg, 10000F);
                Vec3 center = Vec3.atLowerCornerOf(target.blockPosition());
                center.add(0, target.getBbHeight() / 2d, 0);
                ModParticles.spawnParticlesServer(target.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "effect_4"), 12, 0xE02020), center.x, center.y, center.z, 15, 0.5, 0.5, 0.5, 0);
            }
        }
        //Update training on kill
        if (target.getHealth() <= 0.0f && Helper.isVampire(attacker)) {
            float trained = getTrained(stack, attacker);
            int exp = target instanceof Player ? 10 : (attacker instanceof Player ? (Helper.getExperiencePoints(target, (Player) attacker)) : 5);
            float newTrained = exp / 5f * (1.0f - trained) / 15f;
            if (attacker instanceof Player && VampirePlayer.getOpt(((Player) attacker)).map(VampirePlayer::getSkillHandler).map(handler -> handler.isRefinementEquipped(ModRefinements.SWORD_TRAINED_AMOUNT.get())).orElse(false)) {
                newTrained *= VampirismConfig.BALANCE.vrSwordTrainingSpeedMod.get();
            }
            trained += newTrained;
            setTrained(stack, attacker, trained);
        }
        //Consume blood
        float charged = getCharged(stack);
        charged -= getChargeUsage();
        setCharged(stack, charged);
        attacker.setItemInHand(InteractionHand.MAIN_HAND, stack);

        return super.hurtEnemy(stack, target, attacker);
    }

    public boolean isFullyCharged(@NotNull ItemStack stack) {
        return getCharged(stack) == 1f;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return !Helper.isVampire(entity);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        //Try to minimize execution time, but tricky since off hand selection is not directly available, but it can only be off hand if itemSlot 0
        if (worldIn.isClientSide && (isSelected || itemSlot == 0)) {
            float charged = getCharged(stack);
            if (charged > 0 && entityIn.tickCount % ((int) (20 + 100 * (1f - charged))) == 0 && entityIn instanceof LivingEntity) {
                boolean secondHand = !isSelected && ((LivingEntity) entityIn).getItemInHand(InteractionHand.OFF_HAND).equals(stack);
                if (isSelected || secondHand) {
                    spawnChargedParticle((LivingEntity) entityIn, isSelected);
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, @NotNull LivingEntity player, int count) {
        if (player.getCommandSenderWorld().isClientSide) {
            if (count % 3 == 0) {
                spawnChargingParticle(player, player.getMainHandItem().equals(stack));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return !Helper.isVampire(player);
    }

    /**
     * Might want to use {@link #charge(ItemStack, int)} instead to charge it with mB of blood
     *
     * @param value Is clamped between 0 and 1
     */
    public void setCharged(@NotNull ItemStack stack, float value) {
        stack.addTagElement("charged", FloatTag.valueOf(Mth.clamp(value, 0f, 1f)));
    }

    /**
     * Sets the stored trained value for the given player
     *
     * @param value Clamped between 0 and 1
     */
    public void setTrained(@NotNull ItemStack stack, @NotNull LivingEntity player, float value) {
        CompoundTag nbt = stack.getOrCreateTagElement("trained");
        nbt.putFloat(player.getUUID().toString(), Mth.clamp(value, 0f, 1f));
    }

    /**
     * If the stack is not named and the player hasn't been named before, ask the player to name this stack
     */
    public void tryName(@NotNull ItemStack stack, @NotNull Player player) {
        if (!stack.hasCustomHoverName() && player.level.isClientSide() && (!stack.hasTag() || !stack.getTag().getBoolean("dont_name"))) {
            VampirismMod.proxy.displayNameSwordScreen(stack);
            player.level.playLocalSound((player).getX(), (player).getY(), (player).getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1f, false);
        }
    }

    /**
     * Updated during {@link net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent}
     *
     * @return True if the cached value was updated
     */
    public boolean updateTrainedCached(@NotNull ItemStack stack, @NotNull LivingEntity player) {
        float cached = getTrained(stack);
        float trained = getTrained(stack, player);
        if (cached != trained) {
            stack.addTagElement("trained-cache", FloatTag.valueOf(trained));
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        return VampirePlayer.getOpt(playerIn).map(vampire -> {
            if (vampire.getLevel() == 0) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

            if (this.canBeCharged(stack) && playerIn.isShiftKeyDown() && vampire.getSkillHandler().isSkillEnabled(VampireSkills.BLOOD_CHARGE.get()) && (playerIn.isCreative() || vampire.getBloodLevel() >= (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.BLOOD_CHARGE_SPEED.get()) ? VampirismConfig.BALANCE.vrBloodChargeSpeedMod.get() : 2))) {
                playerIn.startUsingItem(handIn);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }

            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }).orElse(new InteractionResultHolder<>(InteractionResult.PASS, stack));
    }

    @Override
    protected final float getAttackDamage(@NotNull ItemStack stack) {
        return super.getAttackDamage(stack) * getAttackDamageModifier(stack);
    }

    protected float getAttackDamageModifier(@NotNull ItemStack stack) {
        return getCharged(stack) > 0 ? 1f : minStrength;
    }

    @Override
    protected final float getAttackSpeed(@NotNull ItemStack stack) {
        return untrainedAttackSpeed + (trainedAttackSpeed - untrainedAttackSpeed) * getTrained(stack);
    }

    /**
     * @return The amount of charge consumed per hit
     */
    protected abstract float getChargeUsage();

    /**
     * Gets the charged value from the tag compound
     *
     * @return Value between 0 and 1
     */
    protected float getCharged(@NotNull ItemStack stack) {
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
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@NotNull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains("trained-cache")) {
                return nbt.getFloat("trained-cache");
            }
        }
        return 0.0f;
    }

    /**
     * Gets the trained value from the tag compound
     *
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@NotNull ItemStack stack, @Nullable LivingEntity player) {
        if (player == null) return getTrained(stack);
        UUID id = player.getUUID();
        CompoundTag nbt = stack.getTagElement("trained");
        if (nbt != null) {
            if (nbt.contains(id.toString())) {
                return nbt.getFloat(id.toString());
            }
        }
        return 0f;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargedParticle(@NotNull LivingEntity player, boolean mainHand) {
        Vec3 mainPos = UtilLib.getItemPosition(player, mainHand);
        for (int j = 0; j < 3; ++j) {
            Vec3 pos = mainPos.add((player.getRandom().nextFloat() - 0.5f) * 0.1f, (player.getRandom().nextFloat() - 0.3f) * 0.9f, (player.getRandom().nextFloat() - 0.5f) * 0.1f);
            ModParticles.spawnParticleClient(player.getCommandSenderWorld(), new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (player.getRandom().nextFloat() * 0.9F + 0.1F)), true, pos.x + (player.getRandom().nextFloat() - 0.5D) * 0.1D, pos.y + (player.getRandom().nextFloat() - 0.5D) * 0.1D, pos.z + (player.getRandom().nextFloat() - 0.5D) * 0.1D, new ResourceLocation("minecraft", "glitter_1")), pos.x, pos.y, pos.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargingParticle(@NotNull LivingEntity player, boolean mainHand) {
        Vec3 pos = UtilLib.getItemPosition(player, mainHand);
        if (player.getAttackAnim(1f) > 0f) return;
        pos = pos.add((player.getRandom().nextFloat() - 0.5f) * 0.1f, (player.getRandom().nextFloat() - 0.3f) * 0.9f, (player.getRandom().nextFloat() - 0.5f) * 0.1f);
        Vec3 playerPos = new Vec3((player).getX(), (player).getY() + player.getEyeHeight() - 0.2f, (player).getZ());
        ModParticles.spawnParticleClient(player.getCommandSenderWorld(), new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (player.getRandom().nextFloat() * 0.6F + 0.1F)), true, pos.x, pos.y, pos.z), playerPos.x, playerPos.y, playerPos.z);
    }
}
