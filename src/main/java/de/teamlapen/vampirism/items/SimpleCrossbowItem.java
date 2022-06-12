package de.teamlapen.vampirism.items;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

public class SimpleCrossbowItem extends VampirismItemCrossbow {

    private final float speed;
    private final int coolDownTicks;

    /**
     * @param speed         Speed of the shot arrows (0.1F-20F)
     * @param coolDownTicks Cooldown ticks >0
     * @param maxDamage     Max usages or 0 if unbreakable
     */
    public SimpleCrossbowItem(float speed, int coolDownTicks, int maxDamage, ItemTier enchantability) {
        super(maxDamage);
        this.speed = speed;
        this.coolDownTicks = coolDownTicks;
        if (coolDownTicks < 0) {
            throw new IllegalArgumentException("Cooldown ticks have to be >= 0");
        }
        if (speed > 20F || speed < 0.1F) {
            throw new IllegalArgumentException("Invalid speed");
        }
        this.setEnchantability(enchantability);
    }


    @Override
    protected float getArrowVelocity() {
        return speed;
    }

    @Override
    protected int getCooldown(PlayerEntity player, ItemStack stack) {
        return coolDownTicks;
    }
}
