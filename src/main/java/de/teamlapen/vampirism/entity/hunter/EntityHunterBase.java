package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

/**
 * Base class for all vampire hunter
 */
public abstract class EntityHunterBase extends EntityVampirism implements IHunter {
    private final boolean countAsMonster;

    public EntityHunterBase(World world, boolean countAsMonster) {
        super(world);
        this.countAsMonster = countAsMonster;

    }

    @Override
    public IFaction getFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        if (forSpawnCount && countAsMonster && type == EnumCreatureType.MONSTER) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
    }
}
