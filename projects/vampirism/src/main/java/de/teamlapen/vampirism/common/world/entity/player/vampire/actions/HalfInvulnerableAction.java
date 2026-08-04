package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
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
        return 20 * (ModConfig.balance().vaHalfInvulnerableCooldown.get());
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return 20 * (ModConfig.balance().vaHalfInvulnerableDuration.get());
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaHalfInvulnerableEnabled.get();
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
    public void onReActivatedServer(@NotNull IVampirePlayer player) {
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
    protected IActionResult activateServer(@NotNull IVampirePlayer vampire, ActivationContext context) {
        ((VampirePlayer) vampire).getSkillProperties().half_invulnerable = true;
        applyEffect(vampire);
        return IActionResult.SUCCESS;
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.SLOWNESS, 22, 1, false, false));
    }

}
