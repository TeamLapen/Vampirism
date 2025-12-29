package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("entity")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .executes(context -> entity(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int entity(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) {
        List<Entity> l = asPlayer.level().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity entity : l) {
            if (entity instanceof PathfinderMob) {
                Identifier id = RegUtil.id(entity.getType());
                commandSource.sendSuccess(() -> Component.literal(id.toString()), true);
            } else {
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.entity.notbiteable", entity.getClass().getName()), true);
            }
        }
        return 0;
    }
}
