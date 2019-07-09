package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;

/**
 * Base class for all vampire hunter
 */
@SuppressWarnings("EntityConstructor")
public abstract class HunterBaseEntity extends VampirismEntity implements IHunter {
    private final boolean countAsMonster;

    public HunterBaseEntity(EntityType type, World world, boolean countAsMonster) {
        super(type, world);
        this.countAsMonster = countAsMonster;
    }

    @Override
    public float getEyeHeight() {
        return height * 0.875f;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public boolean isCreatureType(EntityClassification type, boolean forSpawnCount) {
        if (forSpawnCount && countAsMonster && type == EntityClassification.MONSTER) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new SwimGoal(this));
    }
}
