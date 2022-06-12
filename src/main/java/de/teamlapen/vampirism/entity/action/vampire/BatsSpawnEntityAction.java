package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;

public class BatsSpawnEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public BatsSpawnEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(T entity) {
        int amount = VampirismConfig.BALANCE.eaBatspawnAmount.get();
        for (int i = 0; i < amount; i++) {
            Helper.createEntity(ModEntities.BLINDING_BAT.get(), entity.getCommandSenderWorld()).ifPresent(e -> {
                e.restrictLiveSpan();
                e.setResting(false);
                e.copyPosition(entity);
                entity.getCommandSenderWorld().addFreshEntity(e);
            });
        }
        entity.getCommandSenderWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.BAT_SWARM.get(), SoundSource.PLAYERS, 1.3F, entity.getCommandSenderWorld().random.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaBatspawnCooldown.get() * 20;
    }
}