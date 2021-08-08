package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.util.WeighedRandom;

public class EntityActionEntry extends WeighedRandom.WeighedRandomItem {
    private final IEntityAction action;

    public EntityActionEntry(int itemWeightIn, IEntityAction actionIn) {
        super(itemWeightIn);
        this.action = actionIn;
    }

    public IEntityAction getAction() {
        return action;
    }
}
