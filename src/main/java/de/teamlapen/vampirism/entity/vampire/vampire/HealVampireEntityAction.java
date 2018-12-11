package de.teamlapen.vampirism.entity.vampire.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;

public class HealVampireEntityAction extends DefaultEntityAction implements IInstantAction<IVampireMob> {

    @Override
    public int getCooldown(int level) {
        // TODO Auto-generated method stub
        return 100; // FIXME cooldown
    }

    @Override
    public boolean onActivated(IVampireMob entity) {
        float healamount = entity.getRepresentingEntity().getMaxHealth() / 10;
        entity.getRepresentingEntity().heal(healamount);
        return true;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

}
