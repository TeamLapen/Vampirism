package de.teamlapen.vampirism.items;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class SimpleCrossbowItem extends VampirismItemCrossbow {

    private final float speed;
    private final int coolDownTicks;

    /**
     * @param speed         Speed of the shot arrows (0.1F-20F)
     * @param coolDownTicks Cooldown ticks >0
     * @param maxDamage     Max usages or 0 if unbreakable
     */
    public SimpleCrossbowItem(float speed, int coolDownTicks, int maxDamage, Tiers material) {
        super(maxDamage, material);
        this.speed = speed;
        this.coolDownTicks = coolDownTicks;
        if (coolDownTicks < 0) {
            throw new IllegalArgumentException("Cooldown ticks have to be >= 0");
        }
        if (speed > 20F || speed < 0.1F) {
            throw new IllegalArgumentException("Invalid speed");
        }
    }


    @Override
    protected float getArrowVelocity() {
        return speed;
    }

    @Override
    protected int getCooldown(Player player, ItemStack stack) {
        return coolDownTicks;
    }
}
