package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IHunter;
import de.teamlapen.vampirism.api.entity.factions.Faction;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.world.World;

/**
 * Base class for all vampire hunter
 */
public class EntityHunterBase extends EntityVampirism implements IHunter {
    public EntityHunterBase(World world) {
        super(world);
    }

    @Override
    public Faction getFaction() {
        return VampirismAPI.HUNTER_FACTION;
    }
}
