package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;
import java.util.Random;

public class RegenerationAOFEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    public RegenerationAOFEntityAction() {
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.REGENERATION_DURATION * 20;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.REGENERATION_COOLDOWN * 20;
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public void onUpdate(T entity, int duration) {
        List<EntityLiving> players = entity.getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.posX - 4, entity.posY - 1, entity.posZ - 4, entity.posX + 4, entity.posY + 3, entity.posZ + 4));
        for (EntityLiving e : players) {
            if (VampirismAPI.factionRegistry().getFaction(entity) == VampirismAPI.factionRegistry().getFaction(e)) {
                e.heal(entity.getMaxHealth() / 100 * Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20));
                if (duration % 20 == 0) {
                    VampLib.proxy.getParticleHandler().spawnParticles(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld(), new ResourceLocation("vampirism", "heal"), e.posX, e.posY + 1, e.posZ, 2, 0.01D, new Random(), e);
                }
            }
        }

    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }
}
