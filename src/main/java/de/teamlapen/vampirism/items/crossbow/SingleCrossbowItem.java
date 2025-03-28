package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModTasks;
import de.teamlapen.vampirism.core.tags.ModEnchantmentTags;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SingleCrossbowItem extends HunterCrossbowItem {

    public SingleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier, Holder<ISkill<?>> requiredSkill) {
        super(properties.component(ModDataComponents.FACTION_RESTRICTION, FactionRestriction.builder(ModFactionTags.IS_HUNTER).skill(requiredSkill).build()), arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 2f : 0.4f;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(ModEnchantmentTags.SINGLE_HUNTER_CROSSBOW_COMPATIBLE);
    }
}