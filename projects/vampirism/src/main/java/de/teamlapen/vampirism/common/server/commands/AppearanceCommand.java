package de.teamlapen.vampirism.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AppearanceCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("appearance")
                .then(EyeCommand.register())
                .then(FangCommand.register())
                .then(GlowingEyeCommand.register());
    }
}
