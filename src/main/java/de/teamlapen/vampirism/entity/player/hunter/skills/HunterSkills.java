package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, REFERENCE.MODID);

    public static final RegistryObject<ISkill<IHunterPlayer>> BASIC_ALCHEMY = SKILLS.register("basic_alchemy", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> DOUBLE_CROSSBOW = SKILLS.register("double_crossbow", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> ENHANCED_ARMOR = SKILLS.register("enhanced_armor", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> ENHANCED_WEAPONS = SKILLS.register("enhanced_weapons", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> GARLIC_DIFFUSER = SKILLS.register("garlic_diffuser", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> GARLIC_DIFFUSER_IMPROVED = SKILLS.register("garlic_diffuser_improved", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> ENHANCED_BLESSING = SKILLS.register("enhanced_blessing", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_ATTACK_SPEED = SKILLS.register("hunter_attack_speed", () -> new VampirismSkill.SimpleHunterSkill(2, false).registerAttributeModifier(Attributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", () -> VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_ATTACK_SPEED_ADVANCED = SKILLS.register("hunter_attack_speed_advanced", () -> new VampirismSkill.SimpleHunterSkill(2, true).registerAttributeModifier(Attributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", () -> VampirismConfig.BALANCE.hsMajorAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_ATTACK_DAMAGE = SKILLS.register("hunter_attack_damage", () -> new VampirismSkill.SimpleHunterSkill(2, false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "ffafd115-96e2-4d08-9588-d1bc9be0d902", () -> VampirismConfig.BALANCE.hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_AWARENESS = SKILLS.register("hunter_awareness", () -> new ActionSkill<>(HunterActions.AWARENESS_HUNTER, 2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_DISGUISE = SKILLS.register("hunter_disguise", () -> new ActionSkill<>(HunterActions.DISGUISE_HUNTER, 1, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> PURIFIED_GARLIC = SKILLS.register("purified_garlic", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> STAKE1 = SKILLS.register("stake1", () -> new VampirismSkill.SimpleHunterSkill(2, false)
            .setDescription(() -> {
                MutableComponent desc = Component.translatable("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                    desc.append(Component.literal(" "));
                    desc.append(Component.translatable("text.vampirism.from_behind"));
                }
                return desc;
            }));
    public static final RegistryObject<ISkill<IHunterPlayer>> STAKE2 = SKILLS.register("stake2", () -> new VampirismSkill.SimpleHunterSkill(2, false)
            .setDescription(() -> {
                Component desc;
                if (VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get()) {
                    desc = Component.translatable("skill.vampirism.stake2.desc_npc", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());
                } else {
                    desc = Component.translatable("skill.vampirism.stake2.desc_all", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());

                }
                return desc;
            }));
    public static final RegistryObject<ISkill<IHunterPlayer>> TECH_WEAPONS = SKILLS.register("tech_weapons", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> WEAPON_TABLE = SKILLS.register("weapon_table", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> DURABLE_BREWING = SKILLS.register("durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> CONCENTRATED_BREWING = SKILLS.register("concentrated_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> MULTITASK_BREWING = SKILLS.register("multitask_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> EFFICIENT_BREWING = SKILLS.register("efficient_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> MASTER_BREWER = SKILLS.register("master_brewer", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> SWIFT_BREWING = SKILLS.register("swift_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> CONCENTRATED_DURABLE_BREWING = SKILLS.register("concentrated_durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> POTION_RESISTANCE = SKILLS.register("potion_resistance", () -> new ActionSkill<>(HunterActions.POTION_RESISTANCE_HUNTER, 2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> CRUCIFIX_WIELDER = SKILLS.register("crucifix_wielder", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> ULTIMATE_CRUCIFIX = SKILLS.register("ultimate_crucifix", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> MINION_COLLECT = SKILLS.register("hunter_minion_collect", () -> new VampirismSkill.LordHunterSkill(2, true));
    public static final RegistryObject<ISkill<IHunterPlayer>> MINION_STATS_INCREASE = SKILLS.register("hunter_minion_stats_increase", () -> new VampirismSkill.LordHunterSkill(3, true).setToggleActions(hunter -> hunter.updateMinionAttributes(true), hunter -> hunter.updateMinionAttributes(false)));
    public static final RegistryObject<ISkill<IHunterPlayer>> MINION_TECH_CROSSBOWS = SKILLS.register("minion_tech_crossbows", () -> new VampirismSkill.LordHunterSkill(1, true));

    static {
        SKILLS.register(SkillType.LEVEL.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(2, false));
        SKILLS.register(SkillType.LORD.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(2, false));
    }

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
