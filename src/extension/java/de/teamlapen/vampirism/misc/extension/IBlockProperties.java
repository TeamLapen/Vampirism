package de.teamlapen.vampirism.misc.extension;

import net.minecraft.Util;
import net.minecraft.resources.DependantName;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface IBlockProperties {

    BlockBehaviour.Properties vampirism$description(DependantName<Block, String> dependant);

    static BlockBehaviour.Properties withDescription(BlockBehaviour.Properties properties, DependantName<Block, String> dependant) {
        return properties.vampirism$description(dependant);
    }

    static BlockBehaviour.Properties descriptionWithout(BlockBehaviour.Properties properties, String regexPathReplace) {
        return properties.vampirism$description(block -> Util.makeDescriptionId("block", block.location().withPath(block.location().getPath().replaceAll(regexPathReplace, ""))));
    }
}
