package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

/**
 * Handwritten subclass of the generated {@link ConvertedHorse} base, adding increased xp reward,
 * tamed-aware behaviour, and boosted health on randomization.
 */
public class ConvertedHorseEntity extends ConvertedHorse {

    public ConvertedHorseEntity(EntityType<? extends ConvertedHorse> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }

    @Override
    protected boolean handleEating(@NonNull Player player, @NonNull ItemStack itemStack) {
        if (!super.handleEating(player, itemStack)) {
            if (itemStack.is(ModItems.GOLDEN_HEART) || itemStack.is(Items.ENCHANTED_BOOK)) {
                this.heal(10);
                if (isBaby() && !this.isAgeLocked() && !this.level().isClientSide()) {
                    this.ageUp(240);
                }
                if (getTemper() < getMaxTemper() && !this.level().isClientSide()) {
                    this.modifyTemper(10);
                }
                if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                    setInLove(player);
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
