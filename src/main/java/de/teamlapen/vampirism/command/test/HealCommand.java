package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class HealCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("heal")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return heal(context.getSource().asPlayer());
                });
    }

    private static int heal(EntityPlayerMP asPlayer) {
        asPlayer.heal(10000);
        return 0;
    }
}
