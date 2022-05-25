package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.AlchemicalCauldronBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

/**
 * Handles all block render registration including TileEntities
 */
@OnlyIn(Dist.CLIENT)
public class ModBlocksRender {


    public static void register() {
        registerRenderType();
    }

    public static void registerColorsUnsafe() {
        BlockColors colors = Minecraft.getInstance().getBlockColors();
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire.get());
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof AlchemicalCauldronBlockEntity) {
                    return ((AlchemicalCauldronBlockEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron.get());
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof TotemBlockEntity) {
                    IFaction<?> f = ((TotemBlockEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
        colors.register((state, worldIn, pos, tintIndex) -> 0x1E1F1F, ModBlocks.vampire_spruce_leaves.get());
        colors.register((state, worldIn, pos, tintIndex) -> 0x2e0606, ModBlocks.bloody_spruce_leaves.get());
        colors.register((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : GrassColor.get(0.5D, 1.0D), ModBlocks.cursed_grass_block.get());
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTiles.coffin, CoffinBESR::new);
        event.registerBlockEntityRenderer(ModTiles.altar_infusion, AltarInfusionBESR::new);
        event.registerBlockEntityRenderer(ModTiles.blood_pedestal, PedestalBESR::new);
        event.registerBlockEntityRenderer(ModTiles.totem, TotemBESR::new);
        event.registerBlockEntityRenderer(ModTiles.garlic_diffuser, GarlicDiffuserBESR::new);
    }

    private static void registerRenderType() {
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffuser_weak.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffuser_improved.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffuser_normal.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.impure_blood, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.blood, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.alchemical_cauldron.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.alchemical_fire.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_infusion.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_inspiration.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_pillar.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_tip.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_container.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_pedestal.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.potion_table.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_sieve.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_cleansing.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.fire_place.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.sunscreen_beacon.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tent.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tent_main.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_base.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top_vampirism_hunter.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top_vampirism_vampire.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_grinder.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.coffin.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.hunter_table.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.med_chair.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.weapon_table.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.vampire_orchid.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.potted_vampire_orchid.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.bloody_spruce_leaves.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.vampire_spruce_leaves.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.bloody_spruce_sapling.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.vampire_spruce_sapling.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.chandelier.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.candelabra.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.candelabra_wall.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.cross.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone1.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone2.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone3.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.grave_cage.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.cursed_grass_block.get(), RenderType.cutoutMipped());
    }


}
