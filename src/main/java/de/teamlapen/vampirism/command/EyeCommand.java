package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class EyeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("eye")
                .then(Commands.argument("type", IntegerArgumentType.integer(0, REFERENCE.EYE_TYPE_COUNT - 1))
                        .executes(context -> {
                            return setEye(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "type"));
                        }));
    }

    private static int setEye(CommandContext<CommandSource> context, PlayerEntity player, int type) {
        if (VampirePlayer.getOpt(player).map(vampire -> vampire.setEyeType(type)).orElse(false)) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.eye.success", type), false);
        }
        return type;
    }

}
