package de.teamlapen.vampirism.common.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.commands.BasicCommand;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.common.commands.arguments.FactionArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class GiveBannerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("banner")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                        .executes(context -> giveBannerItem(FactionArgument.getFaction(context, "faction"), context.getSource().getPlayerOrException()))
                );
    }

    @SuppressWarnings("SameReturnValue")
    private static int giveBannerItem(@NotNull Holder<IFaction<?>> faction, @NotNull ServerPlayer player) {
        player.addItem(faction.value().getVillageData().createBanner(player.registryAccess()));
        return 0;
    }
}
