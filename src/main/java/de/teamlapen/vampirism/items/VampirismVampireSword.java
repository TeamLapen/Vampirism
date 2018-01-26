package de.teamlapen.vampirism.items;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public VampirismVampireSword(String regName, ToolMaterial material, float attackSpeedModifier) {
        super(regName, material, attackSpeedModifier);
    }

    public VampirismVampireSword(String regName, ToolMaterial material) {
        super(regName, material);
    }

    public VampirismVampireSword(String regName, ToolMaterial material, float attackSpeedModifier, float attackDamage) {
        super(regName, material, attackSpeedModifier, attackDamage);
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


    @Override
    protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        float charged = getCharged(stack);
        float trained = getTrained(stack, playerIn);
        tooltip.add(UtilLib.translate("text.vampirism.sword_charged") + " " + ((int) (charged * 100f)) + "%");
        tooltip.add(UtilLib.translate("text.vampirism.sword_trained") + " " + ((int) (trained * 100f)) + "%");
        tooltip.add(UtilLib.translate("text.vampirism.sword_trained") + " " + ((int) (getTrained(stack) * 100f)) + "%");

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

    /**
     * Gets the trained value from the tag compound
     *
     * @param stack
     * @param player
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@Nonnull ItemStack stack, @Nullable EntityLivingBase player) {
        if (player == null) return getTrained(stack);
        UUID id = player.getPersistentID();
        NBTTagCompound nbt = stack.getSubCompound("trained");
        if (nbt != null) {
            if (nbt.hasKey(id.toString())) {
                return nbt.getFloat(id.toString());
            }
        }
        return 0f;
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
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
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

    @SideOnly(Side.CLIENT)
    private void spawnChargedParticle(EntityLivingBase player, boolean mainHand) {
        Vec3d mainPos = UtilLib.getItemPosition(player, mainHand);
        for (int j = 0; j < 3; ++j) {
            Vec3d pos = mainPos.addVector((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
            VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, pos.x, pos.y, pos.z, pos.x + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.y + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.z + (player.getRNG().nextFloat() - 0.5D) * 0.2D, (int) (4.0F / (player.getRNG().nextFloat() * 0.9F + 0.1F)), 177);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnChargingParticle(EntityLivingBase player, boolean mainHand) {
        Vec3d pos = UtilLib.getItemPosition(player, mainHand);
        if (player.getSwingProgress(1f) > 0f) return;
        pos = pos.addVector((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
        Vec3d playerPos = new Vec3d((player).posX, (player).posY + player.getEyeHeight() - 0.2f, (player).posZ);
        VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, playerPos.x, playerPos.y, playerPos.z, pos.x, pos.y, pos.z, (int) (4.0F / (player.getRNG().nextFloat() * 0.6F + 0.1F)));

    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player.getEntityWorld().isRemote) {
            if (count % 3 == 0) {
                spawnChargingParticle(player, player.getHeldItemMainhand().equals(stack));
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) return stack;
        IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability((EntityPlayer) entityLiving);
        if (((EntityPlayer) entityLiving).isCreative() || vampire.getBloodStats().consumeBlood(2)) {
            this.charge(stack, 2 * VReference.FOOD_TO_FLUID_BLOOD);
        }
        if (getCharged(stack) == 1) {
            tryName(stack, (EntityPlayer) entityLiving);
        }
        return stack;
    }


    /**
     * If the stack is not named and the player hasn't been named before, ask the player to name this stack
     */
    public void tryName(ItemStack stack, EntityPlayer player) {
        if (!stack.hasDisplayName() && (!stack.hasTagCompound() || !stack.getTagCompound().getBoolean("dont_name"))) {
            (player).openGui(VampirismMod.instance, ModGuiHandler.ID_NAME_SWORD, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            (player).world.playSound((player).posX, (player).posY, (player).posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f, false);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, stack);


        if (this.canBeCharged(stack) && (playerIn.isCreative() || vampire.getBloodLevel() >= 2) && vampire.getSkillHandler().isSkillEnabled(VampireSkills.blood_charge)) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    /**
     * Sets the stored trained value for the given player
     *
     * @param value Clamped between 0 and 1
     */
    public void setTrained(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player, float value) {
        NBTTagCompound nbt = stack.getOrCreateSubCompound("trained");
        nbt.setFloat(player.getPersistentID().toString(), MathHelper.clamp(value, 0f, 1f));
    }

    /**
     * Gets a cached trained value from the tag compound
     *
     * @param stack
     * @return Value between 0 and 1. Defaults to 0
     */
    protected float getTrained(@Nonnull ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("trained-cache")) {
                return nbt.getFloat("trained-cache");
            }
        }
        return 0.0f;
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getCharged(stack) == 1f;
    }
    /**
     * Gets the charged value from the tag compound
     *
     * @param stack
     * @return Value between 0 and 1
     */
    protected float getCharged(@Nonnull ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().getFloat("charged");
        }
        return 0.0f;
    }

    /**
     * Might want to use {@link #charge(ItemStack, int)} instead to charge it with mB of blood
     * @param value Is clamped between 0 and 1
     */
    public void setCharged(@Nonnull ItemStack stack, float value) {
        stack.setTagInfo("charged", new NBTTagFloat(MathHelper.clamp(value,0f,1f)));
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
     * @return Charging factor multiplied with amount to get charge percentage
     */
    protected abstract float getChargingFactor(ItemStack stack);
}
