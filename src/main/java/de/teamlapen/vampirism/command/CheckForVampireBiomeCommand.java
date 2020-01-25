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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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
                .executes(context -> checkForVampireBiome(context, context.getSource().asPlayer(), 500, false))
                .then(Commands.argument("maxRadius in chunks", IntegerArgumentType.integer(0))
                        .executes(context -> checkForVampireBiome(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "maxRadius in chunks"), false))
                        .then(Commands.literal("!yes")
                                .executes(context -> checkForVampireBiome(context, context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "maxRadius in chunks"), true))));
    }

    private static int checkForVampireBiome(CommandContext<CommandSource> context, ServerPlayerEntity entityPlayerMP, int radius, boolean force) {
        if (VampirismConfig.SERVER.disableVampireForest.get()) {
            context.getSource().sendFeedback(new TranslationTextComponent("The Vampire Biome is disabled in the config file"), false);
            return 0;
        }
        if (radius > 500 && !force) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.vampire_biome.time_warning", "checkForVampireBiome", radius), false);
        } else {
            List<Biome> biomes = new ArrayList<>();
            biomes.add(ModBiomes.vampire_forest);
            context.getSource().sendFeedback((new TranslationTextComponent("command.vampirism.base.vampire_biome.searching")), true);
            ChunkPos pos = UtilLib.findNearBiome(entityPlayerMP.getServerWorld(), entityPlayerMP.getPosition(), radius, biomes);
            if (pos == null) {
                context.getSource().sendFeedback((new TranslationTextComponent("command.vampirism.base.vampire_biome.not_found")), true);
            } else { //copy from locate command
                int i = MathHelper.floor(getDistance(context.getSource().getPos().getX(), context.getSource().getPos().getZ(), pos.asBlockPos().getX(), pos.asBlockPos().getZ()));
                ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", pos.asBlockPos().getX(), "~", pos.asBlockPos().getZ())).applyTextStyle((p_211746_1_) -> {
                    p_211746_1_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.asBlockPos().getX() + " ~ " + pos.asBlockPos().getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
                });
                context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.vampire_biome.found", itextcomponent, i), false);
            }
        }
        return 0;
    }

    private static float getDistance(double x1, double z1, double x2, double z2) {
        double i = x2 - x1;
        double j = z2 - z1;
        return MathHelper.sqrt((float) (i * i + j * j));
    }

}
