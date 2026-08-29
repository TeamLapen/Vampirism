package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import net.minecraft.data.worldgen.BootstrapContext;

@SuppressWarnings("unused")
public class ModSkills {

    public static void createSkillSegments(BootstrapContext<ISkillSegment> context) {
        HunterSkills.Segments.createSkillSegments(context);
        VampireSkills.Segments.createSkillSegments(context);
    }

    static void createSkillTrees(BootstrapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
