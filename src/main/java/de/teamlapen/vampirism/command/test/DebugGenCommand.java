package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class DebugGenCommand extends BasicCommand{

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("debugGen")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return debugGen(context.getSource());
                });
    }

    private static int debugGen(CommandSource commandSource) {
        if (VampirismWorldGen.debug) {
            VampirismWorldGen.debug = false;
            commandSource.sendFeedback(new TextComponentTranslation("command.vampirism.test.gen_debug.false"), true);
        } else {
            VampirismWorldGen.debug = true;
            commandSource.sendFeedback(new TextComponentTranslation("command.vampirism.test.gen_debug.true"), true);
        }
        return 0;
    }
}
