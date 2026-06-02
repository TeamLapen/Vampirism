package de.teamlapen.vampirism.misc.mixin;

import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StructureBlockEntity.class)
public class StructureMixin {

    @ModifyConstant(method = "loadAdditional", constant = @Constant(intValue = 48))
    private int increaseStructureSize(int constant) {
        return 128;
    }
    @ModifyConstant(method = "loadAdditional", constant = @Constant(intValue = -48))
    private int increaseStruct2ureSize(int constant) {
        return -128;
    }
}
