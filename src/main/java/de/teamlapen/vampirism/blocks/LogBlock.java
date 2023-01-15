package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

public class LogBlock extends RotatedPillarBlock {

    public static final WoodType dark_spruce = WoodType.register(WoodType.create(REFERENCE.MODID + ":dark_spruce"));
    public static final WoodType cursed_spruce = WoodType.register(WoodType.create(REFERENCE.MODID + ":cursed_spruce"));

    public LogBlock(BlockBehaviour.@NotNull Properties properties) {
        super(properties);
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }

    public LogBlock(MaterialColor color1, MaterialColor color2) {
        super(BlockBehaviour.Properties.of(Material.WOOD, (p_235431_2_) -> {
            return p_235431_2_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? color1 : color2;
        }).strength(2.0F).sound(SoundType.WOOD));
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }
}
