package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoEntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("printEntityNBT")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .executes(context -> infoEntity(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int infoEntity(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) {
        List<Entity> l = asPlayer.level().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity o : l) {
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, o.registryAccess());
        }
        commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.infoentity.printed"), false);
        return 0;
    }
}
