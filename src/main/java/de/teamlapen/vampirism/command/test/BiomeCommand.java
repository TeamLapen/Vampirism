package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class BiomeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("biome")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> biome(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int biome(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) {
        ResourceLocation res = Helper.getBiomeId(asPlayer);
        commandSource.sendSuccess(() -> Component.literal(res.toString()), true);
        return 0;
    }
}
