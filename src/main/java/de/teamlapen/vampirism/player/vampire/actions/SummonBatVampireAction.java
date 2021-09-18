package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * Summon bat skill
 */
public class SummonBatVampireAction extends DefaultVampireAction {

    public SummonBatVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer player) {
        Player entityPlayer = player.getRepresentingPlayer();
        boolean refined = player.getSkillHandler().isRefinementEquipped(ModRefinements.summon_bats);
        int amount = VampirismConfig.BALANCE.vaSummonBatsCount.get();
        if (amount > 1 && refined) {
            amount = amount / 2;
        }
        for (int i = 0; i < amount; i++) {
            BlindingBatEntity e = ModEntities.blinding_bat.create(entityPlayer.getCommandSenderWorld());
            e.restrictLiveSpan();
            if (refined) e.setTargeting();
            e.setResting(false);
            e.copyPosition(player.getRepresentingPlayer());
            player.getRepresentingPlayer().getCommandSenderWorld().addFreshEntity(e);
        }
        entityPlayer.getCommandSenderWorld().playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), ModSounds.bat_swarm, SoundSource.PLAYERS, 1.3F, entityPlayer.getCommandSenderWorld().random.nextFloat() * 0.2F + 1.3F);
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer player) {
        return player.getActionHandler().isActionActive(VampireActions.bat) || player.getSkillHandler().isRefinementEquipped(ModRefinements.summon_bats);
    }


    @Override
    public int getCooldown(IFactionPlayer<?> player) {
        return (int) ((player.getSkillHandler().isRefinementEquipped(ModRefinements.summon_bats) ? 0.7 : 1) * VampirismConfig.BALANCE.vaSummonBatsCooldown.get() * 20);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaSummonBatsEnabled.get();
    }
}
