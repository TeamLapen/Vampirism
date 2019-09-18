package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import org.apache.logging.log4j.LogManager;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class MarkerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("marker")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return marker(null);
                }).then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(context -> {
                                    return marker(StringArgumentType.getString(context, "args"));
                                }
                        ));
    }

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
