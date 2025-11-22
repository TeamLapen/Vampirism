package de.teamlapen.vampirism.common.core;

import de.teamlapen.factions.api.skills.ISkillNode;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPointProvider;
import de.teamlapen.factions.api.skills.SkillPointProviders;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;

@SuppressWarnings("unused")
public class ModSkills {

    static void createSkillNodes(BootstrapContext<ISkillNode> context) {
        HunterSkills.Nodes.createSkillNodes(context);
        VampireSkills.Nodes.createSkillNodes(context);
    }

    static void createSkillTrees(BootstrapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
