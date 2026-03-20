package de.teamlapen.vampirism.common.world.entity.player.vampire.vision;

import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.client.OptifineHandler;
import de.teamlapen.vampirism.common.config.CommonConfig;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Blood vision
 */
public class BloodVision implements IVampireVision {
    @Override
    public @NotNull String getTranslationKey() {
        return "text.vampirism.skill.blood_vision";
    }

    @Override
    public void onActivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().blood_vision = true;
        Player entity = player.asEntity();
        if (entity.level().isClientSide() && OptifineHandler.isShaders()) {
            CommonConfig config = ModConfig.common();
            if (!config.optifineBloodVisionWarning.get()) {
                config.optifineBloodVisionWarning.set(true);
                config.optifineBloodVisionWarning.save();
                entity.displayClientMessage(Component.translatable("notification.vampirism.warning_optifine_blood_vision"), false);
            }
        }
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSkillProperties().blood_vision = false;
    }

    @Override
    public void tick(IVampirePlayer player) {

    }

    @Override
    public boolean isEnabled() {
        return !ModConfig.balance().vpBloodVisionDisabled.get();
    }
}
