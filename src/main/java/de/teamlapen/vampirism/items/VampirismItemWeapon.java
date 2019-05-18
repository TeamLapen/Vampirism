package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    private final IItemTier material;
    private final float attackSpeed;

    public VampirismItemWeapon(String regName, IItemTier material, Properties props) {
        this(regName, material, 0.4F, props);
    }


    public VampirismItemWeapon(String regName, IItemTier material, float attackSpeedModifier, Properties props) {
        this(regName, material, attackSpeedModifier, 3F + material.getAttackDamage(), props);
    }

    public VampirismItemWeapon(String regName, IItemTier material, float attackSpeedModifier, float attackDamage, Properties props) {
        super(regName, props.defaultMaxDamage(material.getMaxUses()));
        this.material = material;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeedModifier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) {
            tooltip.add(new TextComponentString("ModDamage: " + getAttackDamage(stack)));
            tooltip.add(new TextComponentString("ModSpeed: " + getAttackSpeed(stack)));
        }
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
        return material.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
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
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if ((double) state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving);
        }

        return true;
    }

    protected float getAttackDamage(ItemStack stack) {
        return attackDamage;
    }

    protected float getAttackSpeed(ItemStack stack) {
        return attackSpeed;
    }
}
