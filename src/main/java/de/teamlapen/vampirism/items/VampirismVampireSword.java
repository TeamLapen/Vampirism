package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.client.gui.NameSwordScreen;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class VampirismVampireSword extends VampirismItemWeapon implements IBloodChargeable {


    public static final String DO_NOT_NAME_STRING = "DO_NOT_NAME";
    /**
     * Minimal strength modifier
     */
    private final float minStrength = 0.2f;
    /**
     * Minimal speed modifier
     */
    private final float minSpeed = 0.15f;

    public VampirismVampireSword(String regName, ItemTier material, float attackSpeedModifier, Properties prop) {
        super(regName, material, attackSpeedModifier, prop);
    }

    public VampirismVampireSword(String regName, ItemTier material, Properties prop) {
        super(regName, material, prop);
    }

    public VampirismVampireSword(String regName, ItemTier material, int attackDamage, float attackSpeedModifier, Properties prop) {
        super(regName, material, attackDamage, attackSpeedModifier, prop);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        float charged = getCharged(stack);
        float trained = getTrained(stack, Minecraft.getInstance().player);
        tooltip.add(UtilLib.translated("text.vampirism.sword_charged").appendText(" " + ((int) (charged * 100f)) + "%"));
        tooltip.add(UtilLib.translated("text.vampirism.sword_trained").appendText(" " + ((int) (trained * 100f)) + "%"));
        if (Minecraft.getInstance().player != null && !VReference.VAMPIRE_FACTION.equals(FactionPlayerHandler.get(Minecraft.getInstance().player).getCurrentFaction())) {
            tooltip.add(new TranslationTextComponent("text.vampirism.can_only_be_used_by", VReference.VAMPIRE_FACTION.getNamePlural()));
        }
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
        stack.setTagInfo("dont_name", new ByteNBT(Byte.MAX_VALUE));
    }

    public float getAttackDamageModifier(ItemStack stack) {
        return getCharged(stack) > 0 ? 1f : minStrength;
    }

    public float getAttackSpeedModifier(ItemStack stack) {
        return minSpeed + (1f - minSpeed) * getTrained(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity && target.getHealth() <= target.getMaxHealth() * Balance.vps.SWORD_FINISHER_MAX_HEALTH_PERC && !Helper.isVampire(target)) {
            if (VampirePlayer.get((PlayerEntity) attacker).getSkillHandler().isSkillEnabled(VampireSkills.sword_finisher)) {
                DamageSource dmg = DamageSource.causePlayerDamage((PlayerEntity) attacker);
                target.attackEntityFrom(dmg, 10000F);
                Vec3d center = new Vec3d(target.getPosition());
                center.add(0, target.getHeight() / 2d, 0);
                ModParticles.spawnParticles(target.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "effect_4"), 12, 0xE02020), center.x, center.y, center.z, 15, 0.5, target.getRNG());
            }
        }
        return super.hitEntity(stack, target, attacker);
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getCharged(stack) == 1f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //Try to minimize execution time, but tricky since off hand selection is not directly available, but it can only be off hand if itemSlot 0
        if (worldIn.isRemote && (isSelected || itemSlot == 0)) {
            float charged = getCharged(stack);
            if (charged > 0 && entityIn.ticksExisted % ((int) (8 + 80 * (1f - charged))) == 0 && entityIn instanceof LivingEntity) {
                boolean secondHand = !isSelected && ((LivingEntity) entityIn).getHeldItem(Hand.OFF_HAND).equals(stack);
                if (isSelected || secondHand) {
                    spawnChargedParticle((LivingEntity) entityIn, isSelected);
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(ActionResultType.PASS, stack);


        if (this.canBeCharged(stack) && playerIn.isSneaking() && (playerIn.isCreative() || vampire.getBloodLevel() >= 2) && vampire.getSkillHandler().isSkillEnabled(VampireSkills.blood_charge)) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!(entityLiving instanceof PlayerEntity)) return stack;
        IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability((PlayerEntity) entityLiving);
        if (((PlayerEntity) entityLiving).isCreative() || vampire.useBlood(2, false)) {
            this.charge(stack, 2 * VReference.FOOD_TO_FLUID_BLOOD);
        }
        if (getCharged(stack) == 1) {
            tryName(stack, (PlayerEntity) entityLiving);
        }
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.getEntityWorld().isRemote) {
            if (count % 3 == 0) {
                spawnChargingParticle(player, player.getHeldItemMainhand().equals(stack));
            }
        }
    }

    /**
     * Might want to use {@link #charge(ItemStack, int)} instead to charge it with mB of blood
     *
     * @param value Is clamped between 0 and 1
     */
    public void setCharged(@Nonnull ItemStack stack, float value) {
        stack.setTagInfo("charged", new FloatNBT(MathHelper.clamp(value, 0f, 1f)));
    }

    /**
     * Sets the stored trained value for the given player
     *
     * @param value Clamped between 0 and 1
     */
    public void setTrained(@Nonnull ItemStack stack, @Nonnull LivingEntity player, float value) {
        CompoundNBT nbt = stack.getOrCreateChildTag("trained");
        nbt.putFloat(player.getUniqueID().toString(), MathHelper.clamp(value, 0f, 1f));
    }

    /**
     * If the stack is not named and the player hasn't been named before, ask the player to name this stack
     */
    public void tryName(ItemStack stack, PlayerEntity player) {
        if (!stack.hasDisplayName() && player instanceof ServerPlayerEntity && (!stack.hasTag() || !stack.getTag().getBoolean("dont_name"))) {
            Minecraft.getInstance().displayGuiScreen(new NameSwordScreen(stack));
            player.world.playSound((player).posX, (player).posY, (player).posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f, false);
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
            stack.setTagInfo("trained-cache", new FloatNBT(trained));
            return true;
        }
        return false;
    }

    @Override
    protected final float getAttackDamage(ItemStack stack) {
        return getBaseAttackDamage(stack) * getAttackDamageModifier(stack);
    }

    @Override
    protected final float getAttackSpeed(ItemStack stack) {
        return getBaseAttackSpeed(stack) * getAttackSpeedModifier(stack);
    }

    protected float getBaseAttackDamage(ItemStack stack) {
        return super.getAttackDamage(stack);
    }

    protected float getBaseAttackSpeed(ItemStack stack) {
        return super.getAttackSpeed(stack);
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
     * Gets the trained value from the tag compound
     *
     * @param stack
     * @param player
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@Nonnull ItemStack stack, @Nullable LivingEntity player) {
        if (player == null) return getTrained(stack);
        UUID id = player.getUniqueID();
        CompoundNBT nbt = stack.getChildTag("trained");
        if (nbt != null) {
            if (nbt.contains(id.toString())) {
                return nbt.getFloat(id.toString());
            }
        }
        return 0f;
    }

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

    @OnlyIn(Dist.CLIENT)
    private void spawnChargedParticle(LivingEntity player, boolean mainHand) {
        Vec3d mainPos = UtilLib.getItemPosition(player, mainHand);
        for (int j = 0; j < 3; ++j) {
            Vec3d pos = mainPos.add((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
            ModParticles.spawnParticle(player.getEntityWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (4.0F / (player.getRNG().nextFloat() * 0.9F + 0.1F)), new ResourceLocation("minecraft", "glitter_1")), pos.x, pos.y, pos.y, pos.x + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.y + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.z + (player.getRNG().nextFloat() - 0.5D) * 0.2D);//TODO particle textureindex: 177
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargingParticle(LivingEntity player, boolean mainHand) {
        Vec3d pos = UtilLib.getItemPosition(player, mainHand);
        if (player.getSwingProgress(1f) > 0f) return;
        pos = pos.add((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
        Vec3d playerPos = new Vec3d((player).posX, (player).posY + player.getEyeHeight() - 0.2f, (player).posZ);
        ModParticles.spawnParticle(player.getEntityWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (4.0F / (player.getRNG().nextFloat() * 0.6F + 0.1F))), playerPos.x, playerPos.y, playerPos.y, pos.x, pos.y, pos.z);
    }
}
