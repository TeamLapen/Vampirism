package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Registers the default hunter skills
 */
@ObjectHolder(REFERENCE.MODID)
public class HunterSkills {

    public static final ISkill double_crossbow = UtilLib.getNull();
    public static final ISkill weapon_table = UtilLib.getNull();
    public static final ISkill enhanced_crossbow = UtilLib.getNull();
    public static final ISkill enhanced_armor = UtilLib.getNull();
    public static final ISkill enhanced_weapons = UtilLib.getNull();
    public static final ISkill tech_weapons = UtilLib.getNull();
    public static final ISkill stake1 = UtilLib.getNull();
    public static final ISkill stake2 = UtilLib.getNull();
    public static final ISkill blood_potion_table = UtilLib.getNull();
    public static final ISkill blood_potion_less_bad = UtilLib.getNull();
    public static final ISkill blood_potion_good_or_bad = UtilLib.getNull();
    public static final ISkill blood_potion_identify_some = UtilLib.getNull();
    public static final ISkill blood_potion_category_hint = UtilLib.getNull();
    public static final ISkill blood_potion_less_bad_2 = UtilLib.getNull();
    public static final ISkill blood_potion_faster_crafting = UtilLib.getNull();
    public static final ISkill blood_potion_portable_crafting = UtilLib.getNull();
    public static final ISkill blood_potion_duration = UtilLib.getNull();
    public static final ISkill basic_alchemy = UtilLib.getNull();
    public static final ISkill garlic_beacon = UtilLib.getNull();
    public static final ISkill holy_water_enhanced = UtilLib.getNull();
    public static final ISkill purified_garlic = UtilLib.getNull();
    public static final ISkill garlic_beacon_improved = UtilLib.getNull();
    public static final ISkill hunter_attack_speed = UtilLib.getNull();
    public static final ISkill hunter_advanced_attack_speed = UtilLib.getNull();
    public static final ISkill hunter_disguise = UtilLib.getNull();
    public static final ISkill hunter_awareness = UtilLib.getNull();

    public static void registerHunterSkills(IForgeRegistry<ISkill> registry) {
        registry.register(new VampirismSkill.SimpleHunterSkill(VReference.HUNTER_FACTION.getID(), false));
        registry.register(new VampirismSkill.SimpleHunterSkill("double_crossbow", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("weapon_table", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_crossbow", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_armor", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_weapons", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("tech_weapons", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake1", false) {
            @Override
            public ITextComponent getDescription() {
                ITextComponent desc = new TranslationTextComponent("skill.vampirism.stake1.desc", (int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100));
                if (Balance.hps.INSTANT_KILL_SKILL_1_FROM_BEHIND) {
                    desc.appendText(" " + new TranslationTextComponent("text.vampirism.from_behind"));
                }
                return desc;
            }
        });

        registry.register(new VampirismSkill.SimpleHunterSkill("stake2", false) {
            @Override
            public ITextComponent getDescription() {
                StringTextComponent desc = null;
                if (Balance.hps.INSTANT_KILL_SKILL_2_ONLY_NPC) {
                    new TranslationTextComponent("skill.vampirism.stake2.desc_npc", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);
                } else {
                    new TranslationTextComponent("skill.vampirism.stake2.desc_all", (int) Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);

                }
                return desc;
            }
        });
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_table", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_good_or_bad", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_identify_some", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_category_hint", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad_2", true) {
            @Override
            public ITextComponent getDescription() {
                return new TranslationTextComponent("skill.vampirism.blood_potion_less_bad.desc");
            }

            @Override
            public String getTranslationKey() {
                return "skill.vampirism.blood_potion_less_bad";
            }
        });
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_faster_crafting", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_portable_crafting", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_duration", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("basic_alchemy", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("holy_water_enhanced", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("purified_garlic", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon_improved", true));
        DefaultSkill<IHunterPlayer> attackSpeed = new VampirismSkill.SimpleHunterSkill("hunter_attack_speed", false);
        attackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", Balance.hps.SMALL_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_TOTAL);

        registry.register(attackSpeed);
        registry.register(new ActionSkill<IHunterPlayer>("hunter_disguise", HunterActions.disguise_hunter, true));
        registry.register(new ActionSkill<IHunterPlayer>(new ResourceLocation("vampirism", "hunter_awareness"), HunterActions.awareness_hunter));

        DefaultSkill<IHunterPlayer> advancedAttackSpeed = new VampirismSkill.SimpleHunterSkill("hunter_advanced_attack_speed", false);
        advancedAttackSpeed.registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", Balance.hps.MAJOR_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_TOTAL);

        registry.register(advancedAttackSpeed);

    }

    public static void buildSkillTree(SkillNode root) {
        ISkillManager skillManager = VampirismAPI.skillManager();
        SkillNode skill2 = skillManager.createSkillNode(root, stake1);


        SkillNode skill3 = skillManager.createSkillNode(skill2, hunter_attack_speed);
        SkillNode skill4 = skillManager.createSkillNode(skill3, hunter_disguise);
        registerAlchemy(skillManager, skill4);
        registerBloodAlchemy(skillManager, skill4);
        registerWeaponSkills(skillManager, skill4);
    }


    private static void registerWeaponSkills(ISkillManager skillManager, SkillNode root) {
        SkillNode skill5 = skillManager.createSkillNode(root, weapon_table);


        SkillNode skill6 = skillManager.createSkillNode(skill5, hunter_advanced_attack_speed, double_crossbow);
        SkillNode skill7 = skillManager.createSkillNode(skill6, enhanced_weapons, enhanced_crossbow);
        SkillNode skill8 = skillManager.createSkillNode(skill7, enhanced_armor);
        SkillNode skill9 = skillManager.createSkillNode(skill8, tech_weapons);
        SkillNode skill10 = skillManager.createSkillNode(skill9, stake2);
    }

    private static void registerAlchemy(ISkillManager skillManager, SkillNode root) {
        SkillNode skill5 = skillManager.createSkillNode(root, basic_alchemy);
        SkillNode skill6 = skillManager.createSkillNode(skill5, garlic_beacon);
        SkillNode skill7 = skillManager.createSkillNode(skill6, purified_garlic, holy_water_enhanced);
        SkillNode skill8 = skillManager.createSkillNode(skill7, garlic_beacon_improved);
        SkillNode skill9 = skillManager.createSkillNode(skill8, hunter_awareness);
    }

    private static void registerBloodAlchemy(ISkillManager skillManager, SkillNode root) {
        SkillNode skill5 = skillManager.createSkillNode(root, blood_potion_table);
        SkillNode skill6 = skillManager.createSkillNode(skill5, blood_potion_less_bad, blood_potion_good_or_bad);
        SkillNode skill7 = skillManager.createSkillNode(skill6, blood_potion_faster_crafting, blood_potion_category_hint);
        SkillNode skill8 = skillManager.createSkillNode(skill7, blood_potion_duration);
        SkillNode skill9 = skillManager.createSkillNode(skill8, blood_potion_portable_crafting);
        SkillNode skill10 = skillManager.createSkillNode(skill9, blood_potion_less_bad_2, blood_potion_identify_some);
    }
}
