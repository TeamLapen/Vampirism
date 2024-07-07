package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class HissingAction extends DefaultVampireAction {

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaHissingCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaHissingEnabled.get();
    }

    @Override
    protected IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);
        player.getCommandSenderWorld().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10, 10, 10)).forEach(e -> {
            if (e.getTarget() == player) {
                e.targetSelector.getAvailableGoals().stream().filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
                e.setTarget(null);
            }
        });
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
