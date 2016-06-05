package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Registers the default hunter skills
 */
public class HunterSkills {
    public static final ISkill<IHunterPlayer> doubleCrossbow = new VampirismSkill.SimpleHunterSkill("double_crossbow", 0, 0, false);
    public static final ISkill<IHunterPlayer> weaponTable = new VampirismSkill.SimpleHunterSkill("weapon_table", 0, 0, true);
    public static final ISkill<IHunterPlayer> enhancedCrossbow = new VampirismSkill.SimpleHunterSkill("enhanced_crossbow", 0, 0, false);
    public static final ISkill<IHunterPlayer> stake1 = new VampirismSkill.SimpleHunterSkill("stake1", 0, 0, false) {
        @Override
        public String getLocalizedDescription() {
            String desc = UtilLib.translateToLocalFormatted("text.vampirism.skill.stake1.desc", (int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100));
            if (Balance.hps.INSTANT_KILL_SKILL_1_FROM_BEHIND) {
                desc += " " + UtilLib.translateToLocal("text.vampirism.from_behind");
            }
            return desc;
        }
    };
    public static final ISkill<IHunterPlayer> stake2 = new VampirismSkill.SimpleHunterSkill("stake2", 0, 0, false) {
        @Override
        public String getLocalizedDescription() {
            String desc = null;
            if (Balance.hps.INSTANT_KILL_SKILL_2_ONLY_NPC) {
                desc = UtilLib.translateToLocalFormatted("text.vampirism.skill.stake2.desc_npc", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);
            } else {
                desc = UtilLib.translateToLocalFormatted("text.vampirism.skill.stake2.desc_all", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);

            }
            return desc;
        }
    };

    public static void registerHunterSkills() {
        ISkillRegistry registry = VampirismAPI.skillRegistry();
        SkillNode root = registry.setRootSkill(VReference.HUNTER_FACTION, new VampirismSkill.SimpleHunterSkill("root_hunter", 0, 0, false));


        SkillNode skill2 = new SkillNode(root, stake1);
        DefaultSkill<IHunterPlayer> attackSpeed = new VampirismSkill.SimpleHunterSkill("attack_speed", 0, 0, false);
        attackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", Balance.hps.SMALL_ATTACK_SPEED_MODIFIER, 2);

        SkillNode skill3 = new SkillNode(skill2, attackSpeed);
        registerAlchemySkills(skill3);
        registerWeaponSkills(skill3);
    }

    private static void registerWeaponSkills(SkillNode root) {
        SkillNode skill5 = new SkillNode(root, weaponTable);
        DefaultSkill<IHunterPlayer> advancedAttackSpeed = new VampirismSkill.SimpleHunterSkill("advanced_attack_speed", 0, 0, false);
        advancedAttackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", Balance.hps.MAJOR_ATTACK_SPEED_MODIFIER, 2);

        SkillNode skill6 = new SkillNode(skill5, doubleCrossbow);
        SkillNode skill7 = new SkillNode(skill6, advancedAttackSpeed, enhancedCrossbow);
        SkillNode skill8 = new SkillNode(skill7, stake2);
    }

    private static void registerAlchemySkills(SkillNode root) {
        ISkill<IHunterPlayer> s = new VampirismSkill.SimpleHunterSkill("1nothing", 0, 0, false);
        SkillNode skill5 = new SkillNode(root, s);
    }
}
