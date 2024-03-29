package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class SingleCrossbowItem extends VampirismCrossbowItem {

    public SingleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return null;
    }
}