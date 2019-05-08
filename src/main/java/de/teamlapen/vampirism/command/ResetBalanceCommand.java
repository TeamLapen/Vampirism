package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.config.Balance;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class ResetBalanceCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("resetBalance")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN)).executes(context -> {
                    return resetBalance(context, "all");
                })
                .then(Commands.argument("category", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(Balance.getCategories().keySet().stream().map(id -> id.toString()), builder);
                        })
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(new String[] { "all", "help" }, builder);
                        })
                        .executes(context->{
                            return resetBalance(context, StringArgumentType.getString(context, "category"));
                        }));
    }

    private static int resetBalance(CommandContext<CommandSource> context, String cat) {
        if (cat.equals("help")) {
            context.getSource().sendFeedback(new TextComponentString("You can reset Vampirism balance values to the default values. If you have not modified them, this is recommend after every update of Vampirism"), true);
            context.getSource().sendFeedback(new TextComponentString("Use '/vampirism resetBalance all' to reset all categories or specify a category with '/vampirism resetBalance <category>' (Tab completion is supported)"), true);
            return 0;
        }
        if (Balance.resetAndReload(cat)) {
            context.getSource().sendFeedback(new TextComponentTranslation("command.vampirism.base.reset_balance.success", cat), true);
            return 0;
        } else {
            context.getSource().sendErrorMessage(new TextComponentTranslation("command.vampirism.base.reset_balance.not_found", cat));
            return 0;
        }
    }

}
