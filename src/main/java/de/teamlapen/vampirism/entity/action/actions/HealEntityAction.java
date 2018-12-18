package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class HealVampireEntityAction extends DefaultEntityAction implements IInstantAction<IVampireMob> {

    @Override
    public int getCooldown(int level) {
        return Balance.ea.VAMPIRE_HEAL_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public boolean activate(IVampireMob entity) {
        entity.getRepresentingEntity().addPotionEffect(new PotionEffect(MobEffects.REGENERATION, Balance.ea.VAMPIRE_HEAL_AMOUNT, 0));
        return true;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

}
