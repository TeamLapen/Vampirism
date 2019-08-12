package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class ResetBalanceCommand extends BasicCommand {//TODO 1.14 Balance category ArgumentType

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("resetBalance")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN)).executes(context -> {
                    return resetBalance(context, "all");
                })
                .then(Commands.argument("category", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(Balance.getCategories().keySet().stream(), builder);
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
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.reset_balance.help1"), false);
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.reset_balance.help2"), false);
            return 0;
        }
        if (Balance.resetAndReload(cat)) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.reset_balance.success", cat), true);
            return 0;
        } else {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.vampirism.base.reset_balance.not_found", cat));
            return 0;
        }
    }

}
