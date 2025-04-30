package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;

public class DoubleCrossbowItem extends HunterCrossbowItem {

    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>> requiredSkill) {
        super(properties.component(ModDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(ModFactionTags.IS_HUNTER).skill(requiredSkill).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    protected boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack) {
        var first = super.tryLoadProjectiles(pShooter, pCrossbowStack);
        var second = super.tryLoadProjectiles(pShooter, pCrossbowStack);
        return first || second;
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 3f : 1.5f;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(ModEnchantmentTags.DOUBLE_HUNTER_CROSSBOW_COMPATIBLE);
    }
}
