package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("entity")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> entity(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int entity(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) {
        List<Entity> l = asPlayer.getCommandSenderWorld().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity entity : l) {
            if (entity instanceof PathfinderMob) {
                ResourceLocation id = RegUtil.id(entity.getType());
                commandSource.sendSuccess(() -> Component.literal(id.toString()), true);
            } else {
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.entity.notbiteable", entity.getClass().getName()), true);
            }
        }
        return 0;
    }
}
