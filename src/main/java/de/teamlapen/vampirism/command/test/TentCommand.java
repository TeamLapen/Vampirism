package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;

public class TentCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("tent")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), false))
                .then(Commands.literal("advanced")
                        .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), true)));
    }

    private static int tent(CommandSource commandSource, ServerPlayerEntity asPlayer, boolean advanced) {
        RayTraceResult result = UtilLib.getPlayerLookingSpot(asPlayer, 5);
        if (result.getType() == RayTraceResult.Type.BLOCK) {

            TileEntity tent = asPlayer.getCommandSenderWorld().getBlockEntity(((BlockRayTraceResult) result).getBlockPos());
            if (tent instanceof TentTileEntity) {
                ((TentTileEntity) tent).setSpawn(true);
                if (advanced) ((TentTileEntity) tent).setAdvanced(true);
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.tent.success"), false);
            }

        }
        return 0;
    }
}
