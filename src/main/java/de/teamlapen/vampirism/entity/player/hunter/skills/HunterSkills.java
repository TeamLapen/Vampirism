package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;

/**
 * Registers the default hunter skills
 */
public class HunterSkills {
    public static final ISkill<IHunterPlayer> doubleCrossbow = new DefaultSkill<IHunterPlayer>() {
        @Override
        public String getID() {
            return "doubleCrossbow";
        }

        @Override
        public int getMinU() {
            return 0;
        }

        @Override
        public int getMinV() {
            return 0;
        }

        @Override
        public String getUnlocalizedName() {
            return "text.vampirism.skill.double_crossbow";
        }
    };

    public static void registerHunterSkills() {
        ISkillRegistry registry = VampirismAPI.skillRegistry();
        SkillNode root = registry.setRootSkill(VReference.HUNTER_FACTION, new DefaultSkill<IHunterPlayer>() {
            @Override
            public String getID() {
                return "root_hunter";
            }

            @Override
            public int getMinU() {
                return 0;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
                return "text.vampirism.hunter";
            }
        });
        SkillNode skill2 = new SkillNode(root, doubleCrossbow);
        //Placeholder

    }
}
