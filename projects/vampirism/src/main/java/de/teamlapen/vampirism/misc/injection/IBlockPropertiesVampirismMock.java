package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IBlockProperties;
import net.minecraft.resources.DependantName;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Deprecated
public interface IBlockPropertiesVampirismMock extends IBlockProperties {
    @Override
    default BlockBehaviour.Properties vampirism$description(DependantName<Block, String> dependant) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
