package de.teamlapen.vampirism.entity.action.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GarlicAOFEntityAction<T extends PathfinderMob & IEntityActionUser> extends HunterEntityAction<T> implements ILastingAction<T> {

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
        List<Player> players = entity.getCommandSenderWorld().getEntitiesOfClass(Player.class, new AABB(entity.getX() - 4, entity.getY() - 1, entity.getZ() - 4, entity.getX() + 4, entity.getY() + 3, entity.getZ() + 4));
        for (Player e : players) {
            if (VampirismAPI.factionRegistry().getFaction(e) == VReference.VAMPIRE_FACTION) {
                if (e.getEffect(ModEffects.garlic.get()) == null || e.getEffect(ModEffects.garlic.get()).getDuration() <= 60) {
                    e.addEffect(new MobEffectInstance(ModEffects.garlic.get(), 99));
                }
            }
        }
    }

}
