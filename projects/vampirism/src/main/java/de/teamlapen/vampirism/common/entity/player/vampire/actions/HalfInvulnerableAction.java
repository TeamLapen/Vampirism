package de.teamlapen.vampirism.common.entity.player.vampire.actions;

import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class HalfInvulnerableAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public HalfInvulnerableAction() {
        super();
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return 20 * (ModConfig.BALANCE.vaHalfInvulnerableCooldown.get());
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return 20 * (ModConfig.BALANCE.vaHalfInvulnerableDuration.get());
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.vaHalfInvulnerableEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().half_invulnerable = false;
        removePotionEffect(player, MobEffects.SLOWNESS);
    }

    @Override
    public void onReActivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().half_invulnerable = true;

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffect(vampire);
        }
        return false;
    }

    @Override
    protected IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        ((VampirePlayer) vampire).getSkillProperties().half_invulnerable = true;
        applyEffect(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SLOWNESS, 22, 1, false, false));
    }

}
