package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class InvisibilityVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public InvisibilityVampireAction() {
        super();
    }

    @Override
    public IActionResult activateServer(@NotNull IVampirePlayer vampire, ActivationContext context) {
        activate(vampire);
        return IActionResult.SUCCESS;
    }

    protected void activate(@NotNull IVampirePlayer player) {
        player.asEntity().setInvisible(true);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaInvisibilityCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.balance().vaInvisibilityDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaInvisibilityEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IVampirePlayer vampire) {
        ((VampirePlayer) vampire).getSkillProperties().invisible = true;
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        vampire.asEntity().setInvisible(false);
        ((VampirePlayer) vampire).getSkillProperties().invisible = false;
    }

    @Override
    public void onReActivatedServer(@NotNull IVampirePlayer vampire) {
        activate(vampire);
    }

    @Override
    public boolean onUpdate(@NotNull IVampirePlayer vampire) {
        if (!vampire.asEntity().isInvisible()) {
            vampire.asEntity().setInvisible(true);
        }
        return false;
    }

}
