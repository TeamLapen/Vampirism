package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class SingleCrossbowItem extends VampirismCrossbowItem {

    public SingleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ItemTier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }
}