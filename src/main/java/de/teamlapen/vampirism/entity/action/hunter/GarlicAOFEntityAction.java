package de.teamlapen.vampirism.entity.action.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class GarlicAOFEntityAction<T extends CreatureEntity & IEntityActionUser> extends HunterEntityAction<T> implements ILastingAction<T> {

    public GarlicAOFEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaGarlicCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaGarlicDuration.get() * 20;
    }

    @Override
    public void onUpdate(T entity, int duration) {
        List<PlayerEntity> players = entity.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(entity.posX - 4, entity.posY - 1, entity.posZ - 4, entity.posX + 4, entity.posY + 3, entity.posZ + 4));
        for (PlayerEntity e : players) {
            if (VampirismAPI.factionRegistry().getFaction(e) == VReference.VAMPIRE_FACTION) {
                if (e.getActivePotionEffect(ModEffects.garlic) == null || e.getActivePotionEffect(ModEffects.garlic).getDuration() <= 60) {
                    e.addPotionEffect(new EffectInstance(ModEffects.garlic, 99));
                }
            }
        }
    }

}
