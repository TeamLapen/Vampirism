package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.faction.common.server.commands.arguments.FactionArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class GiveBannerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("banner")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                        .executes(context -> giveBannerItem(FactionArgument.getFaction(context, "faction"), context.getSource().getPlayerOrException()))
                );
    }

    @SuppressWarnings("SameReturnValue")
    private static int giveBannerItem(@NotNull Holder<IFaction<?>> faction, @NotNull ServerPlayer player) {
        VillageBanner villageBanner = faction.components().get(FactionDataComponents.VILLAGE_BANNER);
        if (villageBanner != null) {
            player.addItem(villageBanner.createBanner(player.registryAccess()));
        }
        return 0;
    }
}
