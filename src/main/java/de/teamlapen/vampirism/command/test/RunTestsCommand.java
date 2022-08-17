package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.tests.Tests;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class RunTestsCommand extends BasicCommand { //TODO "unit test" can potentially run in 1.18 with help of forge (with headless java) https://github.com/MinecraftForge/MinecraftForge/pull/8225

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("runTests")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> runTests(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int runTests(@NotNull ServerPlayer asPlayer) {
        Tests.runTests(asPlayer.getCommandSenderWorld(), asPlayer);
        return 0;
    }
}
