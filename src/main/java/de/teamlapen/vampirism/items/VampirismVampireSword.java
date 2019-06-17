package de.teamlapen.vampirism.items;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

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
            tooltip.add(new TextComponentTranslation("text.vampirism.can_only_be_used_by", new TextComponentTranslation(VReference.VAMPIRE_FACTION.getTranslationKeyPlural())));
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
        stack.setTagInfo("dont_name", new NBTTagByte(Byte.MAX_VALUE));
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
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer && target.getHealth() <= target.getMaxHealth() * Balance.vps.SWORD_FINISHER_MAX_HEALTH_PERC && !Helper.isVampire(target)) {
            if (VampirePlayer.get((EntityPlayer) attacker).getSkillHandler().isSkillEnabled(VampireSkills.sword_finisher)) {
                DamageSource dmg = DamageSource.causePlayerDamage((EntityPlayer) attacker);
                target.attackEntityFrom(dmg, 10000F);
                Vec3d center = new Vec3d(target.getPosition());
                center.add(0, target.height / 2d, 0);
                VampLib.proxy.getParticleHandler().spawnParticles(target.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 15, 0.5, target.getRNG(), 132, 12, 0xE02020);
            }
        }
        return super.hitEntity(stack, target, attacker);
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getCharged(stack) == 1f;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, stack);


        if (this.canBeCharged(stack) && playerIn.isSneaking() && (playerIn.isCreative() || vampire.getBloodLevel() >= 2) && vampire.getSkillHandler().isSkillEnabled(VampireSkills.blood_charge)) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) return stack;
        IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability((EntityPlayer) entityLiving);
        if (((EntityPlayer) entityLiving).isCreative() || vampire.useBlood(2, false)) {
            this.charge(stack, 2 * VReference.FOOD_TO_FLUID_BLOOD);
        }
        if (getCharged(stack) == 1) {
            tryName(stack, (EntityPlayer) entityLiving);
        }
        return stack;
    }


    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //Try to minimize execution time, but tricky since off hand selection is not directly available, but it can only be off hand if itemSlot 0
        if (worldIn.isRemote && (isSelected || itemSlot == 0)) {
            float charged = getCharged(stack);
            if (charged > 0 && entityIn.ticksExisted % ((int) (8 + 80 * (1f - charged))) == 0 && entityIn instanceof EntityLivingBase) {
                boolean secondHand = !isSelected && ((EntityLivingBase) entityIn).getHeldItem(EnumHand.OFF_HAND).equals(stack);
                if (isSelected || secondHand) {
                    spawnChargedParticle((EntityLivingBase) entityIn, isSelected);
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
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
        stack.setTagInfo("charged", new NBTTagFloat(MathHelper.clamp(value, 0f, 1f)));
    }

    /**
     * Sets the stored trained value for the given player
     *
     * @param value Clamped between 0 and 1
     */
    public void setTrained(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player, float value) {
        NBTTagCompound nbt = stack.getOrCreateChildTag("trained");
        nbt.putFloat(player.getUniqueID().toString(), MathHelper.clamp(value, 0f, 1f));
    }

    /**
     * If the stack is not named and the player hasn't been named before, ask the player to name this stack
     */
    public void tryName(ItemStack stack, EntityPlayer player) {
        if (!stack.hasDisplayName() && player instanceof EntityPlayerMP && (!stack.hasTag() || !stack.getTag().getBoolean("dont_name"))) {
            NetworkHooks.openGui(player, ); //TODO new interaction object for name sword gui
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
    public boolean updateTrainedCached(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player) {
        float cached = getTrained(stack);
        float trained = getTrained(stack, player);
        if (cached != trained) {
            stack.setTagInfo("trained-cache", new NBTTagFloat(trained));
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
    protected float getTrained(@Nonnull ItemStack stack, @Nullable EntityLivingBase player) {
        if (player == null) return getTrained(stack);
        UUID id = player.getUniqueID();
        NBTTagCompound nbt = stack.getChildTag("trained");
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
            NBTTagCompound nbt = stack.getTag();
            if (nbt.contains("trained-cache")) {
                return nbt.getFloat("trained-cache");
            }
        }
        return 0.0f;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargedParticle(EntityLivingBase player, boolean mainHand) {
        Vec3d mainPos = UtilLib.getItemPosition(player, mainHand);
        for (int j = 0; j < 3; ++j) {
            Vec3d pos = mainPos.add((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
            VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, pos.x, pos.y, pos.z, pos.x + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.y + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.z + (player.getRNG().nextFloat() - 0.5D) * 0.2D, (int) (4.0F / (player.getRNG().nextFloat() * 0.9F + 0.1F)), 177);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargingParticle(EntityLivingBase player, boolean mainHand) {
        Vec3d pos = UtilLib.getItemPosition(player, mainHand);
        if (player.getSwingProgress(1f) > 0f) return;
        pos = pos.add((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
        Vec3d playerPos = new Vec3d((player).posX, (player).posY + player.getEyeHeight() - 0.2f, (player).posZ);
        VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, playerPos.x, playerPos.y, playerPos.z, pos.x, pos.y, pos.z, (int) (4.0F / (player.getRNG().nextFloat() * 0.6F + 0.1F)));

    }
}
