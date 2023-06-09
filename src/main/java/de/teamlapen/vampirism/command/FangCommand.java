package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FangCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("fang")
                .then(Commands.argument("type", IntegerArgumentType.integer(0, REFERENCE.FANG_TYPE_COUNT - 1))
                        .executes(context -> setFang(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "type"))));
    }

    private static int setFang(@NotNull CommandContext<CommandSourceStack> context, @NotNull Player player, int type) {
        if (VampirePlayer.getOpt(player).map(vampire -> vampire.setFangType(type)).orElse(false)) {
            context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.fang.success", type), false);
        }
        return type;
    }

}
