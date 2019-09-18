package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

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
        VampirePlayer player = VampirePlayer.get(asPlayer);
        if (player.getLevel() > 0) {
            player.useBlood(Integer.MAX_VALUE, true);
        }
        return 0;
    }
}
