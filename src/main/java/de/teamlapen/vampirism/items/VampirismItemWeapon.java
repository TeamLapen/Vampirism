package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampirismItemWeapon extends SwordItem {
    private final float attackDamage;
    private final float attackSpeed;
    private String translation_key;


    public VampirismItemWeapon(Tier material, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(material, attackDamageIn, attackSpeedIn, builder);
        this.attackDamage = attackDamageIn;
        this.attackSpeed = attackSpeedIn;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) {
            tooltip.add(Component.literal("ModDamage: " + getAttackDamage(stack)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("ModSpeed: " + getAttackSpeed(stack)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if((enchantment == Enchantments.MENDING && (this instanceof IItemWithTier) && ((IItemWithTier) this).getVampirismTier() == IItemWithTier.TIER.ULTIMATE)){
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    protected float getAttackDamage(ItemStack stack) {
        return attackDamage;
    }

    /**
     * This is effectively an attack slowness. The more negative the value, the lower the attack speed
     */
    protected float getAttackSpeed(ItemStack stack) {
        return attackSpeed;
    }

    @Nonnull
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.translation_key == null) {
            this.translation_key = super.getOrCreateDescriptionId().replaceAll("_normal|_enhanced|_ultimate", "");
        }

        return this.translation_key;
    }
}
