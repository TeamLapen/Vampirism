package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.core.ModWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class DebugGenCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("debugGen")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return debugGen(context.getSource());
                });
    }

    private static int debugGen(CommandSource commandSource) {
        if (ModWorld.debug) {
            ModWorld.debug = false;
            commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.gen_debug.false"), true);
        } else {
            ModWorld.debug = true;
            commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.gen_debug.true"), true);
        }
        return 0;
    }
}
