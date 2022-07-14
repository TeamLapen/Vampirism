package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.LogManager;

public class MarkerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("marker")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> marker(null)).then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(context -> marker(StringArgumentType.getString(context, "args"))
                        ));
    }

    @SuppressWarnings("SameReturnValue")
    private static int marker(String args) {
        LogManager.getLogger().debug("************************************************************");
        LogManager.getLogger().debug("");
        LogManager.getLogger().debug("Marker %s");
        if (args != null) LogManager.getLogger().debug(args);
        LogManager.getLogger().debug("");
        LogManager.getLogger().debug("***********************************************************");
        return 0;
    }

}
