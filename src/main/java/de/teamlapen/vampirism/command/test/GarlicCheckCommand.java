package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;

public class GarlicCheckCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("garlicCheck")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return garlicCheck(context.getSource(), context.getSource().getPlayerOrException(), false);
                })
                .then(Commands.literal("print"))
                .executes(context -> {
                    return garlicCheck(context.getSource(), context.getSource().getPlayerOrException(), true);
                });
    }

    private static int garlicCheck(CommandSource commandSource, ServerPlayerEntity asPlayer, boolean print) {
        if (commandSource.getEntity() != null && commandSource.getEntity() instanceof PlayerEntity) {
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.garliccheck.strength" + VampirismAPI.getVampirismWorld(asPlayer.getCommandSenderWorld()).map(w -> w.getStrengthAtChunk(new ChunkPos(asPlayer.blockPosition()))).orElse(EnumStrength.NONE)), true);
        }
        if (print) {
            VampirismWorld.getOpt(asPlayer.getCommandSenderWorld()).ifPresent(vw -> vw.printDebug(commandSource));
        }
        return 0;
    }
}
