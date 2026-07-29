package de.teamlapen.vampirism.common.util;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.faction.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VampireVillage {

    public static VillageBanner create() {
        return new VillageBanner(Items.BLACK_BANNER.builtInRegistryHolder(), List.of(
                new Pair<>(BannerPatterns.TRIANGLES_BOTTOM, DyeColor.RED),
                new Pair<>(BannerPatterns.TRIANGLES_TOP, DyeColor.RED),
                new Pair<>(BannerPatterns.BORDER, DyeColor.PURPLE),
                new Pair<>(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.RED),
                new Pair<>(BannerPatterns.STRAIGHT_CROSS, DyeColor.RED),
                new Pair<>(BannerPatterns.CIRCLE_MIDDLE, DyeColor.PURPLE)
        ));
    }
}
