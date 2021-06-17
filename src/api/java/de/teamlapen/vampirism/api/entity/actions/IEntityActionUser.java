package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;

import java.util.List;

public interface IEntityActionUser extends IAdjustableLevel, IFactionEntity {

    static <T extends LivingEntity & IEntityActionUser> void applyAttributes(T entity) {
        entity.getAttribute(Attributes.MAX_HEALTH).applyPersistentModifier(entity.getEntityClass().getHealthModifier());
        entity.getAttribute(Attributes.ATTACK_DAMAGE).applyPersistentModifier(entity.getEntityClass().getDamageModifier());
        entity.getAttribute(Attributes.MOVEMENT_SPEED).applyPersistentModifier(entity.getEntityClass().getSpeedModifier());
    }

    IActionHandlerEntity getActionHandler();

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
