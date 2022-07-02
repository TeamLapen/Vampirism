package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.AlchemicalCauldronTileEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
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
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof AlchemicalCauldronTileEntity) {
                    return ((AlchemicalCauldronTileEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.ALCHEMICAL_CAULDRON.get());
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof TotemTileEntity) {
                    IFaction f = ((TotemTileEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor().getRGB();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
        colors.register((state, worldIn, pos, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.DARK_SPRUCE_LEAVES.get());
    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntityRenderer(ModTiles.COFFIN.get(), CoffinTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.ALTAR_INFUSION.get(), AltarInfusionTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.BLOOD_PEDESTAL.get(), PedestalTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.TOTEM.get(), TotemTESR::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.GARLIC_BEACON.get(), GarlicBeaconTESR::new);
    }

    private static void registerRenderType() {
        RenderType cutout = RenderType.cutout();
        RenderTypeLookup.setRenderLayer(ModBlocks.GARLIC_BEACON_WEAK.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.GARLIC_BEACON_IMPROVED.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.GARLIC_BEACON_NORMAL.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModFluids.IMPURE_BLOOD.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModFluids.BLOOD.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.ALCHEMICAL_CAULDRON.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.ALCHEMICAL_FIRE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.ALTAR_INFUSION.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.ALTAR_INSPIRATION.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.ALTAR_PILLAR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.ALTAR_TIP.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_CONTAINER.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_PEDESTAL.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.POTION_TABLE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_SIEVE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CHURCH_ALTAR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.FIRE_PLACE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.SUNSCREEN_BEACON.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TENT.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TENT_MAIN.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOTEM_BASE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOTEM_TOP.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_GRINDER.get(), cutout);
        CoffinBlock.COFFIN_BLOCKS.values().forEach(coffin -> RenderTypeLookup.setRenderLayer(coffin, cutout));
        RenderTypeLookup.setRenderLayer(ModBlocks.HUNTER_TABLE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.MED_CHAIR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.WEAPON_TABLE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.VAMPIRE_ORCHID.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.GARLIC.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.POTTED_VAMPIRE_ORCHID.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.DARK_SPRUCE_LEAVES.get(), RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.DARK_SPRUCE_SAPLING.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CHANDELIER.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CANDELABRA.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CANDELABRA_WALL.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CROSS.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOMBSTONE1.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOMBSTONE2.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.TOMBSTONE3.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.GRAVE_CAGE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CURSED_ROOTS.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CURSED_SPRUCE_SAPLING.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CURSED_BARK.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.DARK_SPRUCE_DOOR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CURSED_SPRUCE_DOOR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.VAMPIRE_RACK.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.THRONE.get(), cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.ALCHEMY_TABLE.get(), cutout);
    }


}
