package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPointProvider;
import de.teamlapen.vampirism.api.entity.player.skills.SkillPointProviders;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;

public class ModSkills {

    public static final ISkillPointProvider LEVELING = SkillPointProviders.register(VResourceLocation.mod("leveling"), factionPlayer -> (int)(Math.max(0, factionPlayer.getLevel() -1) * VampirismConfig.BALANCE.skillPointsPerLevel.get()));

    public static final ISkillPointProvider LORD_LEVELING = SkillPointProviders.register(VResourceLocation.mod("lord_leveling"), factionPlayer -> (int) (Math.max(0, FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel()-1) * VampirismConfig.BALANCE.skillPointsPerLordLevel.get()));

    public static final ISkillPointProvider CONFIG_UNLOCK_ALL = SkillPointProviders.register(VResourceLocation.mod("config_unlock_all"), new ISkillPointProvider() {

        @Override
        public int getSkillPoints(IFactionPlayer<?> factionPlayer) {
            return 0;
        }

        @Override
        public boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer) {
            return VampirismConfig.SERVER.unlockAllSkills.get() && factionPlayer.getLevel() == factionPlayer.getMaxLevel();
        }
    });

    public static void init() {}

    static void createSkillNodes(BootstrapContext<ISkillNode> context) {
        HunterSkills.Nodes.createSkillNodes(context);
        VampireSkills.Nodes.createSkillNodes(context);
    }

    static void createSkillTrees(BootstrapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
