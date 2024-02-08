package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPointProvider;
import de.teamlapen.vampirism.api.entity.player.skills.SkillPointProviders;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;

public class ModSkills {

    public static final ISkillPointProvider LEVELING = SkillPointProviders.register(new ResourceLocation(REFERENCE.MODID, "leveling"), factionPlayer -> (int)(factionPlayer.getLevel() * VampirismConfig.BALANCE.skillPointsPerLevel.get()));

    public static final ISkillPointProvider LORD_LEVELING = SkillPointProviders.register(new ResourceLocation(REFERENCE.MODID, "lord_leveling"), factionPlayer -> (int) (FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel() * VampirismConfig.BALANCE.skillPointsPerLordLevel.get()));

    public static final ISkillPointProvider CONFIG_UNLOCK_ALL = SkillPointProviders.register(new ResourceLocation(REFERENCE.MODID, "config_unlock_all"), new ISkillPointProvider() {

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

    public static void createSkillNodes(BootstapContext<ISkillNode> context) {
        HunterSkills.Nodes.createSkillNodes(context);
        VampireSkills.Nodes.createSkillNodes(context);
    }

    public static void createSkillTrees(BootstapContext<ISkillTree> context) {
        HunterSkills.Trees.createSkillTrees(context);
        VampireSkills.Trees.createSkillTrees(context);
    }
}
