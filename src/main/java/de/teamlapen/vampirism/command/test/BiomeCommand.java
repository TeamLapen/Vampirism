package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class BiomeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("biome")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return biome(context.getSource(), context.getSource().asPlayer());
                });
    }

    private static int biome(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        ResourceLocation res = Helper.getBiomeId(asPlayer);
        commandSource.sendFeedback(new StringTextComponent(res.toString()), true);
        return 0;
    }
}
