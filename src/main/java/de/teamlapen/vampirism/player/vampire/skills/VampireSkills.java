package de.teamlapen.vampirism.player.vampire.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers the default vampire skills
 */
@SuppressWarnings("unused")
public class VampireSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(ModRegistries.SKILLS_ID, REFERENCE.MODID);

    public static final RegistryObject<ISkill<IVampirePlayer>> advanced_biter;
    public static final RegistryObject<ISkill<IVampirePlayer>> bat;
    public static final RegistryObject<ISkill<IVampirePlayer>> blood_charge;
    public static final RegistryObject<ISkill<IVampirePlayer>> blood_vision;
    public static final RegistryObject<ISkill<IVampirePlayer>> blood_vision_garlic;
    public static final RegistryObject<ISkill<IVampirePlayer>> dark_blood_projectile;
    public static final RegistryObject<ISkill<IVampirePlayer>> freeze;
    public static final RegistryObject<ISkill<IVampirePlayer>> half_invulnerable;
    public static final RegistryObject<ISkill<IVampirePlayer>> less_blood_thirst;
    public static final RegistryObject<ISkill<IVampirePlayer>> less_sundamage;
    public static final RegistryObject<ISkill<IVampirePlayer>> night_vision;
    public static final RegistryObject<ISkill<IVampirePlayer>> sunscreen;
    public static final RegistryObject<ISkill<IVampirePlayer>> summon_bats;
    public static final RegistryObject<ISkill<IVampirePlayer>> sword_finisher;
    public static final RegistryObject<ISkill<IVampirePlayer>> teleport;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_disguise;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_invisibility;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_jump;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_rage;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_regeneration;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_speed;
    public static final RegistryObject<ISkill<IVampirePlayer>> water_resistance;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_attack_speed;
    public static final RegistryObject<ISkill<IVampirePlayer>> vampire_attack_damage;
    public static final RegistryObject<ISkill<IVampirePlayer>> neonatal_decrease;
    public static final RegistryObject<ISkill<IVampirePlayer>> dbno_duration;
    public static final RegistryObject<ISkill<IVampirePlayer>> hissing;


    public static void registerVampireSkills(IEventBus bus) {
        SKILLS.register(bus);
    }
    
    static {
        SKILLS.register(VReference.VAMPIRE_FACTION.getID().getPath(), () -> new VampirismSkill.SimpleVampireSkill(false));
        advanced_biter = SKILLS.register("advanced_biter", () -> new VampirismSkill.SimpleVampireSkill(false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = true, player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = false).setHasDefaultDescription());
        bat = SKILLS.register("bat", () -> new ActionSkill<>(VampireActions.bat.get(), true));
        blood_charge = SKILLS.register("blood_charge", () -> new VampirismSkill.SimpleVampireSkill(true));
        blood_vision = SKILLS.register("blood_vision", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> player.unlockVision(VReference.vision_bloodVision), player -> player.unUnlockVision(VReference.vision_bloodVision)));
        blood_vision_garlic = SKILLS.register("blood_vision_garlic", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = true, player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = false));
        dark_blood_projectile = SKILLS.register("dark_blood_projectile", () -> new ActionSkill<>(VampireActions.dark_blood_projectile.get(), true));
        freeze = SKILLS.register("freeze", () -> new ActionSkill<>(VampireActions.freeze.get(), true));
        half_invulnerable = SKILLS.register("half_invulnerable", () -> new ActionSkill<>(VampireActions.half_invulnerable.get(), true));
        less_blood_thirst = SKILLS.register("less_blood_thirst", () -> new VampirismSkill.SimpleVampireSkill(true).registerAttributeModifier(ModAttributes.blood_exhaustion.get(), "980ad86f-fe76-433b-b26a-c4060e0e6751", () -> VampirismConfig.BALANCE.vsBloodThirstReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        less_sundamage = SKILLS.register("less_sundamage", () -> new VampirismSkill.SimpleVampireSkill(true).registerAttributeModifier(ModAttributes.sundamage.get(), "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", () -> VampirismConfig.BALANCE.vsSundamageReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        night_vision = SKILLS.register("night_vision", () -> new VampirismSkill.SimpleVampireSkill(false)
                .setToggleActions(player -> {
                    player.unlockVision(VReference.vision_nightVision);
                    player.activateVision(VReference.vision_nightVision);
                }, player -> player.unUnlockVision(VReference.vision_nightVision)));
        sunscreen = SKILLS.register("sunscreen", () -> new ActionSkill<>(VampireActions.sunscreen.get(), true));
        summon_bats = SKILLS.register("summon_bats", () -> new ActionSkill<>(VampireActions.summon_bat.get(), true));
        sword_finisher = SKILLS.register("sword_finisher", () -> new VampirismSkill.SimpleVampireSkill(true).setDescription(() -> new TranslatableComponent("skill.vampirism.sword_finisher.desc", (int) (VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * 100))));
        teleport = SKILLS.register("teleport", () -> new ActionSkill<>(VampireActions.teleport.get(), true));
        vampire_disguise = SKILLS.register("vampire_disguise", () -> new ActionSkill<>(VampireActions.disguise_vampire.get(), true));
        vampire_invisibility = SKILLS.register("vampire_invisibility", () -> new ActionSkill<>(VampireActions.vampire_invisibility.get()));
        vampire_jump = SKILLS.register("vampire_jump", () -> new VampirismSkill.SimpleVampireSkill(false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(VampirismConfig.BALANCE.vsJumpBoost.get() + 1), player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0)));
        vampire_rage = SKILLS.register("vampire_rage", () -> new ActionSkill<>(VampireActions.vampire_rage.get(), true));
        vampire_regeneration = SKILLS.register("vampire_regeneration", () -> new ActionSkill<>(VampireActions.regen.get(), true));
        vampire_speed = SKILLS.register("vampire_speed", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", () -> VampirismConfig.BALANCE.vsSpeedBoost.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        water_resistance = SKILLS.register("water_resistance", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = true, player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = false));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        vampire_attack_speed = SKILLS.register("vampire_attack_speed", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.ATTACK_SPEED, "d4aa1d08-5e0e-4946-86dc-95a1e6f5be20", () -> VampirismConfig.BALANCE.vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        vampire_attack_damage = SKILLS.register("vampire_attack_damage", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "f2acc818-dc3a-4696-ba63-c3294290ad86", () -> VampirismConfig.BALANCE.vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
        neonatal_decrease = SKILLS.register("neonatal_decrease", () -> new VampirismSkill.SimpleVampireSkill(true));
        dbno_duration = SKILLS.register("dbno_duration", () -> new VampirismSkill.SimpleVampireSkill(true));
        hissing = SKILLS.register("hissing", () -> new ActionSkill<>(VampireActions.hissing.get(), true));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill<?>> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:creeper_avoided".equals(missingMapping.key.toString())) {
                missingMapping.ignore();
            } else if ("vampirism:enhanced_crossbow".equals(missingMapping.key.toString())) {
                missingMapping.ignore();
            } else if ("vampirism:vampire_forest_fog".equals(missingMapping.key.toString())) {
                missingMapping.ignore();
            }
        });
    }
}
