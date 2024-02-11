package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessorRule.class)
public interface ProcessorRuleAccessor {

    @Accessor("inputPredicate")
    RuleTest getInputPredicate();

    @Accessor("outputState")
    BlockState getOutputState();

    @Accessor("blockEntityModifier")
    RuleBlockEntityModifier getBlockEntityModifier();

    @Accessor("locPredicate")
    RuleTest getLocPredicate();
}
