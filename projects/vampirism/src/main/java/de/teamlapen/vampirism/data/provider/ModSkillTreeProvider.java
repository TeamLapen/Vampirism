package de.teamlapen.vampirism.data.provider;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.common.factions.skills.SkillTreeConfiguration;
import de.teamlapen.faction.data.provider.base.SkillTreeProvider;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModSkillTreeProvider extends SkillTreeProvider {

    public ModSkillTreeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void buildSkillTrees(HolderLookup.Provider provider, SkillTreeOutput output) {
        HolderLookup.RegistryLookup<ISkillTree> trees = provider.lookupOrThrow(FactionRegistries.Keys.SKILL_TREE);
        HolderLookup.RegistryLookup<ISkillNode> nodes = provider.lookupOrThrow(FactionRegistries.Keys.SKILL_NODE);

        output.accept(modId("hunter_level"), new SkillTreeConfiguration(
                trees.getOrThrow(HunterSkills.Trees.LEVEL),
                nodes.getOrThrow(HunterSkills.Nodes.LEVEL_ROOT),
                node(nodes, HunterSkills.Nodes.SKILL2,
                        node(nodes, HunterSkills.Nodes.SKILL3,
                                node(nodes, HunterSkills.Nodes.SKILL4,
                                        chain(nodes, HunterSkills.Nodes.ALCHEMY1, HunterSkills.Nodes.ALCHEMY2, HunterSkills.Nodes.ALCHEMY3, HunterSkills.Nodes.ALCHEMY4, HunterSkills.Nodes.ALCHEMY5, HunterSkills.Nodes.ALCHEMY6),
                                        chain(nodes, HunterSkills.Nodes.POTION1, HunterSkills.Nodes.POTION2, HunterSkills.Nodes.POTION3, HunterSkills.Nodes.POTION4, HunterSkills.Nodes.POTION5, HunterSkills.Nodes.POTION6),
                                        node(nodes, HunterSkills.Nodes.WEAPON1,
                                                node(nodes, HunterSkills.Nodes.WEAPON2,
                                                        node(nodes, HunterSkills.Nodes.WEAPON3,
                                                                node(nodes, HunterSkills.Nodes.WEAPON4,
                                                                        node(nodes, HunterSkills.Nodes.WEAPON5,
                                                                                node(nodes, HunterSkills.Nodes.WEAPON6),
                                                                                node(nodes, HunterSkills.Nodes.WEAPON7)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ));

        output.accept(modId("hunter_lord"), new SkillTreeConfiguration(
                trees.getOrThrow(HunterSkills.Trees.LORD),
                nodes.getOrThrow(HunterSkills.Nodes.LORD_ROOT),
                node(nodes, HunterSkills.Nodes.LORD_2, node(nodes, HunterSkills.Nodes.LORD_6)),
                node(nodes, HunterSkills.Nodes.LORD_3),
                node(nodes, HunterSkills.Nodes.LORD_4),
                node(nodes, HunterSkills.Nodes.LORD_5)
        ));

        output.accept(modId("vampire_level"), new SkillTreeConfiguration(
                trees.getOrThrow(VampireSkills.Trees.LEVEL),
                nodes.getOrThrow(VampireSkills.Nodes.LEVEL_ROOT),
                node(nodes, VampireSkills.Nodes.SKILL2,
                        node(nodes, VampireSkills.Nodes.SKILL3,
                                node(nodes, VampireSkills.Nodes.SKILL4,
                                        chain(nodes, VampireSkills.Nodes.OFFENSIVE1, VampireSkills.Nodes.OFFENSIVE2, VampireSkills.Nodes.OFFENSIVE3, VampireSkills.Nodes.OFFENSIVE4, VampireSkills.Nodes.OFFENSIVE5, VampireSkills.Nodes.OFFENSIVE6),
                                        node(nodes, VampireSkills.Nodes.DEFENSIVE1,
                                                node(nodes, VampireSkills.Nodes.DEFENSIVE2,
                                                        node(nodes, VampireSkills.Nodes.DEFENSIVE3,
                                                                node(nodes, VampireSkills.Nodes.DEFENSIVE4),
                                                                node(nodes, VampireSkills.Nodes.DEFENSIVE5,
                                                                        node(nodes, VampireSkills.Nodes.DEFENSIVE6,
                                                                                node(nodes, VampireSkills.Nodes.DEFENSIVE7)
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        node(nodes, VampireSkills.Nodes.UTIL1,
                                                node(nodes, VampireSkills.Nodes.UTIL2,
                                                        node(nodes, VampireSkills.Nodes.UTIL3,
                                                                node(nodes, VampireSkills.Nodes.UTIL4,
                                                                        node(nodes, VampireSkills.Nodes.UTIL5,
                                                                                node(nodes, VampireSkills.Nodes.UTIL6)
                                                                        )
                                                                )
                                                        )
                                                ),
                                                node(nodes, VampireSkills.Nodes.UTIL15)
                                        )
                                )
                        )
                )
        ));

        output.accept(modId("vampire_lord"), new SkillTreeConfiguration(
                trees.getOrThrow(VampireSkills.Trees.LORD),
                nodes.getOrThrow(VampireSkills.Nodes.LORD_ROOT),
                node(nodes, VampireSkills.Nodes.LORD_SKILL2),
                node(nodes, VampireSkills.Nodes.LORD_SKILL3),
                node(nodes, VampireSkills.Nodes.LORD_SKILL4),
                node(nodes, VampireSkills.Nodes.LORD_SKILL5)
        ));
    }
}
