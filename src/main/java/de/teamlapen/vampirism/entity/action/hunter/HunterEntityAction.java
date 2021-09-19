package de.teamlapen.vampirism.entity.action.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.entity.PathfinderMob;

import javax.annotation.Nonnull;

public abstract class HunterEntityAction<T extends PathfinderMob & IEntityActionUser> extends DefaultEntityAction implements IEntityAction {

    public HunterEntityAction(@Nonnull EntityActionTier tier, EntityClassType[] param) {
        super(tier, param);
    }

    @Override
    public IFaction<?> getFaction() {
        return VReference.HUNTER_FACTION;
    }

}
