package de.teamlapen.faction.common.server.commands.actions;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ActionsCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("actions")
                .then(BindActionCommand.register(buildContext))
                .then(ResetActionsCommand.register());
    }

}
