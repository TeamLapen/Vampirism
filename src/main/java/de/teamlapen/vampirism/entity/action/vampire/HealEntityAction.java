package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.util.ResourceLocation;
import java.util.Random;

public class HealEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends VampireEntityAction implements IInstantAction<T> {

    public HealEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.HEAL_COOLDOWN * 20;
    }

    @Override
    public boolean activate(T entity) {
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / 100 * Balance.ea.HEAL_AMOUNT);
        VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), new ResourceLocation("vampirism", "heal"), entity.posX, entity.posY + 1, entity.posZ, 10, 0.3D, new Random(), entity);
        return true;
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }
}
