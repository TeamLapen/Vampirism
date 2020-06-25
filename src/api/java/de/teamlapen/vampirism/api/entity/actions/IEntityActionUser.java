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
        entity.getAttribute(Attributes.field_233818_a_/*MAX_HEALTH*/).func_233769_c_(entity.getEntityClass().getHealthModifier()); /*Apply modifier and add to uuid map?*/
        entity.getAttribute(Attributes.field_233823_f_/*ATTACK_DAMAGE*/).func_233769_c_(entity.getEntityClass().getDamageModifier());
        entity.getAttribute(Attributes.field_233821_d_/*MOVEMENT_SPEED*/).func_233769_c_(entity.getEntityClass().getSpeedModifier());
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

    IActionHandlerEntity getActionHandler();
}
