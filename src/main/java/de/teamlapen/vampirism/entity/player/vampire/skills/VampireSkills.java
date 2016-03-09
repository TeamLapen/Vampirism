package de.teamlapen.vampirism.entity.player.vampire.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;

/**
 * Registers the default vampire skills
 */
public class VampireSkills {

    public static void registerVampireSkills() {
        ISkillRegistry registry = VampirismAPI.skillRegistry();
        SkillNode root = registry.setRootSkill(VReference.VAMPIRE_FACTION, new DefaultSkill() {
            @Override
            public String getID() {
                return "root_vampire";
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
                return "text.vampirism.vampire";
            }
        });

        SkillNode skill2 = new SkillNode(root, new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "second";
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
                return "text.vampirism.skill.night_vision";
            }
        });
        SkillNode skill3 = new SkillNode(skill2, new ActionSkill(VampireActions.regenAction, "regen"));
        SkillNode skill4 = new SkillNode(skill3, new ActionSkill(VampireActions.batAction, "bat"));
        registerOffensiveSkills(skill4);
        registerUtilSkills(skill4);
    }

    private static void registerUtilSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new ActionSkill(VampireActions.summonBatAction, "2summonbats"));
        DefaultSkill<IVampirePlayer> damage = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "2lesssundamage";
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
                return "text.vampirism.skill.less_sundamage";
            }
        };
        damage.registerAttributeModifier(VReference.sunDamage, "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", 0.7, 2);
        DefaultSkill<IVampirePlayer> damage2 = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "2lessgarlicdamage";
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
                return "text.vampirism.skill.less_garlicdamage";
            }
        };
        damage2.registerAttributeModifier(VReference.garlicDamage, "155DF42A-9CA4-43BC-9F80-F0716CA43DA9", 0.5, 2);
        SkillNode skill2 = new SkillNode(skill1, damage, damage2);

        SkillNode skill3 = new SkillNode(skill2, (new DefaultSkill() {
            @Override
            public String getID() {
                return "2lessbloodthirst";
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
                return "text.vampirism.skill.less_bloodthirst";
            }
        }).registerAttributeModifier(VReference.bloodExhaustion, "980ad86f-fe76-433b-b26a-c4060e0e6751", 0.6, 2));
        SkillNode skill4 = new SkillNode(skill3, new ActionSkill(VampireActions.disguiseAction, "2disguise"));
        //TODO add one more
        SkillNode skill6 = new SkillNode(skill4, new ActionSkill(VampireActions.invisibilityAction, "2invisibility"));
    }

    private static void registerOffensiveSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new ActionSkill(VampireActions.rageAction, "3rage"));
        DefaultSkill<IVampirePlayer> bite = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3bite1";
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
                return "text.vampirism.skill.more_bite_damage";
            }
        };
        bite.registerAttributeModifier(VReference.biteDamage, "A08CAB62-EE88-4DB9-8F62-E9EF108A4E87", 2, 1);
        DefaultSkill<IVampirePlayer> bite2 = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3bite2";
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
                return "text.vampirism.skill.poisonous_bite";
            }

            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().poisonous_bite = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().poisonous_bite = true;
            }
        };
        SkillNode skill2 = new SkillNode(skill1, bite, bite2);
        //TODO add lighting or so


    }
}
