package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

public interface IProcessorRuleMock extends IProcessorRule {
    @Override
    default RuleTest getInputPredicate() {
        return null;
    }

    @Override
    default RuleBlockEntityModifier getBlockEntityModifier() {
        return null;
    }

    @Override
    default RuleTest getLocPredicate() {
        return null;
    }
}
