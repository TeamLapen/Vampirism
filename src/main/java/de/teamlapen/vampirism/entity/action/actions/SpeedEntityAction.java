package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import java.util.UUID;

public class SpeedEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {
    public static final UUID UUIDS = UUID.fromString("2b49cf70-b634-4e85-8c3e-0147919eaf54");

    public SpeedEntityAction() {
        super();
    }

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
        entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(UUIDS);
    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(UUIDS, "speedaction", Balance.ea.SPEED_AMOUNT, 2))) {
            entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(UUIDS, "speedaction", Balance.ea.SPEED_AMOUNT, 2));
        }
        if (duration % 5 == 0) {
            VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), ModParticles.CLOUD, entity.posX, entity.posY, entity.posZ, 5, 0.5, entity.getRNG(), -entity.motionX, 0.0D, -entity.motionZ);
        }
    }

    @Override
    public void activate(T entity) {
    }
}
