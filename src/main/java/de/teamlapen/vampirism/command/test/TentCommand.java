package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.tileentity.TileTent;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class TentCommand extends BasicCommand{

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("tent")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return tent(context.getSource(), context.getSource().asPlayer());
                });
    }

    private static int tent(CommandSource commandSource, EntityPlayerMP asPlayer) {
        RayTraceResult result = UtilLib.getPlayerLookingSpot(asPlayer, 5);
        if (result != null && result.type == RayTraceResult.Type.BLOCK) {

            TileEntity tent = asPlayer.getEntityWorld().getTileEntity(result.getBlockPos());
            if (tent != null && tent instanceof TileTent) {
                ((TileTent) tent).setSpawn(true);
                commandSource.sendFeedback(new TextComponentString("Success"), true);
            }

        }
        return 0;
    }
}
