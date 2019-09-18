package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers the default hunter skills
 */
@ObjectHolder(REFERENCE.MODID)
@SuppressWarnings("unused")
public class HunterSkills {

    public static final ISkill basic_alchemy = getNull();
    public static final ISkill blood_potion_category_hint = getNull();
    public static final ISkill blood_potion_duration = getNull();
    public static final ISkill blood_potion_faster_crafting = getNull();
    public static final ISkill blood_potion_good_or_bad = getNull();
    public static final ISkill blood_potion_identify_some = getNull();
    public static final ISkill blood_potion_less_bad = getNull();
    public static final ISkill blood_potion_less_bad_2 = getNull();
    public static final ISkill blood_potion_portable_crafting = getNull();
    public static final ISkill blood_potion_table = getNull();
    public static final ISkill double_crossbow = getNull();
    public static final ISkill enhanced_armor = getNull();
    public static final ISkill enhanced_crossbow = getNull();
    public static final ISkill enhanced_weapons = getNull();
    public static final ISkill garlic_beacon = getNull();
    public static final ISkill garlic_beacon_improved = getNull();
    public static final ISkill holy_water_enhanced = getNull();
    public static final ISkill hunter_attack_speed = getNull();
    public static final ISkill hunter_attack_speed_advanced = getNull();
    public static final ISkill hunter_awareness = getNull();
    public static final ISkill hunter_disguise = getNull();
    public static final ISkill purified_garlic = getNull();
    public static final ISkill stake1 = getNull();
    public static final ISkill stake2 = getNull();
    public static final ISkill tech_weapons = getNull();
    public static final ISkill weapon_table = getNull();

    @SuppressWarnings("deprecation")
    public static void registerHunterSkills(IForgeRegistry<ISkill> registry) {
        registry.register(new VampirismSkill.SimpleHunterSkill(VReference.HUNTER_FACTION.getID(), false));
        registry.register(new VampirismSkill.SimpleHunterSkill("basic_alchemy", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_category_hint", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_duration", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_faster_crafting", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_good_or_bad", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_identify_some", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_less_bad_2", true).setDescription(() -> new TranslationTextComponent("skill.vampirism.blood_potion_less_bad.desc")).setTranslationKey("skill.vampirism.blood_potion_less_bad"));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_portable_crafting", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("blood_potion_table", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("double_crossbow", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_armor", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_crossbow", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_weapons", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon_improved", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("holy_water_enhanced", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed", false).registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", Balance.hps.SMALL_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed_advanced", false).registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", Balance.hps.MAJOR_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new ActionSkill<IHunterPlayer>("hunter_awareness", HunterActions.awareness_hunter));
        registry.register(new ActionSkill<IHunterPlayer>("hunter_disguise", HunterActions.disguise_hunter, true));
        registry.register(new VampirismSkill.SimpleHunterSkill("purified_garlic", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake1", false)
                .setDescription(() -> {
                    ITextComponent desc = new TranslationTextComponent("skill.vampirism.stake1.desc", (int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100));
                    if (Balance.hps.INSTANT_KILL_SKILL_1_FROM_BEHIND) {
                        desc.appendText(" " + new TranslationTextComponent("text.vampirism.from_behind"));
                    }
                    return desc;
                }));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake2", false)
                .setDescription(() -> {
                    StringTextComponent desc = null;
                    if (Balance.hps.INSTANT_KILL_SKILL_2_ONLY_NPC) {
                        new TranslationTextComponent("skill.vampirism.stake2.desc_npc", Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);
                    } else {
                        new TranslationTextComponent("skill.vampirism.stake2.desc_all", Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH);

                    }
                    return desc;
                }));
        registry.register(new VampirismSkill.SimpleHunterSkill("tech_weapons", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("weapon_table", true));
    }
}
