package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class VampirismVampireSword extends VampirismItemWeapon {

    private final float minStrength = 0.2f;
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

    public float getAttackDamageModifier(ItemStack stack) {
        return minStrength + (1f - minStrength) * getCharged(stack);
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
     * @param value Is clamped between 0 and 1
     */
    public void setCharged(@Nonnull ItemStack stack, float value) {
        stack.setTagInfo("charged", new NBTTagFloat(MathHelper.clamp(value,0f,1f)));
    }

}
