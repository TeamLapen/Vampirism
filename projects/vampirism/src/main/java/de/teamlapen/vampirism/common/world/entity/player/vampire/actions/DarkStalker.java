package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DarkStalker extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaDarkStalkerEnabled.get();
    }

    @Override
    protected IActionResult activate(IVampirePlayer player, ActivationContext context) {
        ((VampirePlayer) player).getSkillProperties().darkStalker = true;
        applyEffect(player);
        return IActionResult.SUCCESS;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.balance().vaDarkStalkerDuration.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().darkStalker = true;
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().darkStalker = false;

    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().darkStalker = true;
    }

    @Override
    public boolean onUpdate(IVampirePlayer player, int duration, int expectedDuration) {
        if (duration % ModConfig.helper().getDarkStalkerTicksPerBlood() == 0) {
            player.useBlood(1, true);
        }
        if (!player.isRemote() && player.asEntity().tickCount % 20 == 0) {
            applyEffect(player);
        }
        return false;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaDarkStalkerCooldown.get();
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SLOWNESS, 22, 1, false, false));
    }

}
