package de.teamlapen.faction.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import de.teamlapen.faction.common.network.packets.server.ClientboundActionBindingPacket;
import de.teamlapen.faction.common.server.commands.arguments.ActionArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;

public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("bind-action")
                .then(Commands.argument("key", EnumArgument.enumArgument(ActionKeys.class))
                        .then(Commands.argument("action", ResourceArgument.resource(buildContext, FactionRegistries.Keys.ACTION))
                                .executes(context -> bindAction(context, context.getSource().getPlayerOrException(), context.getArgument("key", ActionKeys.class), ActionArgument.getAction(context, "action")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int bindAction(CommandContext<CommandSourceStack> context, ServerPlayer asPlayer, ActionKeys key, Holder<IAction<?>> action) {
        asPlayer.connection.send(new ClientboundActionBindingPacket(key, action));
        context.getSource().sendSuccess(() -> Component.translatable("command.factionapi.base.bind_action.success", action.value().getName(), key.ordinal() + 1), false);
        return 0;
    }

}
