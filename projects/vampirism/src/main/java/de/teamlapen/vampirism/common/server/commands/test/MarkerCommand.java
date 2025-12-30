package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class MarkerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("marker")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .executes(context -> marker(null)).then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(context -> marker(StringArgumentType.getString(context, "args"))
                        ));
    }

    @SuppressWarnings("SameReturnValue")
    private static int marker(@Nullable String args) {
        LogManager.getLogger().debug("************************************************************");
        LogManager.getLogger().debug("");
        LogManager.getLogger().debug("Marker %s");
        if (args != null) LogManager.getLogger().debug(args);
        LogManager.getLogger().debug("");
        LogManager.getLogger().debug("***********************************************************");
        return 0;
    }

}
