package de.teamlapen.vampirism.entity.action.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

public abstract class HunterEntityAction extends DefaultEntityAction implements IEntityAction {

    public HunterEntityAction(EntityActionTier tier, EntityClassType[] param) {
        super(tier, param);
    }

    @Override
    public IPlayableFaction getFaction() {
        return VReference.HUNTER_FACTION;
    }

}
