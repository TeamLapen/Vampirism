package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;

/**
 * Summon bat skill
 */
public class SummonBatVampireAction extends DefaultVampireAction {

    public SummonBatVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer player) {
        PlayerEntity entityPlayer = player.getRepresentingPlayer();
        for (int i = 0; i < VampirismConfig.BALANCE.vaSummonBatCount.get(); i++) {
            BlindingBatEntity e = ModEntities.blinding_bat.create(entityPlayer.getEntityWorld());
            e.restrictLiveSpan();
            e.setIsBatHanging(false);
            e.copyLocationAndAnglesFrom(player.getRepresentingPlayer());
            player.getRepresentingPlayer().getEntityWorld().addEntity(e);
        }
        entityPlayer.getEntityWorld().playSound(null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, ModSounds.bat_swarm, SoundCategory.PLAYERS, 1.3F, entityPlayer.getEntityWorld().rand.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer player) {
        return player.getActionHandler().isActionActive(VampireActions.bat);
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaSummonBatCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaSunscreenEnabled.get();
    }
}
