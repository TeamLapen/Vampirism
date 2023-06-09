package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class DarkSpruceLeavesBlock extends LeavesBlock {

    public DarkSpruceLeavesBlock() {
        super(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2F).randomTicks().ignitedByLava().pushReaction(PushReaction.DESTROY).isViewBlocking(UtilLib::never).sound(SoundType.GRASS).noOcclusion());
        ((FireBlock) Blocks.FIRE).setFlammable(this, 30, 60);
    }
}
