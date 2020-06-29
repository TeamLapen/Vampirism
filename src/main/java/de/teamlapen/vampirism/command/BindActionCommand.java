package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("shortcutnumber", IntegerArgumentType.integer(1, 2))
                        .then(Commands.argument("action", ActionArgument.actions())
                                .executes(context -> bindAction(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "shortcutnumber"), ActionArgument.getAction(context, "action")))))
                .then(Commands.literal("help")
                        .executes(BindActionCommand::help));
    }

    private static int bindAction(CommandContext<CommandSource> context, ServerPlayerEntity asPlayer, int number, IAction action) {
        if (number == 1) {
            FactionPlayerHandler.get(asPlayer).setBoundAction1(action, true);
        } else if (number == 2) {
            FactionPlayerHandler.get(asPlayer).setBoundAction2(action, true);
        }
        context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.bind_action.success", action.getRegistryName() + " (" + new TranslationTextComponent(action.getTranslationKey()).getFormattedText() + ")", number), false);
        return 0;
    }

    private static int help(CommandContext<CommandSource> context) {
        context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.bind_action.help"), false);
        StringBuilder b = new StringBuilder();
        for (ResourceLocation key : ModRegistries.ACTIONS.getKeys()) {
            b.append(key.toString()).append(" ");
        }
        context.getSource().sendFeedback(new StringTextComponent(b.toString()), false);
        return 0;
    }

}
