package de.teamlapen.library.common.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class LibraryBlockRegisterHelper {

    public static UnaryOperator<BlockBehaviour.Properties> logProperties(MapColor sideColor, MapColor topColor) {
        return p -> p.mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? sideColor : topColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava();
    }

    public static void setFlammable(DeferredBlock<?> block, int encouragement, int flammability) {
        ((FireBlock) Blocks.FIRE).setFlammable(block.get(), encouragement, flammability);
    }

    public static boolean noView(BlockState var1, BlockGetter var2, BlockPos var3) {
        return false;
    }

    public static boolean noSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType<?> var4) {
        return false;
    }
}
