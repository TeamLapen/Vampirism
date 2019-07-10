package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
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
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }
}
