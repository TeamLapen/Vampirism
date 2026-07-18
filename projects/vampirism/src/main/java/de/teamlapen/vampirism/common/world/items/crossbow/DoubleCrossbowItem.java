package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.common.world.items.component.EnchantmentOverride;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;

public class DoubleCrossbowItem extends HunterCrossbowItem {

    @SafeVarargs
    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>>... requiredSkills) {
        super(properties.component(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(requiredSkills).build()).component(ModDataComponents.ENCHANTMENT_OVERRIDE, new EnchantmentOverride(ModEnchantmentTags.DOUBLE_HUNTER_CROSSBOW_COMPATIBLE)), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public int getMaxLoadedProjectiles() {
        return 2;
    }

    @Override
    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        boolean first = super.tryLoadProjectiles(shooter, crossbow);
        boolean second = super.tryLoadProjectiles(shooter, crossbow);
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
