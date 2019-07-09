package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.GarlicChunkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class GarlicCheckCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("garlicCheck")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return garlicCheck(context.getSource(), context.getSource().asPlayer(), false);
                })
                .then(Commands.literal("print"))
                        .executes(context -> {
                    return garlicCheck(context.getSource(), context.getSource().asPlayer(), true);
                });
    }

    private static int garlicCheck(CommandSource commandSource, ServerPlayerEntity asPlayer, boolean print) {
        if (commandSource.getEntity() != null && commandSource.getEntity() instanceof PlayerEntity)
            commandSource.sendFeedback(new StringTextComponent("Garlic strength: " + VampirismAPI.getGarlicChunkHandler(asPlayer.getEntityWorld()).getStrengthAtChunk(new ChunkPos(asPlayer.getPosition()))), true);
        if(print)
            ((GarlicChunkHandler) VampirismAPI.getGarlicChunkHandler(asPlayer.getEntityWorld())).printDebug(commandSource);
        return 0;
    }
}
