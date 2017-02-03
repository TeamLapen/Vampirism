package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.*;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Registers the default hunter skills
 */
public class HunterSkills {
    public static final ISkill<IHunterPlayer> doubleCrossbow = new VampirismSkill.SimpleHunterSkill("double_crossbow", 192, 32, false);
    public static final ISkill<IHunterPlayer> weaponTable = new VampirismSkill.SimpleHunterSkill("weapon_table", 48, 32, true);
    public static final ISkill<IHunterPlayer> enhancedCrossbow = new VampirismSkill.SimpleHunterSkill("enhanced_crossbow", 208, 32, false);
    public static final ISkill<IHunterPlayer> enhancedArmor = new VampirismSkill.SimpleHunterSkill("enhanced_armor", 0, 48, false);
    public static final ISkill<IHunterPlayer> enhancedWeapons = new VampirismSkill.SimpleHunterSkill("enhanced_weapons", 16, 48, false);
    public static final ISkill<IHunterPlayer> techWeapons = new VampirismSkill.SimpleHunterSkill("tech_weapons", 240, 32, true);
    public static final ISkill<IHunterPlayer> stake1 = new VampirismSkill.SimpleHunterSkill("stake1", 16, 32, false) {
        @Override
        public String getLocalizedDescription() {
            String desc = UtilLib.translateFormatted("text.vampirism.skill.stake1.desc", (int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100));
            if (Balance.hps.INSTANT_KILL_SKILL_1_FROM_BEHIND) {
                desc += " " + UtilLib.translate("text.vampirism.from_behind");
            }
            return desc;
        }
    };
    public static final ISkill<IHunterPlayer> stake2 = new VampirismSkill.SimpleHunterSkill("stake2", 224, 32, false) {
        @Override
        public String getLocalizedDescription() {
            String desc = null;
            if (Balance.hps.INSTANT_KILL_SKILL_2_ONLY_NPC) {
                desc = UtilLib.translateFormatted("text.vampirism.skill.stake2.desc_npc", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);
            } else {
                desc = UtilLib.translateFormatted("text.vampirism.skill.stake2.desc_all", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);

            }
            return desc;
        }
    };
    public static final ISkill<IHunterPlayer> bloodPotionTable = new VampirismSkill.SimpleHunterSkill("blood_potion_table", 64, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_lessBad = new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad", 80, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_goodOrBad = new VampirismSkill.SimpleHunterSkill("blood_potion_good_or_bad", 96, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_identifySome = new VampirismSkill.SimpleHunterSkill("blood_potion_identify_some", 112, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_categoryHint = new VampirismSkill.SimpleHunterSkill("blood_potion_category_hint", 128, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_lessBad2 = new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad_2", 80, 32, true) {
        @Override
        public String getLocalizedDescription() {
            return UtilLib.translate("text.vampirism.skill.blood_potion_less_bad.desc");
        }

        @Override
        public String getUnlocalizedName() {
            return "text.vampirism.skill.blood_potion_less_bad";
        }
    };
    public static final ISkill<IHunterPlayer> bloodPotion_fasterCrafting = new VampirismSkill.SimpleHunterSkill("blood_potion_faster_crafting", 144, 32, false);
    public static final ISkill<IHunterPlayer> bloodPotion_portableCrafting = new VampirismSkill.SimpleHunterSkill("blood_potion_portable_crafting", 176, 32, true);
    public static final ISkill<IHunterPlayer> bloodPotion_increaseDuration = new VampirismSkill.SimpleHunterSkill("blood_potion_duration", 160, 32, true);

    public static final ISkill<IHunterPlayer> basic_alchemy = new VampirismSkill.SimpleHunterSkill("basic_alchemy", 32, 48, true);
    public static final ISkill<IHunterPlayer> garlicBeacon = new VampirismSkill.SimpleHunterSkill("garlic_beacon", 48, 48, true);
    public static final ISkill<IHunterPlayer> holyWater_enhanced = new VampirismSkill.SimpleHunterSkill("holy_water_enhanced", 80, 48, true);
    public static final ISkill<IHunterPlayer> purifiedGarlic = new VampirismSkill.SimpleHunterSkill("purified_garlic", 64, 48, true);
    public static final ISkill<IHunterPlayer> garlicBeacon_improved = new VampirismSkill.SimpleHunterSkill("garlic_beacon_improved", 96, 48, true);

    public static void registerHunterSkills() {
        ISkillRegistry registry = VampirismAPI.skillRegistry();
        SkillNode root = registry.setRootSkill(VReference.HUNTER_FACTION, new VampirismSkill.SimpleHunterSkill("root_hunter", 0, 32, false));


        SkillNode skill2 = new SkillNode(root, stake1);
        DefaultSkill<IHunterPlayer> attackSpeed = new VampirismSkill.SimpleHunterSkill("attack_speed", 32, 32, false);
        attackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", Balance.hps.SMALL_ATTACK_SPEED_MODIFIER, 2);

        SkillNode skill3 = new SkillNode(skill2, attackSpeed);
        SkillNode skill4 = new SkillNode(skill3, new ActionSkill<IHunterPlayer>(HunterActions.disguiseAction, "disguise") {
            @Override
            public String getLocalizedDescription() {
                return UtilLib.translate("text.vampirism.skill.disguiseHunter.desc");
            }
        });
        registerAlchemy(skill4);
        registerBloodAlchemy(skill4);
        registerWeaponSkills(skill4);
    }

    private static void registerWeaponSkills(SkillNode root) {
        SkillNode skill5 = new SkillNode(root, weaponTable);
        DefaultSkill<IHunterPlayer> advancedAttackSpeed = new VampirismSkill.SimpleHunterSkill("advanced_attack_speed", 32, 32, false);
        advancedAttackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", Balance.hps.MAJOR_ATTACK_SPEED_MODIFIER, 2);

        SkillNode skill6 = new SkillNode(skill5, advancedAttackSpeed, doubleCrossbow);
        SkillNode skill7 = new SkillNode(skill6, enhancedWeapons, enhancedCrossbow);
        SkillNode skill8 = new SkillNode(skill7, enhancedArmor);
        SkillNode skill9 = new SkillNode(skill8, techWeapons);
        SkillNode skill10 = new SkillNode(skill9, stake2);
    }

    private static void registerAlchemy(SkillNode root) {
        SkillNode skill5 = new SkillNode(root, basic_alchemy);
        SkillNode skill6 = new SkillNode(skill5, garlicBeacon);
        SkillNode skill7 = new SkillNode(skill6, purifiedGarlic, holyWater_enhanced);
        SkillNode skill8 = new SkillNode(skill7, garlicBeacon_improved);
    }

    private static void registerBloodAlchemy(SkillNode root) {
        SkillNode skill5 = new SkillNode(root, bloodPotionTable);
        SkillNode skill6 = new SkillNode(skill5, bloodPotion_lessBad, bloodPotion_goodOrBad);
        SkillNode skill7 = new SkillNode(skill6, bloodPotion_fasterCrafting, bloodPotion_categoryHint);
        SkillNode skill8 = new SkillNode(skill7, bloodPotion_increaseDuration);
        SkillNode skill9 = new SkillNode(skill8, bloodPotion_portableCrafting);
        SkillNode skill10 = new SkillNode(skill9, bloodPotion_lessBad2, bloodPotion_identifySome);
    }
}
