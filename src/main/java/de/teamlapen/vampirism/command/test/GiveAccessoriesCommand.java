package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.command.arguments.RefinementSetArgument;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;


public class GiveAccessoriesCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("giveAccessories")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("set", RefinementSetArgument.actions())
                                .executes(context -> give(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "slot"), RefinementSetArgument.getSet(context, "set")))))
                .then(Commands.literal("help")
                        .executes(GiveAccessoriesCommand::help));
    }

    private static int give(CommandContext<CommandSource> context, ServerPlayerEntity asPlayer, int number, IRefinementSet set) {
        VampireRefinementItem i;
        switch (number){
            case 1:
                i= ModItems.amulet;
                break;
            case 2:
                i = ModItems.ring;
                break;
            default:
                i = ModItems.obi_belt;
                break;
        }
        ItemStack s = new ItemStack(i);
        if(i.applyRefinementSet(s,set)){
            asPlayer.addItemStackToInventory(s);
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.test.give_accessories.success", set.getName(), number), false);
        }
        else{
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.test.give_accessories.incompatible", set.getName(), number), false);
        }

        return 0;
    }

    private static int help(CommandContext<CommandSource> context) {
        context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.test.give_accessories.help"), false);
        return 0;
    }

}
