package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.LogUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class InfoEntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("printEntityNBT")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return infoEntity(context.getSource(), context.getSource().getPlayerOrException());
                });
    }

    private static int infoEntity(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        List<Entity> l = asPlayer.getCommandSenderWorld().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity o : l) {
            CompoundNBT nbt = new CompoundNBT();
            o.saveAsPassenger(nbt);
            LogManager.getLogger().info(LogUtil.TEST, "Data {}", nbt);
        }
        commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.infoentity.printed"), false);
        return 0;
    }
}
