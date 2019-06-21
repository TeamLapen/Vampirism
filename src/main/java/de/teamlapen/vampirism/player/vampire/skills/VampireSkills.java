package de.teamlapen.vampirism.player.vampire.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Registers the default vampire skills
 */
@ObjectHolder(REFERENCE.MODID)
public class VampireSkills {

    public static final ISkill night_vision = UtilLib.getNull();
    public static final ISkill vampire_regeneration = UtilLib.getNull();
    public static final ISkill bat = UtilLib.getNull();
    public static final ISkill summon_bats = UtilLib.getNull();
    public static final ISkill less_sundamage = UtilLib.getNull();
    public static final ISkill water_resistance = UtilLib.getNull();
    public static final ISkill less_blood_thirst = UtilLib.getNull();
    public static final ISkill vampire_disguise = UtilLib.getNull();
    public static final ISkill vampire_invisibility = UtilLib.getNull();
    public static final ISkill vampire_rage = UtilLib.getNull();
    public static final ISkill freeze = UtilLib.getNull();
    public static final ISkill sunscreen = UtilLib.getNull();
    public static final ISkill vampire_jump = UtilLib.getNull();
    public static final ISkill vampire_speed = UtilLib.getNull();
    public static final ISkill blood_vision = UtilLib.getNull();
    public static final ISkill creeper_avoided = UtilLib.getNull();
    public static final ISkill vampire_forest_fog = UtilLib.getNull();
    public static final ISkill teleport = UtilLib.getNull();
    public static final ISkill blood_charge = UtilLib.getNull();
    public static final ISkill sword_finisher = UtilLib.getNull();
    public static final ISkill dark_blood_projectile = UtilLib.getNull();
    public static final ISkill half_invulnerable = UtilLib.getNull();
    public static final ISkill advanced_biter = UtilLib.getNull();
    public static final ISkill garlic_blood_vision = UtilLib.getNull();

    public static void registerVampireSkills(IForgeRegistry<ISkill> registry) {
        registry.register(new VampirismSkill.SimpleVampireSkill(VReference.VAMPIRE_FACTION.getKey(), 32, 0, false));
        registry.register(new VampirismSkill.SimpleVampireSkill("night_vision", 48, 0, false) {

            @Override
            public String getTranslationKey() {
                return "text.vampirism.skill.night_vision";
            }


            @Override
            protected void onDisabled(IVampirePlayer player) {
                player.unUnlockVision(VReference.vision_nightVision);
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                player.unlockVision(VReference.vision_nightVision);
                player.activateVision(VReference.vision_nightVision);
            }
        });
        registry.register(new ActionSkill<>("vampire_regeneration", VampireActions.regen));
        registry.register(new ActionSkill<>("bat", VampireActions.bat));
        registry.register(new ActionSkill<>("summon_bats", VampireActions.summon_bat, true));
        DefaultSkill<IVampirePlayer> damage = new VampirismSkill.SimpleVampireSkill("less_sundamage", 96, 0, false);
        damage.registerAttributeModifier(VReference.sunDamage, "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", Balance.vps.SUNDAMAGE_REDUCTION1, 2);
        registry.register(damage);
        DefaultSkill<IVampirePlayer> damage2 = new VampirismSkill.SimpleVampireSkill("water_resistance", 208, 0, true) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().waterResistance = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().waterResistance = true;
            }
        };
        registry.register(damage2);
        registry.register((new VampirismSkill.SimpleVampireSkill("less_blood_thirst", 80, 0, true)).registerAttributeModifier(VReference.bloodExhaustion, "980ad86f-fe76-433b-b26a-c4060e0e6751", Balance.vps.BLOOD_THIRST_REDUCTION1, 2));
        registry.register(new ActionSkill<>("vampire_disguise", VampireActions.disguise_vampire));
        registry.register(new ActionSkill<>("vampire_invisibility", VampireActions.vampire_invisibility));
        registry.register(new ActionSkill<>("vampire_rage", VampireActions.vampire_rage, true));

        VampirismSkill.SimpleVampireSkill advanced_biter = new VampirismSkill.SimpleVampireSkill("advanced_biter", 128, 0, false) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().advanced_biter = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().advanced_biter = true;
            }
        };
        advanced_biter.setHasDefaultDescription();
        advanced_biter.registerAttributeModifier(VReference.biteDamage, "A08CAB62-EE88-4DB9-8F62-E9EF108A4E87", Balance.vps.BITE_DAMAGE_MULT, 1);
        registry.register(advanced_biter);

        registry.register(new VampirismSkill.SimpleVampireSkill("blood_charge", 240, 0, true));
        registry.register(new ActionSkill<>("freeze", VampireActions.freeze, true));
        registry.register(new ActionSkill<>("sunscreen", VampireActions.sunscreen, true));
        DefaultSkill<IVampirePlayer> jump = new VampirismSkill.SimpleVampireSkill("vampire_jump", 160, 0, false) {

            @Override
            public String getTranslationKey() {
                return "effect.jump";
            }

            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0);
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(Balance.vps.JUMP_BOOST + 1);
            }
        };
        registry.register(jump);
        DefaultSkill<IVampirePlayer> speed = new VampirismSkill.SimpleVampireSkill("vampire_speed", 144, 0, false) {

            @Override
            public String getTranslationKey() {
                return "effect.moveSpeed";
            }
        };
        speed.registerAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", Balance.vps.SPEED_BOOST, 2);
        registry.register(speed);
        registry.register(new VampirismSkill.SimpleVampireSkill("blood_vision", 176, 0, true) {


            @Override
            protected void onDisabled(IVampirePlayer player) {
                player.unUnlockVision(VReference.vision_bloodVision);
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                player.unlockVision(VReference.vision_bloodVision);
            }
        });
        registry.register(new VampirismSkill.SimpleVampireSkill("creeper_avoided", 192, 0, false) {


            @Override
            public ITextComponent getDescription() {
                if (Balance.vps.DISABLE_AVOIDED_BY_CREEPERS) {
                    return new TextComponentString("Disabled by admin").applyTextStyle(TextFormatting.RED);
                }
                return super.getDescription();
            }


            @Override
            public String getTranslationKey() {
                return "text.vampirism.skill.avoided_by_creepers";
            }

            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().avoided_by_creepers = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().avoided_by_creepers = true;
            }
        });
        registry.register(new VampirismSkill.SimpleVampireSkill("vampire_forest_fog", 224, 0, true) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = true;

            }
        });
        registry.register(new ActionSkill<>("teleport", VampireActions.teleport));
        registry.register(new VampirismSkill.SimpleVampireSkill("sword_finisher", 0, 16, true) {
            @Override
            public ITextComponent getDescription() {
                return new TextComponentTranslation("text.vampirism.skill.sword_finisher.desc", (int) (Balance.vps.SWORD_FINISHER_MAX_HEALTH_PERC * 100));
            }
        });
        registry.register(new ActionSkill<>("dark_blood_projectile", VampireActions.dark_blood_projectile));
        registry.register(new ActionSkill<>("half_invulnerable", VampireActions.half_invulnerable, true));
        registry.register(new VampirismSkill.SimpleVampireSkill(new ResourceLocation("vampirism", "garlic_blood_vision"), 16, 16, true) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().garlic_blood_vision = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().garlic_blood_vision = true;
            }
        });

    }

    public static void buildSkillTree(SkillNode root) {
        ISkillManager skillManager = VampirismAPI.skillManager();
        SkillNode skill2 = skillManager.createSkillNode(root, night_vision);
        SkillNode skill3 = skillManager.createSkillNode(skill2, vampire_regeneration);
        SkillNode skill4 = skillManager.createSkillNode(skill3, bat);
        registerOffensiveSkills(skillManager, skill4);
        registerUtilSkills(skillManager, skill4);
        registerDefensiveSkills(skillManager, skill4);
    }


    private static void registerUtilSkills(ISkillManager skillManager, SkillNode start) {
        SkillNode skill1 = skillManager.createSkillNode(start, summon_bats);


        SkillNode skill2 = skillManager.createSkillNode(skill1, less_sundamage, water_resistance);

        SkillNode skill3 = skillManager.createSkillNode(skill2, less_blood_thirst);
        SkillNode skill4 = skillManager.createSkillNode(skill3, vampire_disguise);
        SkillNode skill5 = skillManager.createSkillNode(skill4, half_invulnerable);
        SkillNode skill6 = skillManager.createSkillNode(skill5, vampire_invisibility);
    }

    private static void registerOffensiveSkills(ISkillManager skillManager, SkillNode start) {
        SkillNode skill1 = skillManager.createSkillNode(start, vampire_rage);


        SkillNode skill2 = skillManager.createSkillNode(skill1, advanced_biter);
        SkillNode skill3 = skillManager.createSkillNode(skill2, sword_finisher);
        SkillNode skill4 = skillManager.createSkillNode(skill3, dark_blood_projectile);
        SkillNode skill5 = skillManager.createSkillNode(skill4, blood_charge);
        SkillNode skill6 = skillManager.createSkillNode(skill5, freeze);

        //TODO add lighting or so

    }

    private static void registerDefensiveSkills(ISkillManager skillManager, SkillNode start) {
        SkillNode skill1 = skillManager.createSkillNode(start, sunscreen);


        SkillNode skill2 = skillManager.createSkillNode(skill1, vampire_jump, vampire_speed);

        SkillNode skill3 = skillManager.createSkillNode(skill2, blood_vision);
        SkillNode skill4 = skillManager.createSkillNode(skill3, creeper_avoided);

        SkillNode skill5 = skillManager.createSkillNode(skill4, vampire_forest_fog, garlic_blood_vision);
        SkillNode skill6 = skillManager.createSkillNode(skill5, teleport);


    }

    public static void fixMapping(RegistryEvent.MissingMappings.Mapping<ISkill> m) {
        if (new ResourceLocation("vampirism:bite1").equals(m.key) || new ResourceLocation("vampirism:bite2").equals(m.key)) {
            m.ignore();
        }
    }
}
