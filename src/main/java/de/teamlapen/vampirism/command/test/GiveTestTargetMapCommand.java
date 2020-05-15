package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class GiveTestTargetMapCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("giveTestTargetMap")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return giveTestTargetMap(context.getSource().asPlayer());
                });
    }

    private static int giveTestTargetMap(ServerPlayerEntity asPlayer) {
        ServerWorld w = asPlayer.getServerWorld();
        BlockPos dungeonPos = new BlockPos(0,0,0);
        ItemStack itemstack = FilledMapItem.setupNewMap(w, dungeonPos.getX(), dungeonPos.getZ(), (byte) 2, true, true);
        FilledMapItem.func_226642_a_(w, itemstack); //renderBiomePreviewMap
        MapData.addTargetDecoration(itemstack, dungeonPos, "+", MapDecoration.Type.TARGET_X);
        asPlayer.dropItem(itemstack, false);
        return 0;
    }

}
