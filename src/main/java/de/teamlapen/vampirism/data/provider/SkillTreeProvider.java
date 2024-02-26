package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkillTreeProvider extends de.teamlapen.vampirism.data.provider.parent.SkillTreeProvider {

    public SkillTreeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void buildSkillTrees(HolderLookup.Provider provider, @NotNull SkillTreeOutput output) {
        HolderLookup.RegistryLookup<ISkillTree> trees = provider.lookupOrThrow(VampirismRegistries.Keys.SKILL_TREE);
        HolderLookup.RegistryLookup<ISkillNode> nodes = provider.lookupOrThrow(VampirismRegistries.Keys.SKILL_NODE);
        output.accept(modId("hunter_level"), new SkillTreeConfiguration(trees.getOrThrow(HunterSkills.Trees.LEVEL), nodes.getOrThrow(HunterSkills.Nodes.LEVEL_ROOT),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.SKILL2),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.SKILL3),
                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.SKILL4),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY4),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY5),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.ALCHEMY6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION4),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION5),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.POTION6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON4),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON5),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.WEAPON6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ));
        output.accept(modId("hunter_lord"), new SkillTreeConfiguration(trees.getOrThrow(HunterSkills.Trees.LORD), nodes.getOrThrow(HunterSkills.Nodes.LORD_ROOT),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.LORD_2),
                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.LORD_6))),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.LORD_3)),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.LORD_4)),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(HunterSkills.Nodes.LORD_5))
                )
        );

        output.accept(modId("vampire_level"), new SkillTreeConfiguration(trees.getOrThrow(VampireSkills.Trees.LEVEL), nodes.getOrThrow(VampireSkills.Nodes.LEVEL_ROOT),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.SKILL2),
                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.SKILL3),
                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.SKILL4),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE4),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE5),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.OFFENSIVE6))
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE4)),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE5),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE6),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.DEFENSIVE7))
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL1),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL2),
                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL3),
                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL4),
                                                                        new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL5),
                                                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL6))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.UTIL15))
                                        )
                                )
                        )
                )
        ));
        output.accept(modId("vampire_lord"), new SkillTreeConfiguration(trees.getOrThrow(VampireSkills.Trees.LORD), nodes.getOrThrow(VampireSkills.Nodes.LORD_ROOT),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.LORD_SKILL2)),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.LORD_SKILL3)),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.LORD_SKILL4)),
                new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(VampireSkills.Nodes.LORD_SKILL5))
        ));

    }
}
