package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.command.arguments.RefinementSetArgument;
import de.teamlapen.vampirism.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.items.RefinementItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class GiveAccessoriesCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("giveAccessories")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("set", RefinementSetArgument.set())
                                .executes(context -> give(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "slot"), RefinementSetArgument.getSet(context, "set")))))
                .then(Commands.literal("random")
                        .executes(context -> random(context, context.getSource().getPlayerOrException(), 1))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> random(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("help")
                        .executes(GiveAccessoriesCommand::help));
    }

    @SuppressWarnings("SameReturnValue")
    private static <Z extends Item & IRefinementItem> int give(CommandContext<CommandSourceStack> context, ServerPlayer asPlayer, int number, IRefinementSet set) {
        IFaction<?> faction = set.getFaction();
        if (faction instanceof PlayableFaction<?>) { // should always be true
            Z i = ((PlayableFaction<?>) faction).getRefinementItem(IRefinementItem.AccessorySlotType.values()[number - 1]);
            ItemStack s = new ItemStack(i);
            if (i.applyRefinementSet(s, set)) {
                asPlayer.addItem(s);
                context.getSource().sendSuccess(Component.translatable("command.vampirism.test.give_accessories.success", set.getName(), number), false);
            } else {
                context.getSource().sendSuccess(Component.translatable("command.vampirism.test.give_accessories.incompatible", set.getName(), number), false);
            }
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int help(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(Component.translatable("command.vampirism.test.give_accessories.help"), false);
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static <T extends IFactionPlayer<T>> int random(CommandContext<CommandSourceStack> context, ServerPlayer entity, int amount) {
        IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
        if (faction instanceof PlayableFaction<?>) {
            for (int i = 0; i < amount; ++i) {
                //noinspection unchecked
                ItemStack stack = RefinementItem.getRandomRefinementItem(((PlayableFaction<T>) faction));
                if (!stack.isEmpty()) {
                    entity.addItem(stack);
                } else {
                    context.getSource().sendSuccess(Component.translatable("command.vampirism.test.give_accessories.no_item"), false);
                    return 0;
                }
            }
        }
        return 0;
    }

}
