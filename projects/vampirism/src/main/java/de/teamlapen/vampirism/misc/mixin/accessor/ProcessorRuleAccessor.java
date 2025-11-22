package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessorRule.class)
public interface ProcessorRuleAccessor extends IProcessorRule {

    @Override
    @Accessor("inputPredicate")
    RuleTest getInputPredicate();

    @Override
    @Accessor("blockEntityModifier")
    RuleBlockEntityModifier getBlockEntityModifier();

    @Override
    @Accessor("locPredicate")
    RuleTest getLocPredicate();
}
