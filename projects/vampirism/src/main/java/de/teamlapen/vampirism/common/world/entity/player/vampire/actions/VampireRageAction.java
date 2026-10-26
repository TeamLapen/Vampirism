package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class VampireRageAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public VampireRageAction() {
        super();
    }

    @Override
    public IActionResult activateServer(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffects(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT_FORM);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaVampireRageCooldown.get() * 20;
    }

    @Override
    public int getDuration(@NotNull IVampirePlayer player) {
        return 20 * (ModConfig.balance().vaVampireRageMinDuration.get() + ModConfig.balance().vaVampireRageDurationIncrease.get() * player.getLevel());
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaVampireRageEnabled.get();
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
    public void onReActivatedServer(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffects(vampire);
        }

        return false;
    }

    protected void applyEffects(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SPEED, 22, 2, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.STRENGTH, 22, 0, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.HASTE, 22, 0, false, false));
    }
}
