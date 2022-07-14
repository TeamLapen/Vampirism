package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.LogUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class InfoEntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("printEntityNBT")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> infoEntity(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int infoEntity(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        List<Entity> l = asPlayer.getCommandSenderWorld().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity o : l) {
            CompoundTag nbt = new CompoundTag();
            o.saveAsPassenger(nbt);
            LogManager.getLogger().info(LogUtil.TEST, "Data {}", nbt);
        }
        commandSource.sendSuccess(Component.translatable("command.vampirism.test.infoentity.printed"), false);
        return 0;
    }
}
