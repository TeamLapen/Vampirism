package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;

public class InvisibleVampireEntityAction extends DefaultEntityAction implements ILastingAction<IVampireMob> {
    public InvisibleVampireEntityAction() {
    }
    
    public boolean activate(IVampireMob entity) {
        entity.getRepresentingEntity().setInvisible(true);
        return true;
    }
    
    @Override
    public int getCooldown(int level) {
        return Balance.ea.VAMPIRE_INVISIBLE_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.VAMPIRE_INVISIBLE_DURATION;
    }

    @Override
    public void deactivate(IVampireMob entity) {
        entity.getRepresentingEntity().setInvisible(false);
        
    }

    @Override
    public boolean onUpdate(IVampireMob entity) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
        return false;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

}
