package de.teamlapen.vampirism.items;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemSimpleCrossbow extends VampirismItemCrossbow {

    private final float speed;
    private final int coolDownTicks;

    /**
     * @param regName       Registry name
     * @param speed         Speed of the shot arrows (0.1F-20F)
     * @param coolDownTicks Cooldown ticks >0
     * @param maxDamage     Max usages or 0 if unbreakable
     */
    public ItemSimpleCrossbow(String regName, float speed, int coolDownTicks, int maxDamage) {
        super(regName, maxDamage);
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
    protected int getCooldown(PlayerEntity player, ItemStack stack) {
        return coolDownTicks;
    }
}
