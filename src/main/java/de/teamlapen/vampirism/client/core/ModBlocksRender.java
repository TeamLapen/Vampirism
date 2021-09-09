package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.blockentity.AlchemicalCauldronBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.color.block.BlockColors;
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

    public static void registerColors() {
        BlockColors colors = Minecraft.getInstance().getBlockColors();
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire);
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof AlchemicalCauldronBlockEntity) {
                    return ((AlchemicalCauldronBlockEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron);
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                BlockEntity tile = (worldIn == null || pos == null) ? null : worldIn.getBlockEntity(pos);
                if (tile instanceof TotemBlockEntity) {
                    IFaction f = ((TotemBlockEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor().getRGB();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
        colors.register((state, worldIn, pos, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.vampire_spruce_leaves);
        colors.register((state, worldIn, pos, tintIndex) -> {
            return 0x2e0606;
        }, ModBlocks.bloody_spruce_leaves);
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTiles.coffin, CoffinBESR::new);
        event.registerBlockEntityRenderer(ModTiles.altar_infusion, AltarInfusionBESR::new);
        event.registerBlockEntityRenderer(ModTiles.blood_pedestal, PedestalBESR::new);
        event.registerBlockEntityRenderer(ModTiles.totem, TotemBESR::new);
        event.registerBlockEntityRenderer(ModTiles.garlic_beacon, GarlicBeaconBESR::new);
    }

    private static void registerRenderType() {
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffusor_weak, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffusor_improved, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic_diffusor_normal, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.impure_blood, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.blood, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.alchemical_cauldron, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.alchemical_fire, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_infusion, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_inspiration, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_pillar, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.altar_tip, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_container, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_pedestal, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.potion_table, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_sieve, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.church_altar, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.fire_place, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.sunscreen_beacon, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tent, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tent_main, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_base, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top_vampirism_hunter, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.totem_top_vampirism_vampire, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.blood_grinder, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.coffin, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.hunter_table, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.med_chair, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.weapon_table, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.vampire_orchid, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.garlic, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.potted_vampire_orchid, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.bloody_spruce_leaves, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.vampire_spruce_leaves, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.bloody_spruce_sapling, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.chandelier, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.candelabra, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.candelabra_wall, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.cross, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone1, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone2, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.tombstone3, cutout);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.grave_cage, cutout);
    }


}
