package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class HissingAction extends DefaultVampireAction {

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaHissingCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaHissingEnabled.get();
    }

    @Override
    protected IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);
        player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10, 10, 10)).forEach(e -> {
            if (e.getTarget() == player) {
                e.targetSelector.getAvailableGoals().stream().filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
                e.setTarget(null);
            }
        });
        return IActionResult.SUCCESS;
    }

}
