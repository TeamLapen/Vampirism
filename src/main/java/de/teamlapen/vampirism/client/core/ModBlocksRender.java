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
        }, ModBlocks.ALCHEMICAL_FIRE.get());
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof AlchemicalCauldronBlockEntity) {
                    return ((AlchemicalCauldronBlockEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.ALCHEMICAL_CAULDRON.get());
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
        colors.register((state, worldIn, pos, tintIndex) -> 0x1E1F1F, ModBlocks.VAMPIRE_SPRUCE_LEAVES.get());
        colors.register((state, worldIn, pos, tintIndex) -> 0x2e0606, ModBlocks.BLOODY_SPRUCE_LEAVES.get());
        colors.register((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : GrassColor.get(0.5D, 1.0D), ModBlocks.CURSED_GRASS_BLOCK.get());
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTiles.COFFIN.get(), CoffinBESR::new);
        event.registerBlockEntityRenderer(ModTiles.ALTAR_INFUSION.get(), AltarInfusionBESR::new);
        event.registerBlockEntityRenderer(ModTiles.BLOOD_PEDESTAL.get(), PedestalBESR::new);
        event.registerBlockEntityRenderer(ModTiles.TOTEM.get(), TotemBESR::new);
        event.registerBlockEntityRenderer(ModTiles.GARLIC_DIFFUSER.get(), GarlicDiffuserBESR::new);
    }

    private static void registerRenderType() {
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.IMPURE_BLOOD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALCHEMICAL_CAULDRON.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALCHEMICAL_FIRE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR_INFUSION.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR_INSPIRATION.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR_PILLAR.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR_TIP.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_CONTAINER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_PEDESTAL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTION_TABLE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_SIEVE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR_CLEANSING.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.FIRE_PLACE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SUNSCREEN_BEACON.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TENT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TENT_MAIN.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_BASE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_TOP.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOOD_GRINDER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COFFIN.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HUNTER_TABLE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MED_CHAIR.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WEAPON_TABLE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAMPIRE_ORCHID.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GARLIC.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_VAMPIRE_ORCHID.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOODY_SPRUCE_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOODY_SPRUCE_SAPLING.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAMPIRE_SPRUCE_SAPLING.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CHANDELIER.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDELABRA.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDELABRA_WALL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CROSS.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOMBSTONE1.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOMBSTONE2.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TOMBSTONE3.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GRAVE_CAGE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CURSED_GRASS_BLOCK.get(), RenderType.cutoutMipped());
    }


}
