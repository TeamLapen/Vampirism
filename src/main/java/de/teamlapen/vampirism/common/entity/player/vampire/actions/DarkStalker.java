package de.teamlapen.vampirism.common.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DarkStalker extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.vaDarkStalkerEnabled.get();
    }

    @Override
    protected IActionResult activate(IVampirePlayer player, ActivationContext context) {
        ((VampirePlayer) player).getSpecialAttributes().darkStalker = true;
        applyEffect(player);
        return IActionResult.SUCCESS;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.BALANCE.vaDarkStalkerDuration.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().darkStalker = true;
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().darkStalker = false;

    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().darkStalker = true;
    }

    @Override
    public boolean onUpdate(IVampirePlayer player, int duration, int expectedDuration) {
        if (duration % ModConfig.HELPER.getTicksPerBlood() == 0) {
            player.useBlood(1, true);
        }
        if (!player.isRemote() && player.asEntity().tickCount % 20 == 0) {
            applyEffect(player);
        }
        return false;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.BALANCE.vaDarkStalkerCooldown.get();
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SLOWNESS, 22, 1, false, false));
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
