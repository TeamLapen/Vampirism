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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VampirismSwordItem extends SwordItem {
    private String translation_key;


    public VampirismSwordItem(@NotNull Tier material, int attackDamageIn, float attackSpeedIn, @NotNull Properties builder) {
        super(material, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if ((enchantment == Enchantments.MENDING && (this instanceof IItemWithTier) && ((IItemWithTier) this).getVampirismTier() == IItemWithTier.TIER.ULTIMATE)) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.translation_key == null) {
            this.translation_key = super.getOrCreateDescriptionId().replaceAll("_normal|_enhanced|_ultimate", "");
        }

        return this.translation_key;
    }
}
