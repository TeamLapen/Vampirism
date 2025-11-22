package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.misc.extension.IBlockProperties;
import net.minecraft.resources.DependantName;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements IBlockProperties {

    @Shadow
    private DependantName<Block, String> descriptionId;

    @Unique
    @Override
    public BlockBehaviour.Properties vampirism$description(@NotNull DependantName<Block, String> dependant) {
        this.descriptionId = dependant;
        return (BlockBehaviour.Properties) (Object) this;
    }
}
