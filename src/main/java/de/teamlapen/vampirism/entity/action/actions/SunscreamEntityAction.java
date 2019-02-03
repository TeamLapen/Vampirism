package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.potion.PotionEffect;

public class SunscreamEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    public SunscreamEntityAction() {
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.SUNSCREEN_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.SUNSCREEN_DURATION * 20;
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().removePotionEffect(ModPotions.sunscreen);

    }

    @Override
    public void onUpdate(T entity, int duration) {
    }

    @Override
    public void activate(T entity) {
        entity.getRepresentingEntity().addPotionEffect(new PotionEffect(ModPotions.sunscreen, getDuration(entity.getLevel()), 3, false, false));

    }


}
