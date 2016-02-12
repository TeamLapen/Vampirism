package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles VP's blood stats. Very similar to {@link FoodStats}
 * TODO maybe make a api interface for this
 */
public class BloodStats {
    public static final float LOW_SATURATION = 0.3F;
    public static final float MEDIUM_SATURATION = 0.7F;
    public static final float HIGH_SATURATION = 1.0F;
    private final int MAXBLOOD = 20;
    private final EntityPlayer player;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel;
    private int bloodTimer;
    private int prevBloodLevel = 20;
    private Map<String, Float> modifiers = new HashMap<String, Float>();
    private float modifier;

    public BloodStats(EntityPlayer player) {
        this.player = player;
        addExhaustionModifier("config", (float) Balance.vp.BLOOD_EXHAUSTION_MOD);
    }


    /**
     * Reads nbt written by either {@link #writeNBTBlood(NBTTagCompound)} or {@link #writeNBT(NBTTagCompound)}
     *
     * @param nbt
     */
    public void readNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("bloodLevel")) {
            bloodLevel = nbt.getInteger("bloodLevel");
            if (nbt.hasKey("bloodTimer")) {
                bloodTimer = nbt.getInteger("bloodTimer");
                bloodSaturationLevel = nbt.getFloat("bloodSaturation");
                bloodExhaustionLevel = nbt.getFloat("bloodExhaustion");
            }
        }
    }

    /**
     * Write all relevant data to nbt
     *
     * @param nbt
     */
    public void writeNBT(NBTTagCompound nbt) {
        writeNBTBlood(nbt);
        nbt.setInteger("bloodTimer", bloodTimer);
        nbt.setFloat("bloodSaturation", bloodSaturationLevel);
        nbt.setFloat("bloodExhaustion", bloodExhaustionLevel);
    }

    void loadUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey("bloodLevel")) {
            setBloodLevel(nbt.getInteger("bloodLevel"));
        }
    }

    NBTTagCompound writeUpdate(NBTTagCompound nbt) {
        nbt.setInteger("bloodLevel", bloodLevel);
        return nbt;
    }

    /**
     * Write only the blood level to nbt
     *
     * @param nbt
     */
    public void writeNBTBlood(NBTTagCompound nbt) {
        nbt.setInteger("bloodLevel", bloodLevel);
    }

    /**
     * Add an exhaustion modifier.
     * TODO APIfy maybe
     *
     * @param id  ID to remove it later
     * @param mod Exhaustion is multiplied with this
     */
    public void addExhaustionModifier(String id, float mod) {
        modifiers.put(id, mod);
        updateExhaustionModifier();
    }

    public void removeExhaustionModifier(String id) {
        modifiers.remove(id);
        updateExhaustionModifier();
    }

    private void updateExhaustionModifier() {
        modifier = 1.0F;
        for (Float f : modifiers.values()) {
            modifier *= f;
        }
    }

    /**
     * Updated the blood level
     *
     * @return Whether it changed or not
     */
    public boolean onUpdate() {
        EnumDifficulty enumDifficulty = player.worldObj.getDifficulty();
        this.prevBloodLevel = bloodLevel;
        if (this.bloodExhaustionLevel > 4.0F) {
            this.bloodExhaustionLevel -= 4.0F;
            if (bloodSaturationLevel > 0) {
                bloodSaturationLevel = Math.max(bloodSaturationLevel - 1F, 0F);
            } else if (enumDifficulty != EnumDifficulty.PEACEFUL) {
                this.bloodLevel = Math.max(bloodLevel - 1, 0);
            }
        }
        if (player.worldObj.getGameRules().getBoolean("naturalRegeneration") && this.bloodLevel >= 18 && player.shouldHeal()) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(3.0F);
                this.bloodTimer = 0;
            }
        } else if (this.bloodTimer <= 0) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                if (player.getHealth() > 10.0F || enumDifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumDifficulty == EnumDifficulty.NORMAL) {
                    player.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.bloodTimer = 0;
            }
        } else {
            this.bloodTimer = 0;
        }
        return this.prevBloodLevel != this.bloodLevel;
    }

    /**
     * Adds blood to the stats
     *
     * @param amount
     * @param saturationModifier
     * @return The amound which could not be added
     */
    public int addBlood(int amount, float saturationModifier) {
        int add = Math.min(amount, MAXBLOOD - bloodLevel);
        bloodLevel += add;
        bloodSaturationLevel = Math.min(this.bloodSaturationLevel + (float) add * saturationModifier * 2.0F, (float) bloodLevel);
        return amount - add;
    }

    /**
     * Removes blood from the vampires blood level
     *
     * @param a amount
     * @return whether the vampire had enough blood or not
     */
    public boolean consumeBlood(int a) {
        int blood = getBloodLevel();
        int bloodToRemove = Math.min(a, blood);

        bloodLevel -= bloodToRemove;
        return bloodToRemove <= blood;
    }

    public int getBloodLevel() {
        return bloodLevel;
    }

    public void setBloodLevel(int amt) {
        bloodLevel = amt < 0 ? 0 : (amt > 20 ? 20 : amt);
    }

    public void addExhaustion(float amount) {
        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount * modifier, 40F);
    }

    @SideOnly(Side.CLIENT)
    public int getPrevBloodLevel() {
        return prevBloodLevel;
    }

}
