package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(ModRegistries.SKILLS, REFERENCE.MODID);

    public static final RegistryObject<ISkill<IHunterPlayer>> basic_alchemy;
    public static final RegistryObject<ISkill<IHunterPlayer>> double_crossbow;
    public static final RegistryObject<ISkill<IHunterPlayer>> enhanced_armor;
    public static final RegistryObject<ISkill<IHunterPlayer>> enhanced_weapons;
    public static final RegistryObject<ISkill<IHunterPlayer>> garlic_diffuser;
    public static final RegistryObject<ISkill<IHunterPlayer>> garlic_diffuser_improved;
    public static final RegistryObject<ISkill<IHunterPlayer>> holy_water_enhanced;
    public static final RegistryObject<ISkill<IHunterPlayer>> hunter_attack_speed;
    public static final RegistryObject<ISkill<IHunterPlayer>> hunter_attack_speed_advanced;
    public static final RegistryObject<ISkill<IHunterPlayer>> hunter_attack_damage;
    public static final RegistryObject<ISkill<IHunterPlayer>> hunter_awareness;
    public static final RegistryObject<ISkill<IHunterPlayer>> hunter_disguise;
    public static final RegistryObject<ISkill<IHunterPlayer>> purified_garlic;
    public static final RegistryObject<ISkill<IHunterPlayer>> stake1;
    public static final RegistryObject<ISkill<IHunterPlayer>> stake2;
    public static final RegistryObject<ISkill<IHunterPlayer>> tech_weapons;
    public static final RegistryObject<ISkill<IHunterPlayer>> weapon_table;
    public static final RegistryObject<ISkill<IHunterPlayer>> durable_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> concentrated_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> multitask_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> efficient_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> master_brewer;
    public static final RegistryObject<ISkill<IHunterPlayer>> swift_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> concentrated_durable_brewing;
    public static final RegistryObject<ISkill<IHunterPlayer>> potion_resistance;

    public static void registerHunterSkills(IEventBus bus) {
        SKILLS.register(bus);
    }

    static {
        SKILLS.register(VReference.HUNTER_FACTION.getID().getPath(), () -> new VampirismSkill.SimpleHunterSkill(false));
        basic_alchemy = SKILLS.register("basic_alchemy", () -> new VampirismSkill.SimpleHunterSkill(true));
        double_crossbow = SKILLS.register("double_crossbow", () -> new VampirismSkill.SimpleHunterSkill(true));
        enhanced_armor = SKILLS.register("enhanced_armor", () -> new VampirismSkill.SimpleHunterSkill(true));
        enhanced_weapons = SKILLS.register("enhanced_weapons", () -> new VampirismSkill.SimpleHunterSkill(true));
        garlic_diffuser = SKILLS.register("garlic_diffuser", () -> new VampirismSkill.SimpleHunterSkill(true));
        garlic_diffuser_improved = SKILLS.register("garlic_diffuser_improved", () -> new VampirismSkill.SimpleHunterSkill(true));
        holy_water_enhanced = SKILLS.register("holy_water_enhanced", () -> new VampirismSkill.SimpleHunterSkill(true));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        hunter_attack_speed = SKILLS.register("hunter_attack_speed", () -> new VampirismSkill.SimpleHunterSkill(false).registerAttributeModifier(Attributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", () -> VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        hunter_attack_speed_advanced = SKILLS.register("hunter_attack_speed_advanced", () -> new VampirismSkill.SimpleHunterSkill(true).registerAttributeModifier(Attributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", () -> VampirismConfig.BALANCE.hsMajorAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        hunter_attack_damage = SKILLS.register("hunter_attack_damage", () -> new VampirismSkill.SimpleHunterSkill(false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "ffafd115-96e2-4d08-9588-d1bc9be0d902", () -> VampirismConfig.BALANCE.hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
        hunter_awareness = SKILLS.register("hunter_awareness", () -> new ActionSkill<>(HunterActions.awareness_hunter.get(), true));
        hunter_disguise = SKILLS.register("hunter_disguise", () -> new ActionSkill<>(HunterActions.disguise_hunter.get(), true));
        purified_garlic = SKILLS.register("purified_garlic", () -> new VampirismSkill.SimpleHunterSkill(true));
        stake1 = SKILLS.register("stake1", () -> new VampirismSkill.SimpleHunterSkill(false)
                .setDescription(() -> {
                    BaseComponent desc = new TranslatableComponent("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                    if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                        desc.append(new TextComponent(" "));
                        desc.append(new TranslatableComponent("text.vampirism.from_behind"));
                    }
                    return desc;
                }));
        stake2 = SKILLS.register("stake2", () -> new VampirismSkill.SimpleHunterSkill(false)
                .setDescription(() -> {
                    Component desc;
                    if (VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get()) {
                        desc = new TranslatableComponent("skill.vampirism.stake2.desc_npc", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());
                    } else {
                        desc = new TranslatableComponent("skill.vampirism.stake2.desc_all", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());

                    }
                    return desc;
                }));
        tech_weapons = SKILLS.register("tech_weapons", () -> new VampirismSkill.SimpleHunterSkill(true));
        weapon_table = SKILLS.register("weapon_table", () -> new VampirismSkill.SimpleHunterSkill(true));
        durable_brewing = SKILLS.register("durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        concentrated_brewing = SKILLS.register("concentrated_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        multitask_brewing = SKILLS.register("multitask_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        efficient_brewing = SKILLS.register("efficient_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        master_brewer = SKILLS.register("master_brewer", () -> new VampirismSkill.SimpleHunterSkill(true));
        swift_brewing = SKILLS.register("swift_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        concentrated_durable_brewing = SKILLS.register("concentrated_durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(true));
        potion_resistance = SKILLS.register("potion_resistance", () -> new ActionSkill<>(HunterActions.potion_resistance_hunter.get(), true));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill<?>> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if (missingMapping.key.toString().startsWith("vampirism:blood_potion_")) {
                missingMapping.ignore();
            }
            else if(missingMapping.key.toString().startsWith("vampirism:garlic_beacon_improved")){
                missingMapping.remap(garlic_diffuser_improved.get());
            }
            else if(missingMapping.key.toString().startsWith("vampirism:garlic_beacon")){
                missingMapping.remap(garlic_diffuser.get());
            }
        });
    }
}
