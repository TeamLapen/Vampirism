package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class HealCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("heal")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> heal(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int heal(@NotNull ServerPlayer asPlayer) {
        asPlayer.heal(10000);
        return 0;
    }
}
