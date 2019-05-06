package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class LevelCommand extends BasicCommand {
    
    final static IPlayableFaction[] pfactions = VampirismAPI.factionRegistry().getPlayableFactions();

    public static ArgumentBuilder<CommandSource, ?> register() {
        LiteralArgumentBuilder<CommandSource> argument = Commands.literal("level")
                .requires(context->context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT));
        
        for(IPlayableFaction faction : pfactions) {
            argument.then(Commands.literal(faction.name())
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, faction.getHighestReachableLevel()))
                            .executes(context -> {
                                return setLevel(context, context.getSource().asPlayer(), faction, IntegerArgumentType.getInteger(context, "level"));
                            })));
        }

        return argument;
    }
    
    private static int setLevel(CommandContext<CommandSource> context, EntityPlayer player, IPlayableFaction<IFactionPlayer> faction, int level) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        if (level == 0 && !handler.canLeaveFaction()) {
            context.getSource().sendErrorMessage(new TextComponentTranslation("command.vampirism.base.level.cant_leave"));
        }
        if (handler.setFactionAndLevel(faction, level)) {
            context.getSource().sendFeedback(new TextComponentString(player.getName() + " is now a " + faction.getUnlocalizedName() + " level " + level), true);
        } else {
            context.getSource().sendErrorMessage(new TextComponentTranslation("commands.vampirism.failed_to_execute"));
        }
        return level;
    }

}
