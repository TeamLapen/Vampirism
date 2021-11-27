package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.command.arguments.RefinementSetArgument;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;


public class GiveAccessoriesCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("giveAccessories")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("set", RefinementSetArgument.actions())
                                .executes(context -> give(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "slot"), RefinementSetArgument.getSet(context, "set")))))
                .then(Commands.literal("random")
                        .executes(context -> random(context, context.getSource().getPlayerOrException(), 1))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> random(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("help")
                        .executes(GiveAccessoriesCommand::help));
    }

    private static int give(CommandContext<CommandSource> context, ServerPlayerEntity asPlayer, int number, IRefinementSet set) {
        VampireRefinementItem i;
        switch (number) {
            case 1:
                i = ModItems.amulet;
                break;
            case 2:
                i = ModItems.ring;
                break;
            default:
                i = ModItems.obi_belt;
                break;
        }
        ItemStack s = new ItemStack(i);
        if (i.applyRefinementSet(s, set)) {
            asPlayer.addItem(s);
            context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.test.give_accessories.success", set.getName(), number), false);
        } else {
            context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.test.give_accessories.incompatible", set.getName(), number), false);
        }

        return 0;
    }

    private static int help(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.test.give_accessories.help"), false);
        return 0;
    }

    private static <T extends IFactionPlayer<?>> int random(CommandContext<CommandSource> context, ServerPlayerEntity entity, int amount) {
        IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
        if (faction instanceof PlayableFaction<?>) {
            for (int i = 0; i < amount; ++i) {
                ItemStack stack = VampireRefinementItem.getRandomRefinementItem(((PlayableFaction) faction));
                if (!stack.isEmpty()) {
                    entity.addItem(stack);
                } else {
                    context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.test.give_accessories.no_item"), false);
                }
            }
        }
        return 0;
    }

}
