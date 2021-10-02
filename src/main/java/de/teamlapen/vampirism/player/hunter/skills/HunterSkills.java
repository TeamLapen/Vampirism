package de.teamlapen.vampirism.player.hunter.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.VampirismConfig;
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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers the default hunter skills
 */
@ObjectHolder(REFERENCE.MODID)
@SuppressWarnings("unused")
public class HunterSkills {

    public static final ISkill<IHunterPlayer> basic_alchemy = getNull();
    public static final ISkill<IHunterPlayer> double_crossbow = getNull();
    public static final ISkill<IHunterPlayer> enhanced_armor = getNull();
    public static final ISkill<IHunterPlayer> enhanced_weapons = getNull();
    public static final ISkill<IHunterPlayer> garlic_diffuser = getNull();
    public static final ISkill<IHunterPlayer> garlic_diffuser_improved = getNull();
    public static final ISkill<IHunterPlayer> holy_water_enhanced = getNull();
    public static final ISkill<IHunterPlayer> hunter_attack_speed = getNull();
    public static final ISkill<IHunterPlayer> hunter_attack_speed_advanced = getNull();
    public static final ISkill<IHunterPlayer> hunter_attack_damage = getNull();
    public static final ISkill<IHunterPlayer> hunter_awareness = getNull();
    public static final ISkill<IHunterPlayer> hunter_disguise = getNull();
    public static final ISkill<IHunterPlayer> purified_garlic = getNull();
    public static final ISkill<IHunterPlayer> stake1 = getNull();
    public static final ISkill<IHunterPlayer> stake2 = getNull();
    public static final ISkill<IHunterPlayer> tech_weapons = getNull();
    public static final ISkill<IHunterPlayer> weapon_table = getNull();
    public static final ISkill<IHunterPlayer> durable_brewing = getNull();
    public static final ISkill<IHunterPlayer> concentrated_brewing = getNull();
    public static final ISkill<IHunterPlayer> multitask_brewing = getNull();
    public static final ISkill<IHunterPlayer> efficient_brewing = getNull();
    public static final ISkill<IHunterPlayer> master_brewer = getNull();
    public static final ISkill<IHunterPlayer> swift_brewing = getNull();
    public static final ISkill<IHunterPlayer> concentrated_durable_brewing = getNull();
    public static final ISkill<IHunterPlayer> potion_resistance = getNull();

    @SuppressWarnings("deprecation")
    public static void registerHunterSkills(IForgeRegistry<ISkill<?>> registry) {
        registry.register(new VampirismSkill.SimpleHunterSkill(VReference.HUNTER_FACTION.getID(), false));
        registry.register(new VampirismSkill.SimpleHunterSkill("basic_alchemy", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("double_crossbow", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_armor", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("enhanced_weapons", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_diffuser", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("garlic_diffuser_improved", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("holy_water_enhanced", true));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed", false).registerAttributeModifier(Attributes.ATTACK_SPEED, "8dd2f8cc-6ae1-4db1-9e14-96b4c74d7bf2", () -> VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_speed_advanced", true).registerAttributeModifier(Attributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", () -> VampirismConfig.BALANCE.hsMajorAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        //Config null, so cannot get method ref
        //noinspection Convert2MethodRef
        registry.register(new VampirismSkill.SimpleHunterSkill("hunter_attack_damage", true).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "ffafd115-96e2-4d08-9588-d1bc9be0d902", () -> VampirismConfig.BALANCE.hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
        registry.register(new ActionSkill<>("hunter_awareness", HunterActions.awareness_hunter, true));
        registry.register(new ActionSkill<>("hunter_disguise", HunterActions.disguise_hunter, true));
        registry.register(new VampirismSkill.SimpleHunterSkill("purified_garlic", true));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake1", false)
                .setDescription(() -> {
                    BaseComponent desc = new TranslatableComponent("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                    if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                        desc.append(new TextComponent(" "));
                        desc.append(new TranslatableComponent("text.vampirism.from_behind"));
                    }
                    return desc;
                }));
        registry.register(new VampirismSkill.SimpleHunterSkill("stake2", false)
                .setDescription(() -> {
                    Component desc = null;
                    if (VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get()) {
                        desc = new TranslatableComponent("skill.vampirism.stake2.desc_npc", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());
                    } else {
                        desc = new TranslatableComponent("skill.vampirism.stake2.desc_all", VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get());

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
        registry.register(new ActionSkill<>("potion_resistance", HunterActions.potion_resistance_hunter, true));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<ISkill<?>> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if (missingMapping.key.toString().startsWith("vampirism:blood_potion_")) {
                missingMapping.ignore();
            }
            else if(missingMapping.key.toString().startsWith("vampirism:garlic_beacon_improved")){
                missingMapping.remap(garlic_diffuser_improved);
            }
            else if(missingMapping.key.toString().startsWith("vampirism:garlic_beacon")){
                missingMapping.remap(garlic_diffuser);
            }
        });
    }
}
