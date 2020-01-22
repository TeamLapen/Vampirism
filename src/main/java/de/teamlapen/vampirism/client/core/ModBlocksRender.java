package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.AltarInfusionTESR;
import de.teamlapen.vampirism.client.render.tiles.CoffinTESR;
import de.teamlapen.vampirism.client.render.tiles.PedestalTESR;
import de.teamlapen.vampirism.client.render.tiles.TotemTESR;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.AlchemicalCauldronTileEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Handles all block render registration including TileEntities
 */
@OnlyIn(Dist.CLIENT)
public class ModBlocksRender {


    public static void register() {
        registerTileRenderer();
        registerRenderType();
    }

    public static void registerColors() {
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile instanceof AlchemicalCauldronTileEntity) {
                    return ((AlchemicalCauldronTileEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile instanceof TotemTileEntity) {
                    IFaction f = ((TotemTileEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor().getRGB();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getTotems());
    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntityRenderer(ModTiles.coffin, CoffinTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.altar_infusion, AltarInfusionTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.blood_pedestal, PedestalTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.totem, TotemTESR::new);
    }

    private static void registerRenderType() {
        RenderType cutout = RenderType.cutout();
        RenderTypeLookup.setRenderLayer(ModBlocks.garlic_beacon_weak, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.garlic_beacon_improved, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.garlic_beacon_normal, cutout);
        RenderTypeLookup.setRenderLayer(ModFluids.impure_blood, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.blood, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.alchemical_cauldron, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.alchemical_fire, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.altar_infusion, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.altar_inspiration, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.altar_pillar, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.altar_tip, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.blood_container, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.blood_pedestal, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.blood_potion_table, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.blood_sieve, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.church_altar, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.fire_place, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.sunscreen_beacon, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.tent, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.tent_main, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.totem_base, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.totem_top, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.totem_top_vampirism_hunter, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.totem_top_vampirism_vampire, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.blood_grinder, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.coffin, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.hunter_table, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.med_chair, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.weapon_table, cutout);


    }


}
