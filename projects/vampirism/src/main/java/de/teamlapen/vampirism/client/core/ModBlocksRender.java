package de.teamlapen.vampirism.client.core;

import de.teamlapen.faction.common.world.blockentity.TotemBlockEntity;
import de.teamlapen.faction.common.world.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.extensions.BlockExtensions;
import de.teamlapen.vampirism.client.renderer.blockentity.*;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataMaps;
import de.teamlapen.vampirism.common.world.blockentity.AlchemicalCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Handles all block render registration including TileEntities
 */
public class ModBlocksRender {

    public static void registerBlockColors(RegisterColorHandlersEvent.@NotNull BlockTintSources event) {
        event.register(List.of(_ -> 0x8855FF,_ -> 0x9966FF), ModBlocks.ALCHEMICAL_FIRE.get());
        event.register(List.of(_ -> 0xFFFFFF, new BlockTintSource() {
            @Override
            public int color(@NonNull BlockState state) {
                return 0xFFFFFF;
            }

            private FluidStateModelSet fluidSet()  {
                return Minecraft.getInstance().getModelManager().getFluidStateModelSet();
            }

            @Override
            public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                if (level.getBlockEntity(pos) instanceof AlchemicalCauldronBlockEntity totem) {
                    return Optional.ofNullable(totem.getItems().getFirst().getCapability(Capabilities.Fluid.ITEM, null))
                            .map(fluidHandler -> ResourceHandlerUtil.findExtractableResource(fluidHandler, x -> true, null))
                            .map(resource -> resource.toStack(1))
                            .map(stack -> {
                                var source = fluidSet().get(stack.getFluid().defaultFluidState()).fluidTintSource();
                                return source == null ? null : source.colorAsStack(stack);
                            })
                            .orElseGet(() -> {
                                var color = totem.getItems().getFirst().typeHolder().getData(ModDataMaps.LIQUID_COLOR_MAP);
                                return color != null ? color : 0x00003B;
                            });
                }
                return 0xFFFFFF;
            }
        }), ModBlocks.ALCHEMICAL_CAULDRON.get());
        event.register(List.of(_ -> 0xFFFFFF, new BlockTintSource() {
            @Override
            public int color(@NonNull BlockState state) {
                return 0xFFFFFF;
            }

            @Override
            public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                return level.getBlockEntity(pos) instanceof TotemBlockEntity totem ? totem.getControllingFaction().value().getColor() : 0xFFFFFF;
            }
        }),TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.COFFIN.get(), CoffinRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_INFUSION.get(), AltarInfusionRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_PEDESTAL.get(), PedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BAT_CAGE.get(), BatCageRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOTHER_TROPHY.get(), MotherTrophyRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FOG_DIFFUSER.get(), FogDiffuserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VAMPIRE_BEACON.get(), VampireBeaconRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_CONTAINER.get(), BloodContainerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_INSPIRATION.get(), AltarInspirationRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_GRINDER.get(), BloodGrinderRenderer::new);
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(BlockExtensions.TENT, ModBlocks.TENT.get(), ModBlocks.TENT_MAIN.get());
    }

}
