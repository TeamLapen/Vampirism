package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class EmptyBloodBarCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("emptyBloodBar")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return emptyBloodBar(context.getSource().asPlayer());
                });
    }

    private static int emptyBloodBar(ServerPlayerEntity asPlayer) {
        LazyOptional<VampirePlayer> player = VampirePlayer.getOpt(asPlayer);
        if (player.map(VampirismPlayer::getLevel).orElse(0) > 0) {
            player.map(vampire -> vampire.useBlood(Integer.MAX_VALUE, true));
        }
        return 0;
    }
}
