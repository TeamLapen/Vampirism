package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffects(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaRageCooldown.get() * 20;
    }

    @Override
    public int getDuration(@NotNull IVampirePlayer player) {
        return 20 * (ModConfig.balance().vaRageMinDuration.get() + ModConfig.balance().vaRageDurationIncrease.get() * player.getLevel());
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaRageEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        removePotionEffect(vampire, MobEffects.SPEED);
        removePotionEffect(vampire, MobEffects.STRENGTH);
        removePotionEffect(vampire, MobEffects.HASTE);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffects(vampire);
        }

        return false;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

    protected void applyEffects(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SPEED, 22, 2, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.STRENGTH, 22, 0, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.HASTE, 22, 0, false, false));
    }
}
