package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SingleCrossbowItem extends VampirismCrossbowItem {

    public SingleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier, Supplier<ISkill<IHunterPlayer>> requiredSkill) {
        super(properties, arrowVelocity, chargeTime, itemTier, requiredSkill);
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

    @Override
    public float[] getShotPitches(RandomSource pRandom) {
        return new float[] { 1 };
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return doubleCrossbow ? 1f : 0.4f;
    }
}