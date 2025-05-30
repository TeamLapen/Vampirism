package de.teamlapen.vampirism.api;

import net.minecraft.Util;
import net.minecraft.resources.DependantName;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public interface BlockPropertiesExtension {

    @NotNull
    BlockBehaviour.Properties vampirism$description(@NotNull DependantName<Block, String> dependant);

    static BlockBehaviour.Properties withDescription(BlockBehaviour.Properties properties, DependantName<Block, String> dependant) {
        return ((BlockPropertiesExtension) properties).vampirism$description(dependant);
    }

    static BlockBehaviour.Properties descriptionWithout(BlockBehaviour.Properties properties, String regexPathReplace) {
        return ((BlockPropertiesExtension) properties).vampirism$description(block -> Util.makeDescriptionId("block", block.location().withPath(block.location().getPath().replaceAll(regexPathReplace, ""))));
    }
}
