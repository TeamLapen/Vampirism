package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;

public class GiveBannerCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("banner")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.factionArgument())
                        .executes(context -> giveBannerItem(FactionArgument.getFaction(context, "faction"), context.getSource().asPlayer()))
                );
    }

    private static int giveBannerItem(@Nonnull IFaction<?> faction, @Nonnull ServerPlayerEntity player) {
        player.addItemStackToInventory(faction.getVillageData().getBanner());
        return 0;
    }
}
