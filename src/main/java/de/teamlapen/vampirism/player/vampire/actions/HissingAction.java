package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

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
    protected boolean activate(IVampirePlayer vampire, ActivationContext context) {
        Player entity = vampire.getRepresentingPlayer();
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS,1,1);
        vampire.getRepresentingPlayer().getCommandSenderWorld().getEntitiesOfClass(Mob.class, new AABB(vampire.getRepresentingPlayer().blockPosition()).inflate(10, 10, 10)).forEach(e -> {
            if (e.getTarget() == vampire.getRepresentingPlayer()) {
                e.targetSelector.getRunningGoals().filter(g -> g.getGoal() instanceof TargetGoal).forEach(WrappedGoal::stop);
                e.setTarget(null);
            }
        });
        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
