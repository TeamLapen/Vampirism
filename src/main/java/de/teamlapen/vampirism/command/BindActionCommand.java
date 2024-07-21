package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.ActionKeys;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("key", EnumArgument.enumArgument(ActionKeys.class))
                        .then(Commands.argument("action", ResourceArgument.resource(buildContext, VampirismRegistries.Keys.ACTION))
                                .executes(context -> bindAction(context, context.getSource().getPlayerOrException(), context.getArgument("key", ActionKeys.class), ActionArgument.getAction(context, "action")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int bindAction(@NotNull CommandContext<CommandSourceStack> context, @NotNull ServerPlayer asPlayer, ActionKeys key, @NotNull Holder<IAction<?>> action) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(asPlayer);
        handler.setBoundAction(key, action, true);
        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.bind_action.success", action.value().getName(), key.ordinal() + 1), false);
        return 0;
    }

}
