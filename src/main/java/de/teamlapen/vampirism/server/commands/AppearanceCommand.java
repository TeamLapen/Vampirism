package de.teamlapen.vampirism.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.server.commands.BasicCommand;
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
