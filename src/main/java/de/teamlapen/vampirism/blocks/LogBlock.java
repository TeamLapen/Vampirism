package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class LogBlock extends RotatedPillarBlock {

    public static final WoodType DARK_SPRUCE = WoodType.register(new WoodType(REFERENCE.MODID + ":dark_spruce", BlockSetType.SPRUCE));
    public static final WoodType CURSED_SPRUCE = WoodType.register(new WoodType(REFERENCE.MODID + ":cursed_spruce", BlockSetType.SPRUCE));

    public LogBlock(BlockBehaviour.@NotNull Properties properties) {
        super(properties);
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }

    public LogBlock(MapColor color1, MapColor color2) {
        super(BlockBehaviour.Properties.of().mapColor((p_235431_2_) -> {
            return p_235431_2_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? color1 : color2;
        }).strength(2.0F).ignitedByLava().sound(SoundType.WOOD));
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }
}
