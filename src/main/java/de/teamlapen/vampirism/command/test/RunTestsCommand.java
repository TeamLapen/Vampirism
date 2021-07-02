package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.tests.Tests;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class RunTestsCommand extends BasicCommand { //TODO "unit test" can potentially run in 1.17 with help of forge (with headless java)

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("runTests")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return runTests(context.getSource().asPlayer());
                });
    }

    private static int runTests(ServerPlayerEntity asPlayer) {
        Tests.runTests(asPlayer.getEntityWorld(), asPlayer);
        return 0;
    }
}
