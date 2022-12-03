package de.teamlapen.vampirism.entity.player.vampire.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Registers the default vampire skills
 */
@SuppressWarnings("unused")
public class VampireSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, REFERENCE.MODID);

    public static final RegistryObject<ISkill<IVampirePlayer>> ADVANCED_BITER = SKILLS.register("advanced_biter", () -> new VampirismSkill.SimpleVampireSkill(false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = true, player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = false).setHasDefaultDescription());
    public static final RegistryObject<ISkill<IVampirePlayer>> FLEDGLING = SKILLS.register("fledgling", () -> new VampirismSkill.SimpleVampireSkill(true) {
        @Override
        protected void getActions(@NotNull Collection<IAction<IVampirePlayer>> list) {
            list.add(VampireActions.BAT.get());
            list.add(VampireActions.INFECT.get());
        }
    });
    public static final RegistryObject<ISkill<IVampirePlayer>> BLOOD_CHARGE = SKILLS.register("blood_charge", () -> new VampirismSkill.SimpleVampireSkill(true));
    public static final RegistryObject<ISkill<IVampirePlayer>> BLOOD_VISION = SKILLS.register("blood_vision", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> player.unlockVision(VReference.vision_bloodVision), player -> player.unUnlockVision(VReference.vision_bloodVision)));
    public static final RegistryObject<ISkill<IVampirePlayer>> BLOOD_VISION_GARLIC = SKILLS.register("blood_vision_garlic", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = true, player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = false));
    public static final RegistryObject<ISkill<IVampirePlayer>> DARK_BLOOD_PROJECTILE = SKILLS.register("dark_blood_projectile", () -> new ActionSkill<>(VampireActions.DARK_BLOOD_PROJECTILE, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> FREEZE = SKILLS.register("freeze", () -> new ActionSkill<>(VampireActions.FREEZE, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> HALF_INVULNERABLE = SKILLS.register("half_invulnerable", () -> new ActionSkill<>(VampireActions.HALF_INVULNERABLE, true));
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IVampirePlayer>> LESS_BLOOD_THIRST = SKILLS.register("less_blood_thirst", () -> new VampirismSkill.SimpleVampireSkill(true).registerAttributeModifier(ModAttributes.BLOOD_EXHAUSTION.get(), "980ad86f-fe76-433b-b26a-c4060e0e6751", () -> VampirismConfig.BALANCE.vsBloodThirstReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IVampirePlayer>> LESS_SUNDAMAGE = SKILLS.register("less_sundamage", () -> new VampirismSkill.SimpleVampireSkill(true).registerAttributeModifier(ModAttributes.SUNDAMAGE.get(), "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", () -> VampirismConfig.BALANCE.vsSundamageReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<ISkill<IVampirePlayer>> NIGHT_VISION = SKILLS.register("night_vision", () -> new VampirismSkill.SimpleVampireSkill(false)
            .setToggleActions(player -> {
                player.unlockVision(VReference.vision_nightVision);
                player.activateVision(VReference.vision_nightVision);
            }, player -> player.unUnlockVision(VReference.vision_nightVision)));
    public static final RegistryObject<ISkill<IVampirePlayer>> SUNSCREEN = SKILLS.register("sunscreen", () -> new ActionSkill<>(VampireActions.SUNSCREEN, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> SUMMON_BATS = SKILLS.register("summon_bats", () -> new ActionSkill<>(VampireActions.SUMMON_BAT, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> SWORD_FINISHER = SKILLS.register("sword_finisher", () -> new VampirismSkill.SimpleVampireSkill(true).setDescription(() -> Component.translatable("skill.vampirism.sword_finisher.desc", (int) (VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * 100))));
    public static final RegistryObject<ISkill<IVampirePlayer>> TELEPORT = SKILLS.register("teleport", () -> new ActionSkill<>(VampireActions.TELEPORT, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_DISGUISE = SKILLS.register("vampire_disguise", () -> new ActionSkill<>(VampireActions.DISGUISE_VAMPIRE, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_INVISIBILITY = SKILLS.register("vampire_invisibility", () -> new ActionSkill<>(VampireActions.VAMPIRE_INVISIBILITY));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_JUMP = SKILLS.register("vampire_jump", () -> new VampirismSkill.SimpleVampireSkill(false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(VampirismConfig.BALANCE.vsJumpBoost.get() + 1), player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0)));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_RAGE = SKILLS.register("vampire_rage", () -> new ActionSkill<>(VampireActions.VAMPIRE_RAGE, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_REGENERATION = SKILLS.register("vampire_regeneration", () -> new ActionSkill<>(VampireActions.REGEN, true));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_SPEED = SKILLS.register("vampire_speed", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", () -> VampirismConfig.BALANCE.vsSpeedBoost.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<ISkill<IVampirePlayer>> WATER_RESISTANCE = SKILLS.register("water_resistance", () -> new VampirismSkill.SimpleVampireSkill(true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = true, player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = false));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_ATTACK_SPEED = SKILLS.register("vampire_attack_speed", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.ATTACK_SPEED, "d4aa1d08-5e0e-4946-86dc-95a1e6f5be20", () -> VampirismConfig.BALANCE.vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_ATTACK_DAMAGE = SKILLS.register("vampire_attack_damage", () -> new VampirismSkill.SimpleVampireSkill(false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "f2acc818-dc3a-4696-ba63-c3294290ad86", () -> VampirismConfig.BALANCE.vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<ISkill<IVampirePlayer>> NEONATAL_DECREASE = SKILLS.register("neonatal_decrease", () -> new VampirismSkill.SimpleVampireSkill(true));
    public static final RegistryObject<ISkill<IVampirePlayer>> DBNO_DURATION = SKILLS.register("dbno_duration", () -> new VampirismSkill.SimpleVampireSkill(true));
    public static final RegistryObject<ISkill<IVampirePlayer>> HISSING = SKILLS.register("hissing", () -> new ActionSkill<>(VampireActions.HISSING, true));
    public static final RegistryObject<ISkill<IVampirePlayer>> MINION_COLLECT = SKILLS.register("vampire_minion_collect", () -> new VampirismSkill.LordVampireSkill(true));
    public static final RegistryObject<ISkill<IVampirePlayer>> MINION_STATS_INCREASE = SKILLS.register("vampire_minion_stats_increase", () -> new VampirismSkill.LordVampireSkill(true).setToggleActions(vampire -> vampire.updateMinionAttributes(true), vampire -> vampire.updateMinionAttributes(false)));

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    static {
        SKILLS.register(SkillType.LEVEL.createIdForFaction(VReference.VAMPIRE_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleVampireSkill(false));
        SKILLS.register(SkillType.LORD.createIdForFaction(VReference.VAMPIRE_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleVampireSkill(false));
    }

    public static void fixMappings(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(VampirismRegistries.SKILLS_ID).forEach(missingMapping -> {
            switch (missingMapping.getKey().toString()) {
                case "vampirism:creeper_avoided", "vampirism:enhanced_crossbow", "vampirism:vampire_forest_fog" -> missingMapping.ignore();
                case "vampirism:bat" -> missingMapping.remap(FLEDGLING.get());
            }
        });
    }
}
