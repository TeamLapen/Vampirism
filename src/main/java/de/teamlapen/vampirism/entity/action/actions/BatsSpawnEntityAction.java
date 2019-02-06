package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.util.SoundCategory;

public class BatsSpawnEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements IInstantAction<T> {

    public BatsSpawnEntityAction() {
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