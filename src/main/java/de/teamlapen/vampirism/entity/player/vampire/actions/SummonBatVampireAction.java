package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Summon bat skill
 */
public class SummonBatVampireAction extends DefaultVampireAction {

    public SummonBatVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer player, ActivationContext context) {
        Player entityPlayer = player.asEntity();
        boolean refined = player.getRefinementHandler().isRefinementEquipped(ModRefinements.SUMMON_BATS);
        int amount = VampirismConfig.BALANCE.vaSummonBatsCount.get();
        if (amount > 1 && refined) {
            amount = amount / 2;
        }
        for (int i = 0; i < amount; i++) {
            BlindingBatEntity e = ModEntities.BLINDING_BAT.get().create(entityPlayer.getCommandSenderWorld());
            e.restrictLiveSpan();
            if (refined) e.setTargeting();
            e.setResting(false);
            e.copyPosition(player.asEntity());
            player.asEntity().getCommandSenderWorld().addFreshEntity(e);
        }
        entityPlayer.getCommandSenderWorld().playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), ModSounds.BAT_SWARM.get(), SoundSource.PLAYERS, 1.3F, entityPlayer.getCommandSenderWorld().random.nextFloat() * 0.2F + 1.3F);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer player) {
        var res = player.getActionHandler().isActionActive(VampireActions.BAT) || player.getRefinementHandler().isRefinementEquipped(ModRefinements.SUMMON_BATS);
        if (res) {
            return IActionResult.SUCCESS;
        } else {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat_swarm.no_bat_mode"));
        }
    }


    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        return (int) ((player.getRefinementHandler().isRefinementEquipped(ModRefinements.SUMMON_BATS) ? 0.7 : 1) * VampirismConfig.BALANCE.vaSummonBatsCooldown.get() * 20);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaSummonBatsEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
