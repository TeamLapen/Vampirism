package de.teamlapen.vampirism.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PureBloodBlock extends Block {

    public static final IntegerProperty PURITY = IntegerProperty.create("purity", 0, 6);

    public PureBloodBlock(Properties properties) {
        super(properties);
    }
}
