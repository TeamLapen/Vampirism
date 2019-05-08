package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.VampireBookManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class VampireBookCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("vampireBook")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return vampireBook(context.getSource().asPlayer());
                });
    }

    private static int vampireBook(EntityPlayerMP asPlayer) {
        asPlayer.inventory.addItemStackToInventory(VampireBookManager.getInstance().getRandomBook(asPlayer.getRNG()));
        return 0;
    }
}
