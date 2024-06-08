package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("shortcutnumber", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("action", ResourceArgument.resource(buildContext, VampirismRegistries.Keys.ACTION))
                                .executes(context -> bindAction(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "shortcutnumber"), ActionArgument.getAction(context, "action")))))
                .then(Commands.literal("help")
                        .executes(BindActionCommand::help));
    }

    @SuppressWarnings("SameReturnValue")
    private static int bindAction(@NotNull CommandContext<CommandSourceStack> context, @NotNull ServerPlayer asPlayer, int number, @NotNull Holder<IAction<?>> action) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(asPlayer);
        handler.setBoundAction(number, action, true, true);
        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.bind_action.success", action.value().getName(), number), false);
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int help(@NotNull CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.bind_action.help"), false);
        return 0;
    }

}
