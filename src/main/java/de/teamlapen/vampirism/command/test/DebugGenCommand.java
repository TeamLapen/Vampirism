package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class DebugGenCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("debugGen")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return debugGen(context.getSource());
                });
    }

    private static int debugGen(CommandSourceStack commandSource) {
        if (VampirismWorldGen.debug) {
            VampirismWorldGen.debug = false;
            commandSource.sendSuccess(new TranslatableComponent("command.vampirism.test.gen_debug.false"), true);
        } else {
            VampirismWorldGen.debug = true;
            commandSource.sendSuccess(new TranslatableComponent("command.vampirism.test.gen_debug.true"), true);
        }
        return 0;
    }
}
