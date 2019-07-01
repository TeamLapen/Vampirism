package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.entity.EntityCreature;

import javax.annotation.Nonnull;

public abstract class VampireEntityAction<T extends EntityCreature & IEntityActionUser> extends DefaultEntityAction implements IEntityAction {

    public VampireEntityAction(@Nonnull EntityActionTier tier, EntityClassType[] param) {
        super(tier, param);
    }

    @Override
    public IPlayableFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

}
