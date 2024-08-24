package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPointProvider;
import de.teamlapen.vampirism.api.entity.player.skills.SkillPointProviders;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;

@SuppressWarnings("unused")
public class ModSkills {

    public static final ISkillPointProvider LEVELING = SkillPointProviders.register(VResourceLocation.mod("leveling"), (factionPlayer, tree) -> tree.is(ModSkillTreeTags.DEFAULT) ? (int) (Math.max(0, factionPlayer.getLevel() - 1) * VampirismConfig.BALANCE.skillPointsPerLevel.get()) : 0);

    public static final ISkillPointProvider LORD_LEVELING = SkillPointProviders.register(VResourceLocation.mod("lord_leveling"), (factionPlayer, tree) -> tree.is(ModSkillTreeTags.DEFAULT) ? (int) (Math.max(0, FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel() - 1) * VampirismConfig.BALANCE.skillPointsPerLordLevel.get()) : 0);

    public static final ISkillPointProvider CONFIG_UNLOCK_ALL = SkillPointProviders.register(VResourceLocation.mod("config_unlock_all"), new ISkillPointProvider() {

        @Override
        public int getSkillPoints(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> skillTree) {
            return 0;
        }

        @Override
        public boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> skillTree) {
            return VampirismConfig.SERVER.unlockAllSkills.get() && factionPlayer.getLevel() == factionPlayer.getMaxLevel();
        }
    });

    public static void init() {
    }

    static void createSkillNodes(BootstrapContext<ISkillNode> context) {
        HunterSkills.Nodes.createSkillNodes(context);
        VampireSkills.Nodes.createSkillNodes(context);
    }

    static void createSkillTrees(BootstrapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
