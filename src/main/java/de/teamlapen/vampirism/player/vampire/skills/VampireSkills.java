package de.teamlapen.vampirism.player.vampire.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.TextFormatting;

/**
 * Registers the default vampire skills
 */
public class VampireSkills {

    public static void registerVampireSkills() {
        ISkillRegistry registry = VampirismAPI.skillRegistry();
        SkillNode root = registry.setRootSkill(VReference.VAMPIRE_FACTION, new VampirismSkill.SimpleVampireSkill("root_vampire", 32, 0, false));

        SkillNode skill2 = new SkillNode(root, new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "second";
            }

            @Override
            public int getMinU() {
                return 48;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
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
        SkillNode skill3 = new SkillNode(skill2, new ActionSkill<>(VampireActions.regenAction, "regen"));
        SkillNode skill4 = new SkillNode(skill3, new ActionSkill<>(VampireActions.batAction, "bat"));
        registerOffensiveSkills(skill4);
        registerUtilSkills(skill4);
        registerDefensiveSkills(skill4);
    }

    private static void registerUtilSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new ActionSkill<>(VampireActions.summonBatAction, "2summonbats"));
        DefaultSkill<IVampirePlayer> damage = new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "2lesssundamage";
            }

            @Override
            public int getMinU() {
                return 96;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
                return "text.vampirism.skill.less_sundamage";
            }
        };
        damage.registerAttributeModifier(VReference.sunDamage, "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", Balance.vps.SUNDAMAGE_REDUCTION1, 2);
        DefaultSkill<IVampirePlayer> damage2 = new VampirismSkill.SimpleVampireSkill("2waterresistance", 208, 0, true) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().waterResistance = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().waterResistance = true;
            }
        };

        SkillNode skill2 = new SkillNode(skill1, damage, damage2);

        SkillNode skill3 = new SkillNode(skill2, (new VampirismSkill.SimpleVampireSkill("2lessbloodthirst", 80, 0, true)).registerAttributeModifier(VReference.bloodExhaustion, "980ad86f-fe76-433b-b26a-c4060e0e6751", Balance.vps.BLOOD_THIRST_REDUCTION1, 2));
        SkillNode skill4 = new SkillNode(skill3, new ActionSkill<>(VampireActions.disguiseAction, "2disguise"));
        //TODO add one more
        SkillNode skill6 = new SkillNode(skill4, new ActionSkill<>(VampireActions.invisibilityAction, "2invisibility"));
    }

    private static void registerOffensiveSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new ActionSkill<>(VampireActions.rageAction, "3rage"));
        DefaultSkill<IVampirePlayer> bite = new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3bite1";
            }

            @Override
            public String getLocalizedDescription() {
                return UtilLib.translate("text.vampirism.skill.more_bite_damage.desc");
            }

            @Override
            public int getMinU() {
                return 128;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
                return "text.vampirism.skill.more_bite_damage";
            }
        };
        bite.registerAttributeModifier(VReference.biteDamage, "A08CAB62-EE88-4DB9-8F62-E9EF108A4E87", Balance.vps.BITE_DAMAGE_MULT, 1);
        DefaultSkill<IVampirePlayer> bite2 = new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "3bite2";
            }

            @Override
            public String getLocalizedDescription() {
                return UtilLib.translate("text.vampirism.skill.poisonous_bite.desc");
            }

            @Override
            public int getMinU() {
                return 112;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
                return "text.vampirism.skill.poisonous_bite";
            }

            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().poisonous_bite = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().poisonous_bite = true;
            }
        };
        SkillNode skill2 = new SkillNode(skill1, bite, bite2);
        SkillNode skill3 = new SkillNode(skill2, new ActionSkill<>(VampireActions.freezeAction, "1freeze"));

        //TODO add lighting or so

    }

    private static void registerDefensiveSkills(SkillNode start) {
        SkillNode skill1 = new SkillNode(start, new ActionSkill<>(VampireActions.sunscreenVampireAction, "1sunscreen"));
        DefaultSkill<IVampirePlayer> jump = new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "1jump";
            }

            @Override
            public int getMinU() {
                return 160;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
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
        DefaultSkill<IVampirePlayer> speed = new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "1speed";
            }

            @Override
            public int getMinU() {
                return 144;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
                return "effect.moveSpeed";
            }
        };
        speed.registerAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", Balance.vps.SPEED_BOOST, 2);
        SkillNode skill2 = new SkillNode(skill1, jump, speed);

        SkillNode skill3 = new SkillNode(skill2, new VampirismSkill.SimpleVampireSkill("1bloodvision", 176, 0, true) {


            @Override
            protected void onDisabled(IVampirePlayer player) {
                player.unUnlockVision(VReference.vision_bloodVision);
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                player.unlockVision(VReference.vision_bloodVision);
            }
        });
        SkillNode skill4 = new SkillNode(skill3, new VampirismSkill<IVampirePlayer>() {
            @Override
            public String getID() {
                return "1creeper";
            }

            @Override
            public String getLocalizedDescription() {
                if (Balance.vps.DISABLE_AVOIDED_BY_CREEPERS) {
                    return TextFormatting.RED + "Disabled by admin" + TextFormatting.RESET;
                }
                return super.getLocalizedDescription();
            }

            @Override
            public int getMinU() {
                return 192;
            }

            @Override
            public int getMinV() {
                return 0;
            }

            @Override
            public String getUnlocalizedName() {
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
        SkillNode skill5 = new SkillNode(skill4, new VampirismSkill.SimpleVampireSkill("1forestfog", 224, 0, true) {
            @Override
            protected void onDisabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = false;
            }

            @Override
            protected void onEnabled(IVampirePlayer player) {
                ((VampirePlayer) player).getSpecialAttributes().increasedVampireFogDistance = true;

            }
        });
        SkillNode skill6 = new SkillNode(skill5, new ActionSkill<>(VampireActions.teleportAction, "1teleport"));


    }
}
