package de.teamlapen.vampirism.entity.vampire.action;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public abstract class VampireEntityAction<T extends PathfinderMob & IEntityActionUser> extends DefaultEntityAction implements IEntityAction {

    public VampireEntityAction(@NotNull EntityActionTier tier, EntityClassType[] param) {
        super(tier, param);
    }

    @Override
    public IFaction<?> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

}
