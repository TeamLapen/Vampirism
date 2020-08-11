package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;


public class GenderCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("title-gender").then(Commands.argument("female", BoolArgumentType.bool()).executes(context -> setGender(context, context.getSource().asPlayer(), BoolArgumentType.getBool(context, "female"))));

    }

    private static int setGender(CommandContext<CommandSource> context, PlayerEntity player, boolean female) {
        if (FactionPlayerHandler.getOpt(player).map(fph -> fph.setTitleGender(female)).orElse(false)) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.gender.success"), false);

        }
        return 0;
    }
}
