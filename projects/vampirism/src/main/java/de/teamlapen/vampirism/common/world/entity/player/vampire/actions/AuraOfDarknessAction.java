package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AuraOfDarknessAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaAuraOfDarknessEnabled.getAsBoolean();
    }

    @Override
    protected IActionResult activateServer(IVampirePlayer player, ActivationContext context) {
        return IActionResult.SUCCESS;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaAuraOfDarknessCooldown.getAsInt();
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.balance().vaAuraOfDarknessDuration.getAsInt();
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        if (player.asEntity().tickCount % 40 == 0) {
            List<Entity> entities = player.asEntity().level().getEntities((Entity) null, new AABB(player.asEntity().blockPosition()).inflate(10, 4, 10), Helper::isVampire);
            entities.forEach(x -> {
                if (x instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(ModEffects.AURA_OF_DARKNESS, 5*20, 0, true, true));
                }
            });
        }
        return false;
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {

    }

    @Override
    public void onDeactivated(IVampirePlayer player) {

    }

    @Override
    public void onReActivatedServer(IVampirePlayer player) {

    }
}
