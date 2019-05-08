package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("number", IntegerArgumentType.integer(1, 2))
                        .then(Commands.argument("action-id", StringArgumentType.word())
                                .executes(context->{
                                    return bindAction(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "number"), StringArgumentType.getString(context, "action-id"));
                                })));
    }

    private static int bindAction(CommandContext<CommandSource> context, EntityPlayerMP asPlayer, int number, String actionID) {
        @Nullable
        ResourceLocation id = new ResourceLocation(actionID);
        if (actionID.equals("null")) {
            id = null;
        }
        if (id == null || VampirismAPI.actionManager().getRegistry().containsKey(id)) {
            if (number == 1) {
                FactionPlayerHandler.get(asPlayer).setBoundAction1(id, true);
            } else if (number == 2) {
                FactionPlayerHandler.get(asPlayer).setBoundAction2(id, true);
            }
            context.getSource().sendFeedback(new TextComponentTranslation("command.vampirism.base.bind_action.success", actionID, number), true);
        } else {
            context.getSource().sendErrorMessage(new TextComponentTranslation("command.vampirism.base.bind_action.not_existing"));
        }
        return 0;
    }

}
