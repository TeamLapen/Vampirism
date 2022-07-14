package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;


public class GenderCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("title-gender").then(Commands.argument("female", BoolArgumentType.bool()).executes(context -> setGender(context, context.getSource().getPlayerOrException(), BoolArgumentType.getBool(context, "female"))));

    }

    @SuppressWarnings("SameReturnValue")
    private static int setGender(CommandContext<CommandSourceStack> context, Player player, boolean female) {
        if (FactionPlayerHandler.getOpt(player).map(fph -> fph.setTitleGender(female)).orElse(false)) {
            context.getSource().sendSuccess(Component.translatable("command.vampirism.base.gender.success"), false);

        }
        return 0;
    }
}
