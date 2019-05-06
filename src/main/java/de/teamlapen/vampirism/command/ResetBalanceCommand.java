package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.config.Balance;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class ResetBalanceCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("resetBalance")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN)).executes(context -> {
                    return resetBalance(context, "all");
                })
                .then(Commands.argument("category", StringArgumentType.word())
                        .executes(context->{
                            return resetBalance(context, StringArgumentType.getString(context, "category"));
                        }));
    }

    private static int resetBalance(CommandContext<CommandSource> context, String cat) {
        if (Balance.resetAndReload(cat)) {
            context.getSource().sendFeedback(new TextComponentTranslation("command.vampirism.base.reset_balance.success", cat), true);
            return 1;
        } else {
            context.getSource().sendErrorMessage(new TextComponentTranslation("command.vampirism.base.reset_balance.not_found", cat));
            return 0;
        }
    }

}
