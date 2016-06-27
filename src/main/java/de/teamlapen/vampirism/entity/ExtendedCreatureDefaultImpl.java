package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;

import javax.annotation.Nullable;

/**
 * Default implementation required by the capability system. DON'T USE
 */
@Deprecated
class ExtendedCreatureDefaultImpl implements IExtendedCreatureVampirism {

    public ExtendedCreatureDefaultImpl() {
        VampirismMod.log.e("ExtendedCreatureCapability", "Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");

    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        return false;
    }

    @Override
    public boolean canBecomeVampire() {
        return false;
    }

    @Override
    public int getBlood() {
        return 0;
    }

    @Override
    public void setBlood(int blood) {

    }

    @Override
    public float getBloodSaturation() {
        return 0;
    }

    @Override
    public EntityCreature getEntity() {
        return null;
    }

    @Override
    public int getMaxBlood() {
        return 0;
    }

    @Override
    public
    @Nullable
    IConvertedCreature makeVampire() {
        return null;
    }

    @Override
    public int onBite(IVampire biter) {
        return 0;
    }
}
