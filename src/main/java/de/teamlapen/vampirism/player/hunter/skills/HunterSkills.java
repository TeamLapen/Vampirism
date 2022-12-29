package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.MinionRecoverySkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredRegister<ISkill> SKILLS = DeferredRegister.create(ModRegistries.SKILLS, REFERENCE.MODID);

    public static final RegistryObject<ISkill> BASIC_ALCHEMY = SKILLS.register("basic_alchemy", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> DOUBLE_CROSSBOW = SKILLS.register("double_crossbow", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> ENHANCED_ARMOR = SKILLS.register("enhanced_armor", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> ENHANCED_WEAPONS = SKILLS.register("enhanced_weapons", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> GARLIC_BEACON = SKILLS.register("garlic_beacon", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> GARLIC_BEACON_IMPROVED = SKILLS.register("garlic_beacon_improved", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> ENHANCED_BLESSING = SKILLS.register("enhanced_blessing", () -> new VampirismSkill.SimpleHunterSkill(true));
    //Config null, so cannot get method ref
    //noinspection Convert2MethodRef
    public static final RegistryObject<ISkill> HUNTER_ATTACK_SPEED = SKILLS.register("hunter_attack_speed", () -> new VampirismSkill.SimpleHunterSkill(false).registerAttributeModifier(Attributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", () -> VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    //noinspection Convert2MethodRef
    public static final RegistryObject<ISkill> HUNTER_ATTACK_SPEED_ADVANCED = SKILLS.register("hunter_attack_speed_advanced", () -> new VampirismSkill.SimpleHunterSkill(true).registerAttributeModifier(Attributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", () -> VampirismConfig.BALANCE.hsMajorAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    //noinspection Convert2MethodRef
    public static final RegistryObject<ISkill> HUNTER_ATTACK_DAMAGE = SKILLS.register("hunter_attack_damage", () -> new VampirismSkill.SimpleHunterSkill(false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "ffafd115-96e2-4d08-9588-d1bc9be0d902", () -> VampirismConfig.BALANCE.hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<ISkill> HUNTER_AWARENESS = SKILLS.register("hunter_awareness", () -> new ActionSkill<IHunterPlayer>(HunterActions.AWARENESS_HUNTER.get(), true));
    public static final RegistryObject<ISkill> HUNTER_DISGUISE = SKILLS.register("hunter_disguise", () -> new ActionSkill<IHunterPlayer>(HunterActions.DISGUISE_HUNTER.get(), true));
    public static final RegistryObject<ISkill> PURIFIED_GARLIC = SKILLS.register("purified_garlic", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> STAKE1 = SKILLS.register("stake1", () -> new VampirismSkill.SimpleHunterSkill(false)
                    .setDescription(() -> {
                        TextComponent desc = new TranslationTextComponent("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                        if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                            desc.append(new StringTextComponent(" "));
                            desc.append(new TranslationTextComponent("text.vampirism.from_behind"));
                        }
                        return desc;
                    }));
    public static final RegistryObject<ISkill> STAKE2 = SKILLS.register("stake2", () -> new VampirismSkill.SimpleHunterSkill(false)
                    .setDescription(() -> {
                        ITextComponent desc = null;
                        if (VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get()) {
                            desc = new TranslationTextComponent("skill.vampirism.stake2.desc_npc", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());
                        } else {
                            desc = new TranslationTextComponent("skill.vampirism.stake2.desc_all", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());

                        }
                        return desc;
                    }));
    public static final RegistryObject<ISkill> TECH_WEAPONS = SKILLS.register("tech_weapons", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> WEAPON_TABLE = SKILLS.register("weapon_table", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> DURABLE_BREWING = SKILLS.register("durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> CONCENTRATED_BREWING = SKILLS.register("concentrated_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> MULTITASK_BREWING = SKILLS.register("multitask_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> EFFICIENT_BREWING = SKILLS.register("efficient_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> MASTER_BREWER = SKILLS.register("master_brewer", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> SWIFT_BREWING = SKILLS.register("swift_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> CONCENTRATED_DURABLE_BREWING = SKILLS.register("concentrated_durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> POTION_RESISTANCE = SKILLS.register("potion_resistance", () -> new ActionSkill<IHunterPlayer>(HunterActions.POTION_RESISTANCE_HUNTER.get(), true));
    public static final RegistryObject<ISkill> CRUCIFIX_WIELDER = SKILLS.register("crucifix_wielder", () -> new VampirismSkill.SimpleHunterSkill(true));
    public static final RegistryObject<ISkill> ULTIMATE_CRUCIFIX = SKILLS.register("ultimate_crucifix", () -> new VampirismSkill.SimpleHunterSkill(true));

    public static final RegistryObject<ISkill> HUNTER_MINION_STATS_INCREASE = SKILLS.register("hunter_minion_stats_increase", () -> new VampirismSkill.LordHunterSkill(true).setToggleActions(hunter -> hunter.updateMinionAttributes(true), hunter -> hunter.updateMinionAttributes(false)));
    public static final RegistryObject<ISkill> HUNTER_LORD_SPEED = SKILLS.register("hunter_lord_speed", () -> new ActionSkill<IHunterPlayer>(HunterActions.HUNTER_LORD_SPEED.get(), SkillType.LORD,true));
    public static final RegistryObject<ISkill> HUNTER_LORD_ATTACK_SPEED = SKILLS.register("hunter_lord_attack_speed", () -> new ActionSkill<IHunterPlayer>(HunterActions.HUNTER_LORD_ATTACK_SPEED.get(),SkillType.LORD,true));
    public static final RegistryObject<ISkill> HUNTER_MINION_COLLECT = SKILLS.register("hunter_minion_collect", () -> new VampirismSkill.LordHunterSkill(true));
    public static final RegistryObject<ISkill> HUNTER_MINION_RECOVERY = SKILLS.register("hunter_minion_recovery", () -> new MinionRecoverySkill<IHunterPlayer>() {
        @Nonnull
        @Override
        public IPlayableFaction getFaction() {
            return VReference.HUNTER_FACTION;
        }
    });
    public static final RegistryObject<ISkill> MINION_TECH_CROSSBOWS = SKILLS.register("minion_tech_crossbows", () -> new VampirismSkill.LordHunterSkill(true));

    static {
        SKILLS.register(SkillType.LEVEL.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(false));
        SKILLS.register(SkillType.LORD.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(false));
    }


    public static void registerHunterSkills(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if (missingMapping.key.toString().startsWith("vampirism:blood_potion_")) {
                missingMapping.ignore();
            } else if (missingMapping.key.toString().equals("vampirism:holy_water_enhanced")) {
                missingMapping.remap(ENHANCED_BLESSING.get());
            }
        });
    }
}
