package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.vampire.ILastingVampireSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.SkillRegistry;
import de.teamlapen.vampirism.entity.player.vampire.skills.*;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Handles skill for vampire players
 */
public class SkillHandler implements ISkillHandler {
    private final static String TAG = "SkillHandler";
    public static FreezeSkill freezeSkill;
    public static InvisibilitySkill invisibilitySkill;
    public static RegenSkill regenSkill;
    public static TeleportSkill teleportSkill;
    public static VampireRageSkill rageSkill;
    public static BatSkill batSkill;
    /**
     * Saves timers for skill ids
     * Values:
     * 0 - Inactive
     * <0 - Cooldown
     * >0 - Active {@link ILastingVampireSkill}
     */
    private final int[] skillTimer;
    private final VampirePlayer vampire;
    private boolean dirty = false;

    SkillHandler(VampirePlayer player) {
        vampire = player;
        this.skillTimer = new int[SkillRegistry.getSkillCount()];
    }

    public static void registerDefaultSkills() {
        freezeSkill = SkillRegistry.registerSkill(new FreezeSkill(), "freeze");
        invisibilitySkill = SkillRegistry.registerSkill(new InvisibilitySkill(), "invisible");
        regenSkill = SkillRegistry.registerSkill(new RegenSkill(), "regen");
        teleportSkill = SkillRegistry.registerSkill(new TeleportSkill(), "teleport");
        rageSkill = SkillRegistry.registerSkill(new VampireRageSkill(), "rage");
        batSkill = SkillRegistry.registerSkill(new BatSkill(), "bat");
    }

    void loadFromNbt(NBTTagCompound nbt) {
        NBTTagCompound skills = nbt.getCompoundTag("skills");
        if (skills != null) {
            for (String key : skills.getKeySet()) {
                IVampireSkill skill = SkillRegistry.getSkillFromKey(key);
                if (skill == null) {
                    VampirismMod.log.w(TAG, "Did not find skill with key %s", key);
                } else {
                    skillTimer[SkillRegistry.getIdFromSkill(skill)] = skills.getInteger(key);
                }
            }
        }
    }

    void saveToNbt(NBTTagCompound nbt) {
        NBTTagCompound skills = new NBTTagCompound();
        for (int i = 0; i < skillTimer.length; i++) {
            IVampireSkill s = SkillRegistry.getSkillFromId(i);
            String key = SkillRegistry.getKeyFromSkill(s);
            skills.setInteger(key, skillTimer[i]);
        }
        nbt.setTag("skills", skills);
    }

    void onSkillsReactivated() {
        if (!vampire.isRemote()) {
            for (int i = 0; i < skillTimer.length; i++) {
                if (skillTimer[i] > 0) {
                    ((ILastingVampireSkill) SkillRegistry.getSkillFromId(i)).onReActivated(vampire);
                }
            }
        }

    }

    void writeUpdateForClient(NBTTagCompound nbt) {
        nbt.setIntArray("skill_timers", skillTimer);
    }

    void readUpdateFromServer(NBTTagCompound nbt) {

        if (nbt.hasKey("skill_timers")) {
            int[] updated = nbt.getIntArray("skill_timers");
            for (int i = 0; i < skillTimer.length; i++) {
                int old = skillTimer[i];
                skillTimer[i] = updated[i];
                if (updated[i] > 0 && old <= 0) {
                    ((ILastingVampireSkill) SkillRegistry.getSkillFromId(i)).onActivatedClient(vampire);
                } else if (updated[i] <= 0 && old > 0) {
                    ((ILastingVampireSkill) SkillRegistry.getSkillFromId(i)).onDeactivated(vampire);//Called here if the skill is deactivated
                }

            }
        }
    }

    /**
     * Update the skills
     *
     * @return If a sync is recommend, only relevant on server side
     */
    boolean updateSkills() {
        for (int i = 0; i < skillTimer.length; i++) {
            int t = skillTimer[i];
            if (t != 0) {
                if (t < 0) {
                    skillTimer[i]++;
                } else {
                    skillTimer[i]--;
                    ILastingVampireSkill skill = (ILastingVampireSkill) SkillRegistry.getSkillFromId(i);
                    if (t == 1) {
                        skill.onDeactivated(vampire);//Called here if the skill runs out.
                            dirty = true;
                    } else {
                        if (skill.onUpdate(vampire)) {
                            skillTimer[i] = 1;
                        }
                    }

                }
            }
        }
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }


    public void deactivateAllSkills() {
        for (int i = 0; i < skillTimer.length; i++) {
            if (skillTimer[i] > 0) {
                skillTimer[i] = -SkillRegistry.getSkillFromId(i).getCooldown();
                ((ILastingVampireSkill) SkillRegistry.getSkillFromId(i)).onDeactivated(vampire);

            }
        }
    }


    @Override
    public float getPercentageForSkill(IVampireSkill skill) {
        Integer id = SkillRegistry.getIdFromSkill(skill);
        int i = skillTimer[id];
        if (i == 0) return 0F;
        if (i > 0) return i / ((float) ((ILastingVampireSkill) skill).getDuration(vampire.getLevel()));
        return i / (float) skill.getCooldown();
    }

    @Override
    public List<IVampireSkill> getAvailableSkills() {
        return SkillRegistry.getAvailableSkills(vampire);
    }

    @Override
    public IVampireSkill.PERM toggleSkill(IVampireSkill skill) {

        int id = SkillRegistry.getIdFromSkill(skill);
        int t = skillTimer[id];
        VampirismMod.log.t("Toggling skill %s with id %d at current time %d", skill, id, t);
        if (t > 0) {
            skillTimer[id] = Math.min((-skill.getCooldown()) + t, 0);
            ((ILastingVampireSkill) skill).onDeactivated(vampire);
            dirty = true;
            return IVampireSkill.PERM.ALLOWED;
        } else if (t == 0) {
            IVampireSkill.PERM r = skill.canUse(vampire);
            if (r == IVampireSkill.PERM.ALLOWED) {
                if (skill.onActivated(vampire)) {
                    if (skill instanceof ILastingVampireSkill) {
                        skillTimer[id] = ((ILastingVampireSkill) skill).getDuration(vampire.getLevel());
                    } else {
                        skillTimer[id] = -skill.getCooldown();
                    }
                    dirty = true;
                }

                return IVampireSkill.PERM.ALLOWED;
            } else {
                return r;
            }
        } else {
            return IVampireSkill.PERM.COOLDOWN;
        }

    }

    @Override
    public boolean isSkillActive(ILastingVampireSkill skill) {
        return skillTimer[SkillRegistry.getIdFromSkill(skill)] > 0;
    }

    @Override
    public boolean isSkillActive(String id) {
        IVampireSkill skill = SkillRegistry.getSkillFromKey(id);
        if (skill != null) {
            return isSkillActive((ILastingVampireSkill) skill);
        } else {
            VampirismMod.log.w(TAG, "Skill with id %s is not registered");
            return false;
        }

    }

    @Override
    public void resetTimers() {
        for (int i = 0; i < skillTimer.length; i++) {
            if (skillTimer[i] > 0) {
                ((ILastingVampireSkill) SkillRegistry.getSkillFromId(i)).onDeactivated(vampire);
            }
            skillTimer[i] = 0;
        }
    }
}
