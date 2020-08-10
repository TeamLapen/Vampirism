package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CaptureVillageCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("captureVillage")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.factionArgument())
                        .executes(context -> capture(context.getSource(), context.getSource().asPlayer(), FactionArgument.getFaction(context, "faction"))));
    }

    private static int capture(CommandSource source, ServerPlayerEntity player, IPlayableFaction faction) {
        source.sendFeedback(TotemHelper.forceFactionCommand(faction, player), true);
        return 0;
    }
}
