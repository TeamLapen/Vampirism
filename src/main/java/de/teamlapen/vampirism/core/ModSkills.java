package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.data.worldgen.BootstapContext;

public class ModSkills {

    public static void createSkillNodes(BootstapContext<ISkillNode> context) {
        HunterSkills.Nodes.createSkillNodes(context);
        VampireSkills.Nodes.createSkillNodes(context);
    }

    public static void createSkillTrees(BootstapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
