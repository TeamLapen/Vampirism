package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.SoundCategory;

public class BatsSpawnEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public BatsSpawnEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(T entity) {
        int amount = VampirismConfig.BALANCE.eaBatspawnAmount.get();
        for (int i = 0; i < amount; i++) {
            Helper.createEntity(ModEntities.blinding_bat, entity.getEntityWorld()).ifPresent(e -> {
                e.restrictLiveSpan();
                e.setIsBatHanging(false);
                e.copyLocationAndAnglesFrom(entity);
                entity.getEntityWorld().addEntity(e);
            });
        }
        entity.getEntityWorld().playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModSounds.bat_swarm, SoundCategory.PLAYERS, 1.3F, entity.getEntityWorld().rand.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaBatspawnCooldown.get() * 20;
    }
}