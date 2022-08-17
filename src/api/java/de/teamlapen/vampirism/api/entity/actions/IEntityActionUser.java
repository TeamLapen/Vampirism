package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IEntityActionUser extends IAdjustableLevel, IFactionEntity {

    @SuppressWarnings("ConstantConditions")
    static <T extends LivingEntity & IEntityActionUser> void applyAttributes(@NotNull T entity) {
        entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(entity.getEntityClass().getHealthModifier());
        entity.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(entity.getEntityClass().getDamageModifier());
        entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(entity.getEntityClass().getSpeedModifier());
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
