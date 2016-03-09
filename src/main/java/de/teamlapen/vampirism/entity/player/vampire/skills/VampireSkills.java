package de.teamlapen.vampirism.entity.player.vampire.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;

import java.util.Collection;

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
            public String getUnlocalizedName() {
                return "text.vampirism.skill.night_vision";
            }
        });
        SkillNode skill3 = new SkillNode(skill2, new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "regen";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.regenAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection<IAction<IVampirePlayer>> list) {
                list.add(VampireActions.regenAction);
            }
        });
        SkillNode skill4 = new SkillNode(skill3, new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "bat";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.batAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection<IAction<IVampirePlayer>> list) {
                list.add(VampireActions.batAction);
            }
        });
        registerOffensiveSkills(skill4);
        registerUtilSkills(skill4);
    }

    private static void registerUtilSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new DefaultSkill() {
            @Override
            public String getID() {
                return "2summonbats";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.summonBatAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection list) {
                list.add(VampireActions.summonBatAction);
            }
        });
        DefaultSkill<IVampirePlayer> damage = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "2lesssundamage";
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
            public String getUnlocalizedName() {
                return "text.vampirism.skill.less_bloodthirst";
            }
        }).registerAttributeModifier(VReference.bloodExhaustion, "980ad86f-fe76-433b-b26a-c4060e0e6751", 0.6, 2));
        SkillNode skill4 = new SkillNode(skill3, new DefaultSkill() {
            @Override
            public String getID() {
                return "2disguise";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.disguiseAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection list) {
                list.add(VampireActions.disguiseAction);
            }
        });
        //TODO add one more
        SkillNode skill6 = new SkillNode(skill4, new DefaultSkill() {
            @Override
            public String getID() {
                return "2invisibility";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.invisibilityAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection list) {
                list.add(VampireActions.invisibilityAction);
            }
        });
    }

    private static void registerOffensiveSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3rage";
            }

            @Override
            public String getUnlocalizedName() {
                return VampireActions.rageAction.getUnlocalizedName();
            }

            @Override
            protected void getActions(Collection<IAction<IVampirePlayer>> list) {
                list.add(VampireActions.rageAction);
            }
        });
        DefaultSkill<IVampirePlayer> bite = new DefaultSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3bite1";
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
