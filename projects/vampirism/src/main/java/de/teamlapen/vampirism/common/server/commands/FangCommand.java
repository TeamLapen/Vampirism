package de.teamlapen.vampirism.common.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class FangCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("fang")
                .then(Commands.argument("type", IntegerArgumentType.integer(0,6))
                        .executes(context -> setFang(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "type"))));
    }

    private static int setFang(@NotNull CommandContext<CommandSourceStack> context, @NotNull Player player, int type) {
        VampirePlayer.get(player).setFangType(VIdentifier.mod("fang" + type));
        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.fang.success", type), false);
        return type;
    }

}
