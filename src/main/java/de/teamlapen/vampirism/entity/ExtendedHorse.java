package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;

/**
 * Class so skeleton horses cannot be bitten.
 */
public class ExtendedHorse extends ExtendedCreature {

    private final EntityHorse horse;

    public ExtendedHorse(EntityHorse entity) {
        super(entity);
        horse = entity;
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        if (isUndead()) return false;
        return super.canBeBitten(biter);
    }

    @Override
    public boolean canBecomeVampire() {
        if (isUndead()) return false;
        return super.canBecomeVampire();
    }

    @Override
    public int getBlood() {
        if (isUndead()) return -1;
        return super.getBlood();
    }

    @Override
    public int getMaxBlood() {
        if (isUndead()) return -1;
        return super.getMaxBlood();
    }

    private boolean isUndead() {
        return horse.getType().equals(HorseType.SKELETON) || horse.getType().equals(HorseType.ZOMBIE);
    }
}
