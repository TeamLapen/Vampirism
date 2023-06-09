package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.world.gen.VampireSpruceTree;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class VampireSpruceSaplingBlock extends SaplingBlock {

    public VampireSpruceSaplingBlock() {
        super(new VampireSpruceTree(), Block.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).isViewBlocking(UtilLib::never).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }
}
