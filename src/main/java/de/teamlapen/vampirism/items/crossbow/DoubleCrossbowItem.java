package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DoubleCrossbowItem extends VampirismCrossbowItem {

    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier, Supplier<ISkill<IHunterPlayer>> requiredSkill) {
        super(properties, arrowVelocity, chargeTime, itemTier, requiredSkill);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }


    @Override
    protected boolean loadCrossbow(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity, int p_77615_4_) {
        var b1 = tryLoadProjectiles(entity, itemStack);
        var b2 = tryLoadProjectiles(entity, itemStack);
        if (b1 || b2) {
            CrossbowItem.setCharged(itemStack, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = getChargedProjectiles(stack);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size() && i < 2; ++i) { // only shoot a maximum of 2 arrows
            ItemStack itemstack = list.get(i);
            boolean flag = !(shooter instanceof Player player) || player.getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectile(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle); // only one arrow per projectile
            }
        }

        onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }

    @Override
    public float[] getShotPitches(RandomSource pRandom) {
        return new float[] { 1, 1 };
    }
}
