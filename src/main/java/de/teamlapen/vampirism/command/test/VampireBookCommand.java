package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class VampireBookCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("vampireBook")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return vampireBook(context.getSource().getPlayerOrException());
                });
    }

    private static int vampireBook(ServerPlayerEntity asPlayer) {
        asPlayer.inventory.add(VampireBookManager.getInstance().getRandomBookItem(asPlayer.getRandom()));
        return 0;
    }
}
