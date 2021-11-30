package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.util.random.WeightedEntry;

public class EntityActionEntry extends WeightedEntry.IntrusiveBase {
    private final IEntityAction action;

    public EntityActionEntry(int itemWeightIn, IEntityAction actionIn) {
        super(itemWeightIn);
        this.action = actionIn;
    }

    public IEntityAction getAction() {
        return action;
    }
}
