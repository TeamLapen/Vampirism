package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class GiveBannerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("banner")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.factions())
                        .executes(context -> giveBannerItem(FactionArgument.getFaction(context, "faction"), context.getSource().getPlayerOrException()))
                );
    }

    @SuppressWarnings("SameReturnValue")
    private static int giveBannerItem(@Nonnull IFaction<?> faction, @Nonnull ServerPlayer player) {
        player.addItem(faction.getVillageData().getBanner());
        return 0;
    }
}
