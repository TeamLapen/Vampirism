package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.SoundCategory;

public class BatsSpawnEntityAction<T extends EntityCreature & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public BatsSpawnEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.BATSPAWN_COOLDOWN * 20;
    }

    @Override
    public boolean activate(T entity) {
        for (int i = 0; i < Balance.ea.BATSPAWN_AMOUNT; i++) {
            EntityBlindingBat e = new EntityBlindingBat(entity.getEntityWorld());
            e.restrictLiveSpan();
            e.setIsBatHanging(false);
            e.copyLocationAndAnglesFrom(entity);
            entity.getEntityWorld().spawnEntity(e);
        }
        entity.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.bat_swarm, SoundCategory.PLAYERS, 1.3F, entity.getEntityWorld().rand.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }
}