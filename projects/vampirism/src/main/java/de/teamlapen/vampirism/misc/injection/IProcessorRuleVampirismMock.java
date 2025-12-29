package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

@Deprecated
public interface IProcessorRuleVampirismMock extends IProcessorRule {
    @Override
    default RuleTest getInputPredicate() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default RuleBlockEntityModifier getBlockEntityModifier() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default RuleTest getLocPredicate() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
