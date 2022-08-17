package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class VampireBookCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("vampireBook")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> vampireBook(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int vampireBook(@NotNull ServerPlayer asPlayer) {
        asPlayer.getInventory().add(VampireBookManager.getInstance().getRandomBookItem(asPlayer.getRandom()));
        return 0;
    }
}
