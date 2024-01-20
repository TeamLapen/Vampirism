package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessorRule.class)
public interface ProcessorRuleAccessor {

    @Accessor("inputPredicate")
    RuleTest getInputPredicate();

    @Accessor("outputState")
    BlockState getOutputState();
}
