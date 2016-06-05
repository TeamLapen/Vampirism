package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Registers the default hunter skills
 */
public class HunterSkills {
    public static final ISkill<IHunterPlayer> doubleCrossbow = new WeaponSkill("double_crossbow", 0, 0, false);
    public static final ISkill<IHunterPlayer> weaponTable = new WeaponSkill("weapon_table", 0, 0, true);
    public static final ISkill<IHunterPlayer> enhancedCrossbow = new WeaponSkill("enhanced_crossbow", 0, 0, false);

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

        //Placeholder

        DefaultSkill<IHunterPlayer> attackSpeed = new DefaultSkill<IHunterPlayer>() {
            @Override
            public String getID() {
                return "attack_speed";
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
                return "text.vampirism.skill.attack_speed";
            }

        };
        attackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", Balance.hps.SMALL_ATTACK_SPEED_MODIFIER, 2);

        SkillNode skill2 = new SkillNode(root, attackSpeed);
        registerAlchemySkills(skill2);
        registerWeaponSkills(skill2);
    }

    private static void registerWeaponSkills(SkillNode root) {
        SkillNode skill5 = new SkillNode(root, weaponTable);
        DefaultSkill<IHunterPlayer> advancedAttackSpeed = new DefaultSkill<IHunterPlayer>() {
            @Override
            public String getID() {
                return "advanced_attack_speed";
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
                return "text.vampirism.skill.advanced_attack_speed";
            }

        };
        advancedAttackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", Balance.hps.MAJOR_ATTACK_SPPED_MODIFIER, 2);

        SkillNode skill6 = new SkillNode(skill5, doubleCrossbow);
        SkillNode skill7 = new SkillNode(skill6, advancedAttackSpeed, enhancedCrossbow);
    }

    private static void registerAlchemySkills(SkillNode root) {
        ISkill<IHunterPlayer> s = new DefaultSkill<IHunterPlayer>() {
            @Override
            public String getID() {
                return "1nothing";
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
                return "1nothing";
            }
        };
        SkillNode skill5 = new SkillNode(root, s);
    }
}
