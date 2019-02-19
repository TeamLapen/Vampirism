package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.potion.PotionEffect;

public class IgnoreSunDamageEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends VampireEntityAction implements ILastingAction<T> {

    public IgnoreSunDamageEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_COOLDOWN;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_DURATION;
    }

    @Override
    public void deactivate(T entity) {
        if (entity.getActivePotionEffect(ModPotions.sunscreen) != null && entity.getActivePotionEffect(ModPotions.sunscreen).getAmplifier() == 0) {
            entity.removePotionEffect(ModPotions.sunscreen);
        }
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }

    @Override
    public void activate(T entity) {
        entity.addPotionEffect(new PotionEffect(ModPotions.sunscreen, getDuration(entity.getLevel()), 0));

    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

}
