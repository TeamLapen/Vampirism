package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DarkStalker extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaDarkStalkerEnabled.get();
    }

    @Override
    protected boolean activate(IVampirePlayer player, ActivationContext context) {
        ((VampirePlayer) player).getSpecialAttributes().darkStalker = true;
        applyEffect(player);
        return true;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaDarkStalkerDuration.get();
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
        if (duration % VampirismConfig.HELPER.getTicksPerBlood() == 0) {
            player.useBlood(1, true);
        }
        if (!player.isRemote() && player.asEntity().tickCount % 20 == 0) {
            applyEffect(player);
        }
        return false;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaDarkStalkerCooldown.get();
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 22, 1, false, false));
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
