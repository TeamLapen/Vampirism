package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;

import java.util.List;

public interface IEntityActionUser extends IAdjustableLevel, IFactionEntity {

    static <T extends LivingEntity & IEntityActionUser> void applyAttributes(T entity) {
        entity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(entity.getEntityClass().getHealthModifier());
        entity.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(entity.getEntityClass().getDamageModifier());
        entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(entity.getEntityClass().getSpeedModifier());
    }

    /**
     * gets all available actions for this entity.
     */
    default List<IEntityAction> getAvailableActions() {
        return VampirismAPI.entityActionManager().getAllEntityActionsByTierAndClassType(this.getFaction(), getEntityTier(), getEntityClass());
    }

    default EntityClassType getEntityClass() {
        return EntityClassType.None;
    }

    default EntityActionTier getEntityTier() {
        return EntityActionTier.Default;
    }
}
