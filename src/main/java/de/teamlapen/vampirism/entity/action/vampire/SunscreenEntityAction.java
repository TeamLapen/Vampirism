package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import net.minecraft.entity.EntityCreature;
import net.minecraft.potion.PotionEffect;

public class SunscreenEntityAction<T extends EntityCreature & IEntityActionUser> extends VampireEntityAction implements ILastingAction<T> {

    public SunscreenEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
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

    @Override
    public void updatePreAction(T entity, int duration) {
    }

}
