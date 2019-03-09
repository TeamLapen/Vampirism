package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.util.WeightedRandom;

public class EntityActionEntry extends WeightedRandom.Item {
    IEntityAction action;

    public EntityActionEntry(int itemWeightIn, IEntityAction actionIn) {
        super(itemWeightIn);
        this.action = actionIn;
    }

    public IEntityAction getAction() {
        return action;
    }
}
