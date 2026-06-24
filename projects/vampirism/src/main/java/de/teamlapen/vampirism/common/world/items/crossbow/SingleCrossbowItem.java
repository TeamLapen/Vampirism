package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.tags.ModEnchantmentTags;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;

public class SingleCrossbowItem extends HunterCrossbowItem {

    public SingleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>> requiredSkill) {
        super(properties.component(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(requiredSkill).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public int getMaxLoadedProjectiles() {
        return 1;
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 2f : 0.4f;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(ModEnchantmentTags.SINGLE_HUNTER_CROSSBOW_COMPATIBLE);
    }
}