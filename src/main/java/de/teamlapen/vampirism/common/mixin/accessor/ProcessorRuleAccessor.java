package de.teamlapen.vampirism.common.mixin.accessor;

import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessorRule.class)
public interface ProcessorRuleAccessor {

    @Accessor("inputPredicate")
    RuleTest getInputPredicate();

    @Accessor("blockEntityModifier")
    RuleBlockEntityModifier getBlockEntityModifier();

    @Accessor("locPredicate")
    RuleTest getLocPredicate();
}
