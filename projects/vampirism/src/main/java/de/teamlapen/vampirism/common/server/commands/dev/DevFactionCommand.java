package de.teamlapen.vampirism.common.server.commands.dev;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;

public class DevFactionCommand extends BasicCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("l")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("vampire")
                        .executes(context -> setLevel(ModFactions.VAMPIRE, 14, 5, context.getSource().getPlayerOrException())))
                .then(Commands.literal("hunter")
                .executes(context -> setLevel(ModFactions.HUNTER, 14, 5, context.getSource().getPlayerOrException())));
    }

    public static int setLevel(Holder<? extends IPlayableFaction<?>> faction, final int level, final int lord, ServerPlayer player) {
        FactionPlayerHandler.get(player).setFaction(FactionUpdate.builder().faction(faction).level(level).lordLevel(5).build());
        return 0;
    }
}
