package de.teamlapen.vampirism.entity.vampire.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;

public class InvisibleVampireEntityAction extends DefaultEntityAction implements ILastingAction<IVampireMob> {
    public InvisibleVampireEntityAction() {
    }
    
    public boolean activate(IVampireMob entity) {
        entity.getRepresentingEntity().setInvisible(true);
        return true;
    }
    
    @Override
    public int getCooldown(int level) {
        return 100; //FIXME Balance.....InvilibleCoolndown *20
    }

    @Override
    public int getDuration(int level) {
        return 100; // FIXME Balance.....InvilibleCoolndown *20
    }

    @Override
    public void onActivatedClient(IVampireMob entity) {
    }

    @Override
    public void onDeactivated(IVampireMob entity) {
        entity.getRepresentingEntity().setInvisible(false);
        
    }

    @Override
    public void onReActivated(IVampireMob entity) {
        activate(entity);
        
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
