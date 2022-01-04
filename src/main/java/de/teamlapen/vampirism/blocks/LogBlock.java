package de.teamlapen.vampirism.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;

public class LogBlock extends RotatedPillarBlock {

    public static WoodType dark_spruce = WoodType.register(WoodType.create("vampirism:dark_spruce"));
    public static WoodType cursed_spruce = WoodType.register(WoodType.create("vampirism:cursed_spruce"));

    public LogBlock(AbstractBlock.Properties properties) {
        super(properties);
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }

    public LogBlock(MaterialColor color1, MaterialColor color2){
        super(AbstractBlock.Properties.of(Material.WOOD, (p_235431_2_) -> {
            return p_235431_2_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? color1 : color2;
        }).strength(2.0F).sound(SoundType.WOOD));
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }
}
