package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

/**
 * Base class for all vampire hunter
 */
@SuppressWarnings("EntityConstructor")
public abstract class EntityHunterBase extends EntityVampirism implements IHunter {
    private final boolean countAsMonster;

    public EntityHunterBase(World world, boolean countAsMonster) {
        super(world);
        this.countAsMonster = countAsMonster;
    }

    @Override
    public float getEyeHeight() {
        return height * 0.875f;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
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
