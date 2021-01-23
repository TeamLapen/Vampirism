package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

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
        PlayerEntity entity = player.getRepresentingPlayer();
        if (entity.world.isRemote() && OptifineHandler.isShaders()) {
            if (!VampirismConfig.COMMON.optifineBloodvisionWarning.get()) {
                VampirismConfig.COMMON.optifineBloodvisionWarning.set(true);
                VampirismConfig.COMMON.optifineBloodvisionWarning.save();
                entity.sendStatusMessage(new TranslationTextComponent("text.vampirism.warning_optifine_bloodvision"), false);
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
