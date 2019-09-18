package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.world.gen.structures.StructureManager;
import de.teamlapen.vampirism.world.gen.structures.VampirismTemplate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.template.PlacementSettings;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class PlaceCommand extends BasicCommand {//TODO structure ArgumentType

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("place")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("structure", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(StructureManager.Structure.getNames(), builder);
                        })
                        .executes(context -> {
                            return place(context.getSource(), context.getSource().asPlayer(), StringArgumentType.getString(context, "structure"));
                        }));
    }

    private static int place(CommandSource commandSource, ServerPlayerEntity asPlayer, String structure) {
        try {
            StructureManager.Structure s = StructureManager.Structure.valueOf(structure);
            VampirismTemplate template = StructureManager.get(s);
            if (template == null) {
                commandSource.sendErrorMessage(new TranslationTextComponent("command.vampirism.test.place.notloaded", s));
            }
            template.addBlocksToWorld(asPlayer.world, asPlayer.getPosition().offset(Direction.NORTH), new PlacementSettings());

        } catch (IllegalArgumentException e) {
            commandSource.sendErrorMessage(new TranslationTextComponent("command.vampirism.test.place.notfound", structure));
        }
        return 0;
    }
}
