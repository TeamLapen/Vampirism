package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

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
    protected IActionResult activateServer(IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);

        int duration = ModConfig.balance().vaHissingDuration.get() * 20;
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(10), mob -> mob.getTarget() == player || !Helper.isVampire(mob)).forEach(mob -> mob.addEffect(new MobEffectInstance(ModEffects.DISORIENTATION, duration), player));

        return IActionResult.SUCCESS;
    }
}
