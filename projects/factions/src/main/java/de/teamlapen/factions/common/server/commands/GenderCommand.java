package de.teamlapen.factions.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.command.EnumArgument;


public class GenderCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("gender")
                .then(Commands.argument("gender", EnumArgument.enumArgument(IPlayableFaction.TitleGender.class))
                        .executes(context -> setGender(context, context.getSource().getPlayerOrException(), context.getArgument("gender", IPlayableFaction.TitleGender.class)))
                );

    }

    @SuppressWarnings("SameReturnValue")
    private static int setGender(CommandContext<CommandSourceStack> context, Player player, IPlayableFaction.TitleGender gender) {
        FactionPlayerHandler.get(player).setTitleGender(gender);
        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.gender.success"), false);
        return 0;
    }
}
