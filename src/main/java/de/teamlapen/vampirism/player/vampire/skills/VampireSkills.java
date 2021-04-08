package de.teamlapen.vampirism.player.vampire.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers the default vampire skills
 */
@ObjectHolder(REFERENCE.MODID)
@SuppressWarnings("unused")
public class VampireSkills {

    public static final ISkill advanced_biter = getNull();
    public static final ISkill bat = getNull();
    public static final ISkill blood_charge = getNull();
    public static final ISkill blood_vision = getNull();
    public static final ISkill blood_vision_garlic = getNull();
    public static final ISkill dark_blood_projectile = getNull();
    public static final ISkill freeze = getNull();
    public static final ISkill half_invulnerable = getNull();
    public static final ISkill less_blood_thirst = getNull();
    public static final ISkill less_sundamage = getNull();
    public static final ISkill night_vision = getNull();
    public static final ISkill sunscreen = getNull();
    public static final ISkill summon_bats = getNull();
    public static final ISkill sword_finisher = getNull();
    public static final ISkill teleport = getNull();
    public static final ISkill vampire_disguise = getNull();
    public static final ISkill vampire_forest_fog = getNull();
    public static final ISkill vampire_invisibility = getNull();
    public static final ISkill vampire_jump = getNull();
    public static final ISkill vampire_rage = getNull();
    public static final ISkill vampire_regeneration = getNull();
    public static final ISkill vampire_speed = getNull();
    public static final ISkill water_resistance = getNull();
    public static final ISkill vampire_attack_speed = getNull();
    public static final ISkill vampire_attack_damage = getNull();


    @SuppressWarnings({"deprecation", "Convert2MethodRef"})
    public static void registerVampireSkills(IForgeRegistry<ISkill> registry) {
        registry.register(new VampirismSkill.SimpleVampireSkill(VReference.VAMPIRE_FACTION.getID(), false));
        registry.register(new VampirismSkill.SimpleVampireSkill("advanced_biter", false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = true, player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = false).setHasDefaultDescription());
        registry.register(new ActionSkill<>("bat", VampireActions.bat, true));
        registry.register(new VampirismSkill.SimpleVampireSkill("blood_charge", true));
        registry.register(new VampirismSkill.SimpleVampireSkill("blood_vision", true).setToggleActions(player -> player.unlockVision(VReference.vision_bloodVision), player -> player.unUnlockVision(VReference.vision_bloodVision)));
        registry.register(new VampirismSkill.SimpleVampireSkill("blood_vision_garlic", true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = true, player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = false));
        registry.register(new ActionSkill<>("dark_blood_projectile", VampireActions.dark_blood_projectile, true));
        registry.register(new ActionSkill<>("freeze", VampireActions.freeze, true));
        registry.register(new ActionSkill<>("half_invulnerable", VampireActions.half_invulnerable, true));
        registry.register(new VampirismSkill.SimpleVampireSkill("less_blood_thirst", true).registerAttributeModifier(ModAttributes.blood_exhaustion, "980ad86f-fe76-433b-b26a-c4060e0e6751", () -> VampirismConfig.BALANCE.vsBloodThirstReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismSkill.SimpleVampireSkill("less_sundamage", true).registerAttributeModifier(ModAttributes.sundamage, "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", () -> VampirismConfig.BALANCE.vsSundamageReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismSkill.SimpleVampireSkill("night_vision", false)
                .setToggleActions(player -> {
                    player.unlockVision(VReference.vision_nightVision);
                    player.activateVision(VReference.vision_nightVision);
                }, player -> player.unUnlockVision(VReference.vision_nightVision)));
        registry.register(new ActionSkill<>("sunscreen", VampireActions.sunscreen, true));
        registry.register(new ActionSkill<>("summon_bats", VampireActions.summon_bat, true));
        registry.register(new VampirismSkill.SimpleVampireSkill("sword_finisher", true).setDescription(() -> new TranslationTextComponent("skill.vampirism.sword_finisher.desc", (int) (VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * 100))));
        registry.register(new ActionSkill<>("teleport", VampireActions.teleport, true));
        registry.register(new ActionSkill<>("vampire_disguise", VampireActions.disguise_vampire, true));
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_forest_fog", true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = true, player -> ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = false));
        registry.register(new ActionSkill<>("vampire_invisibility", VampireActions.vampire_invisibility));
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_jump", false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(VampirismConfig.BALANCE.vsJumpBoost.get() + 1), player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0)));
        registry.register(new ActionSkill<>("vampire_rage", VampireActions.vampire_rage, true));
        registry.register(new ActionSkill<>("vampire_regeneration", VampireActions.regen, true));
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_speed", false).registerAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", () -> VampirismConfig.BALANCE.vsSpeedBoost.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismSkill.SimpleVampireSkill("water_resistance", true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = true, player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = false));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_attack_speed", false).registerAttributeModifier(Attributes.ATTACK_SPEED, "d4aa1d08-5e0e-4946-86dc-95a1e6f5be20", () -> VampirismConfig.BALANCE.vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_attack_damage", false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "f2acc818-dc3a-4696-ba63-c3294290ad86", () -> VampirismConfig.BALANCE.vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:creeper_avoided".equals(missingMapping.key.toString())) {
                missingMapping.ignore();
            }
        });
    }
}
