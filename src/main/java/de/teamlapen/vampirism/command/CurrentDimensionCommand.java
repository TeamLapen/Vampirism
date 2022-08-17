package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class CurrentDimensionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("currentDimension")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> currentDimension(context, context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int currentDimension(@NotNull CommandContext<CommandSourceStack> context, @NotNull ServerPlayer asPlayer) {
        context.getSource().sendSuccess(Component.translatable("command.vampirism.base.currentdimension.dimension", asPlayer.getCommandSenderWorld().dimension().location() + " (" + asPlayer.getServer().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getKey(asPlayer.getCommandSenderWorld().dimensionType())), false);
        return 0;
    }
}
