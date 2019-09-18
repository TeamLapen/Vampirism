package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.SoundCategory;

public class BatsSpawnEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public BatsSpawnEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(T entity) {
        for (int i = 0; i < Balance.ea.BATSPAWN_AMOUNT; i++) {
            BlindingBatEntity e = ModEntities.blinding_bat.create(entity.getEntityWorld());
            e.restrictLiveSpan();
            e.setIsBatHanging(false);
            e.copyLocationAndAnglesFrom(entity);
            entity.getEntityWorld().addEntity(e);
        }
        entity.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.bat_swarm, SoundCategory.PLAYERS, 1.3F, entity.getEntityWorld().rand.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.BATSPAWN_COOLDOWN * 20;
    }
}