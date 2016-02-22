package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampire;
import de.teamlapen.vampirism.api.entity.factions.Faction;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.World;

/**
 * Base class for Vampirism's vampire entities
 */
public class EntityVampireBase extends EntityVampirism implements IVampire {
    private boolean sundamageCache;

    public EntityVampireBase(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    @Override
    public Faction getFaction() {
        return VampirismAPI.VAMPIRE_FACTION;
    }

    @Override
    public boolean isGettingSundamage(boolean forceRefresh) {
        if (this.ticksExisted % 8 != 0 && !forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this));
    }

    @Override
    public boolean isGettingSundamge() {
        return isGettingSundamage(false);
    }
}
