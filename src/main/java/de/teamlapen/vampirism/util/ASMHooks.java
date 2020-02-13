package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.BatVampireAction;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;


public class ASMHooks {
    public static EntitySize getPlayerSize(PlayerEntity player, Pose pose) {
        return BatVampireAction.BAT_SIZE;
    }

    public static boolean overwritePlayerSize(PlayerEntity player) {
        return VampirePlayer.getOpt(player).map(vampire -> vampire.getSpecialAttributes().bat).orElse(false);
    }
}
