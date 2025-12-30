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

    protected void buildHunter(HolderLookup.Provider provider, SkillTreeOutput output) {
        tree(HunterSkills.Trees.LEVEL, HunterSkills.Nodes.LEVEL_ROOT)
                .addNode(node(HunterSkills.Nodes.SKILL2)
                        .addNode(node(HunterSkills.Nodes.SKILL3)
                                .addNode(node(HunterSkills.Nodes.SKILL4)
                                        .addNode(node(HunterSkills.Nodes.ALCHEMY1)
                                                .addNode(node(HunterSkills.Nodes.ALCHEMY2)
                                                        .addNode(node(HunterSkills.Nodes.ALCHEMY3)
                                                                .addNode(node(HunterSkills.Nodes.ALCHEMY4)
                                                                        .addNode(node(HunterSkills.Nodes.ALCHEMY5)
                                                                                .addNode(node(HunterSkills.Nodes.ALCHEMY6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .addNode(node(HunterSkills.Nodes.POTION1)
                                                .addNode(node(HunterSkills.Nodes.POTION2)
                                                        .addNode(node(HunterSkills.Nodes.POTION3)
                                                                .addNode(node(HunterSkills.Nodes.POTION4)
                                                                        .addNode(node(HunterSkills.Nodes.POTION5)
                                                                                .addNode(node(HunterSkills.Nodes.POTION6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .addNode(node(HunterSkills.Nodes.WEAPON1)
                                                .addNode(node(HunterSkills.Nodes.WEAPON2)
                                                        .addNode(node(HunterSkills.Nodes.WEAPON3)
                                                                .addNode(node(HunterSkills.Nodes.WEAPON4)
                                                                        .addNode(node(HunterSkills.Nodes.WEAPON5)
                                                                                .addNode(node(HunterSkills.Nodes.WEAPON6)
                                                                                        .addNode(node(HunterSkills.Nodes.WEAPON7))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ).build(output, modId("hunter_level"));

        tree(HunterSkills.Trees.LORD, HunterSkills.Nodes.LORD_ROOT)
                .addNode(node(HunterSkills.Nodes.LORD_2)
                        .addNode(node(HunterSkills.Nodes.LORD_6))
                )
                .addNode(node(HunterSkills.Nodes.LORD_3))
                .addNode(node(HunterSkills.Nodes.LORD_4))
                .addNode(node(HunterSkills.Nodes.LORD_5))
                .addAfter(HunterSkills.Trees.LEVEL)
                .build(output, modId("hunter_lord"));
    }

    protected void buildVampire(HolderLookup.Provider provider, SkillTreeOutput output) {

        tree(VampireSkills.Trees.LEVEL, VampireSkills.Nodes.LEVEL_ROOT)
                .addNode(node(VampireSkills.Nodes.SKILL2)
                        .addNode(node(VampireSkills.Nodes.SKILL3)
                                .addNode(node(VampireSkills.Nodes.SKILL4)
                                        .addNode(node(VampireSkills.Nodes.OFFENSIVE1)
                                                .addNode(node(VampireSkills.Nodes.OFFENSIVE2)
                                                        .addNode(node(VampireSkills.Nodes.OFFENSIVE3)
                                                                .addNode(node(VampireSkills.Nodes.OFFENSIVE4)
                                                                        .addNode(node(VampireSkills.Nodes.OFFENSIVE5)
                                                                                .addNode(node(VampireSkills.Nodes.OFFENSIVE6)))
                                                                )
                                                        )
                                                )
                                        )
                                        .addNode(node(VampireSkills.Nodes.DEFENSIVE1)
                                                .addNode(node(VampireSkills.Nodes.DEFENSIVE2)
                                                        .addNode(node(VampireSkills.Nodes.DEFENSIVE3)
                                                                .addNode(node(VampireSkills.Nodes.DEFENSIVE4))
                                                                .addNode(node(VampireSkills.Nodes.DEFENSIVE5)
                                                                        .addNode(node(VampireSkills.Nodes.DEFENSIVE6)
                                                                                .addNode(node(VampireSkills.Nodes.DEFENSIVE7))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .addNode(node(VampireSkills.Nodes.UTIL1)
                                                .addNode(node(VampireSkills.Nodes.UTIL2)
                                                        .addNode(node(VampireSkills.Nodes.UTIL3)
                                                                .addNode(node(VampireSkills.Nodes.UTIL4)
                                                                        .addNode(node(VampireSkills.Nodes.UTIL5)
                                                                                .addNode(node(VampireSkills.Nodes.UTIL6))
                                                                        )
                                                                )
                                                        )
                                                )
                                                .addNode(node(VampireSkills.Nodes.UTIL15))
                                        )
                                )
                        )
                )
                .build(output, modId("vampire_level"));

        tree(VampireSkills.Trees.LORD, VampireSkills.Nodes.LORD_ROOT)
                .addNode(node(VampireSkills.Nodes.LORD_SKILL2))
                .addNode(node(VampireSkills.Nodes.LORD_SKILL3))
                .addNode(node(VampireSkills.Nodes.LORD_SKILL4))
                .addNode(node(VampireSkills.Nodes.LORD_SKILL5))
                .addAfter(VampireSkills.Trees.LEVEL)
                .build(output, modId("vampire_lord"));

        tree(VampireSkills.Trees.DRACULA, VampireSkills.Nodes.DRACULA_ROOT)
                .addNode(node(VampireSkills.Nodes.DRACULA_1))
                .addNode(node(VampireSkills.Nodes.DRACULA_2))
                .addAfter(VampireSkills.Trees.LORD)
                .build(output, modId("vampire_dracula"));
    }

    @Override
    protected void buildSkillTrees(HolderLookup.Provider provider, SkillTreeOutput output) {
        buildHunter(provider, output);
        buildVampire(provider, output);
    }
}
