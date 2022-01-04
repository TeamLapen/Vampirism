package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.AlchemicalCauldronBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Handles all block render registration including TileEntities
 */
@OnlyIn(Dist.CLIENT)
public class ModBlocksRender {


    public static void register() {
        registerRenderType();
    }

    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.ALCHEMICAL_FIRE.get());
        event.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof AlchemicalCauldronBlockEntity) {
                    return ((AlchemicalCauldronBlockEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.ALCHEMICAL_CAULDRON.get());
        event.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof TotemBlockEntity) {
                    IFaction<?> f = ((TotemBlockEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
        event.register((state, worldIn, pos, tintIndex) -> 0x1E1F1F, ModBlocks.DARK_SPRUCE_LEAVES.get());
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
        ItemBlockRenderTypes.setRenderLayer(ModFluids.IMPURE_BLOOD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.BLOOD.get(), RenderType.translucent());
    }


}
