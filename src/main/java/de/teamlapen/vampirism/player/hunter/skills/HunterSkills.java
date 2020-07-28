package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
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
    public static final ISkill durable_brewing = getNull();
    public static final ISkill concentrated_brewing = getNull();
    public static final ISkill multitask_brewing = getNull();
    public static final ISkill efficient_brewing = getNull();
    public static final ISkill master_brewer = getNull();
    public static final ISkill swift_brewing = getNull();
    public static final ISkill concentrated_durable_brewing = getNull();
    public static final ISkill potion_resistance = getNull();

    @SuppressWarnings("deprecation")
    public static void registerHunterSkills(IForgeRegistry<ISkill> registry) {
        registry.register(new VampirismSkill.SimpleHunterSkill(VReference.HUNTER_FACTION.getID(), false));
        registry.register(new VampirismSkill.SimpleHunterSkill("basic_alchemy", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("double_crossbow", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_armor", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_crossbow", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_weapons", false));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_beacon_improved", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("holy_water_enhanced", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed", false).registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed_advanced", true).registerAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", VampirismConfig.BALANCE.hsMajorAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new ActionSkill<IHunterPlayer>("hunter_awareness", HunterActions.awareness_hunter, true));
        registry.register(new ActionSkill<IHunterPlayer>("hunter_disguise", HunterActions.disguise_hunter, true));
        registry.register(new VampirismSkill.SimpleHunterSkill("purified_garlic", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake1", false)
                .setDescription(() -> {
                    ITextComponent desc = new TranslationTextComponent("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                    if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                        desc.appendText(" " + new TranslationTextComponent("text.vampirism.from_behind"));
                    }
                    return desc;
                }));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake2", false)
                .setDescription(() -> {
                    ITextComponent desc = null;
                    if (VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get()) {
                        desc = new TranslationTextComponent("skill.vampirism.stake2.desc_npc", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());
                    } else {
                        desc = new TranslationTextComponent("skill.vampirism.stake2.desc_all", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());

                    }
                    return desc;
                }));
        registry.register(new VampirismSkill.SimpleHunterSkill("tech_weapons", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("weapon_table", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("durable_brewing", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("concentrated_brewing", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("multitask_brewing", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("efficient_brewing", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("master_brewer", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("swift_brewing", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("concentrated_durable_brewing", true));
        registry.register(new ActionSkill<IHunterPlayer>("potion_resistance", HunterActions.potion_resistance_hunter, true));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if (missingMapping.key.toString().startsWith("vampirism:blood_potion_")) {
                missingMapping.ignore();
            }
        });
    }
}
