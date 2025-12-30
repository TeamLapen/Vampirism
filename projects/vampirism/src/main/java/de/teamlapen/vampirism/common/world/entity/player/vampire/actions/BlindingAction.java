package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BlindingAction extends DefaultVampireAction {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected @NonNull IActionResult activate(IVampirePlayer player, @NonNull ActivationContext context) {
        //noinspection unchecked
        List<LivingEntity> entities =  (List<LivingEntity>) (Object) player.asEntity().level().getEntities(player.asEntity(), player.asEntity().getBoundingBox().inflate(10), EntitySelector.NO_SPECTATORS.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).and(x -> !Helper.isVampire(x)));
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 3));
        }
        return IActionResult.SUCCESS;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return 20 * 60;
    }
}
