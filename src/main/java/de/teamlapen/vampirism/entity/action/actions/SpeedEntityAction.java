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
        return Balance.ea.SPEED_DURATION;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.SPEED_COOLDOWN;
    }

    @Override
    public void deactivate(T entity) {
        System.out.println("deactivate"); // TODO remove
        entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(new AttributeModifier(uuid, "speedaction", 2, 1));
    }

    @Override
    public boolean onUpdate(T entity) {
        if (!entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(uuid, "speedaction", 2, 1))) {
            entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(uuid, "speedaction", 2, 1));
        }
        return true;
    }

}
