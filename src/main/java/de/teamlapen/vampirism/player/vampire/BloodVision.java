package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Blood vision
 */
public class BloodVision implements IVampireVision {
    @Override
    public String getTranslationKey() {
        return "text.vampirism.skill.blood_vision";
    }

    @Override
    public void onActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().blood_vision = true;
        Player entity = player.getRepresentingPlayer();
        if (entity.level.isClientSide() && OptifineHandler.isShaders()) {
            if (!VampirismConfig.COMMON.optifineBloodvisionWarning.get()) {
                VampirismConfig.COMMON.optifineBloodvisionWarning.set(true);
                VampirismConfig.COMMON.optifineBloodvisionWarning.save();
                entity.displayClientMessage(Component.translatable("text.vampirism.warning_optifine_bloodvision"), false);
            }
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().blood_vision = false;
    }

    @Override
    public void tick(IVampirePlayer player) {

    }
}
