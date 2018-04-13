package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for weapons
 */
public class VampirismItemWeapon extends VampirismItem {

    /**
     * Value between 0.0 and 1f.
     * Calculation: Base += Base * -(1-Value) => Base * Value
     */
    private final float attackDamage;
    private final Item.ToolMaterial material;
    private final float attackSpeed;

    public VampirismItemWeapon(String regName, Item.ToolMaterial material) {
        this(regName, material, 0.4F);
    }


    public VampirismItemWeapon(String regName, Item.ToolMaterial material, float attackSpeedModifier) {
        this(regName, material, attackSpeedModifier, 3F + material.getAttackDamage());
    }

    public VampirismItemWeapon(String regName, Item.ToolMaterial material, float attackSpeedModifier, float attackDamage) {
        super(regName);
        this.material = material;
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeedModifier;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -(1.0f - this.getAttackSpeed(stack)), 1));
        }

        return multimap;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.material.getRepairItemStack();
        if (!ItemStackUtil.isEmpty(mat) && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false))
            return true;
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if ((double) state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving);
        }

        return true;
    }

    @Override
    protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (advanced) {
            tooltip.add("ModDamage: " + getAttackDamage(stack));
            tooltip.add("ModSpeed: " + getAttackSpeed(stack));
        }
    }

    protected float getAttackDamage(ItemStack stack) {
        return attackDamage;
    }

    protected float getAttackSpeed(ItemStack stack) {
        return attackSpeed;
    }
}
