package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class CheckForVampireBiomeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("checkForVampireBiome")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("maxRadius in chunks", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            return checkForVampireBiome(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "maxRadius in chunks"), false);
                        })
                        .then(Commands.literal("!yes")
                                .executes(context -> {
                                    return checkForVampireBiome(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "maxRadius in chunks"), true);
                                })));
    }

    private static int checkForVampireBiome(CommandContext<CommandSource> context, ServerPlayerEntity entityPlayerMP, int radius, boolean force) {
        if (VampirismConfig.COMMON.disableVampireForest.get()) {
            context.getSource().sendFeedback(new TranslationTextComponent("The Vampire Biome is disabled in the config file"), false);
            return 0;
        }
        if (radius > 500 && !force) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.vampire_biome.time_warning", "checkForVampireBiome", radius), false);
        } else {
            List<Biome> biomes = new ArrayList<>();
            biomes.add(ModBiomes.vampire_forest);
            context.getSource().sendFeedback((new TranslationTextComponent("command.vampirism.base.vampire_biome.searching")), true);
            ChunkPos pos = UtilLib.findNearBiome(entityPlayerMP.getEntityWorld(), entityPlayerMP.getPosition(), radius, biomes, context.getSource());
            if (pos == null) {
                context.getSource().sendFeedback((new TranslationTextComponent("command.vampirism.base.vampire_biome.not_found")), true);
            } else {
                context.getSource().sendFeedback((new TranslationTextComponent("command.vampirism.base.vampire_biome.found", pos)), true);
            }
        }
        return 0;
    }


}
