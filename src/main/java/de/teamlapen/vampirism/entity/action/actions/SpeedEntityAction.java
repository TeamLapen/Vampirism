package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.math.MathHelper;
import java.util.UUID;
import io.netty.util.internal.ThreadLocalRandom;

public class SpeedEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    private UUID uuid = MathHelper.getRandomUUID(ThreadLocalRandom.current());

    @Override
    public int getDuration(int level) {
        return Balance.ea.SPEED_DURATION * 20;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.SPEED_COOLDOWN * 20;
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(uuid);
    }

    @Override
    public boolean onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(uuid, "speedaction", Balance.ea.SPEED_AMOUNT, 2))) {
            entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(uuid, "speedaction", Balance.ea.SPEED_AMOUNT, 2));
        }
        return true;
    }

}
