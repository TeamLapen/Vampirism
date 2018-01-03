package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.List;

public abstract class VampirismVampireSword extends VampirismItemWeapon {

    private final float minStrength = 0.2f;
    private final float minSpeed = 0.5f;

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
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        float strength = getAttackDamageModifier(stack);
        float speed = getAttackSpeedModifier(stack);
        if ((strength > minStrength || speed > minSpeed) && !Helper.isVampire(player)) {
            setAttackDamageModifier(stack, minStrength);
            setAttackSpeedModifier(stack, minSpeed);
            player.setHeldItem(EnumHand.MAIN_HAND, stack);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        float charged = getAttackDamageModifier(stack);
        float trained = getAttackSpeedModifier(stack);
        tooltip.add(UtilLib.translate("text.vampirism.sword_charged") + " " + ((int) (charged * 100f)) + "%");
        tooltip.add(UtilLib.translate("text.vampirism.sword_trained") + " " + ((int) (trained * 100f)) + "%");
    }

    public float getAttackDamageModifier(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("charged")) {
                return (float) MathHelper.clamp(nbt.getDouble("charged"), minStrength, 1.0);
            }
        }
        return minStrength;
    }

    public void setAttackDamageModifier(ItemStack stack, float strength) {
        stack.setTagInfo("charged", new NBTTagDouble(strength));
    }

    public float getAttackSpeedModifier(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("trained")) {
                return (float) MathHelper.clamp(nbt.getDouble("trained"), minSpeed, 1.0);
            }
        }
        return minSpeed;
    }

    public void setAttackSpeedModifier(ItemStack stack, float strength) {
        stack.setTagInfo("trained", new NBTTagDouble(strength));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target.isDead && Helper.isVampire(attacker)) {
            setAttackSpeedModifier(stack, MathHelper.clamp(getAttackSpeedModifier(stack) + 0.05f, minSpeed, 1.0f));
            attacker.setHeldItem(EnumHand.MAIN_HAND, stack);
        }
        return super.hitEntity(stack, target, attacker);
    }
}
