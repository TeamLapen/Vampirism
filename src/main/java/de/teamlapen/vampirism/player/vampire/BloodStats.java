package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles VP's blood stats. Very similar to {@link FoodStats}
 * TODO maybe make a api interface for this
 */
public class BloodStats {
    public static final float LOW_SATURATION = 0.3F;
    public static final float MEDIUM_SATURATION = 0.7F;
    public static final float HIGH_SATURATION = 1.0F;
    private final static String TAG = "BloodStats";
    protected final int MAXBLOOD = 20;
    private final EntityPlayer player;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel;
    private int bloodTimer;
    private int prevBloodLevel = 20;
    private boolean changed = false;

    public BloodStats(EntityPlayer player) {
        this.player = player;
    }

    /**
     * Adds blood to the stats
     * Consider using {@link VampirePlayer#consumeBlood(int, float)} instead
     * @param amount
     * @param saturationModifier
     * @return The amount which could not be added
     */
    public int addBlood(int amount, float saturationModifier) {
        int add = Math.min(amount, MAXBLOOD - bloodLevel);
        bloodLevel += add;
        bloodSaturationLevel = Math.min(this.bloodSaturationLevel + (float) add * saturationModifier * 2.0F, (float) bloodLevel);
        changed = true;
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
        changed = true;
        return bloodToRemove <= blood;
    }

    public int getBloodLevel() {
        return bloodLevel;
    }

    public void setBloodLevel(int amt) {
        bloodLevel = amt < 0 ? 0 : (amt > 20 ? 20 : amt);
        changed = true;
    }

    @SideOnly(Side.CLIENT)
    public int getPrevBloodLevel() {
        return prevBloodLevel;
    }

    public boolean needsBlood() {
        return bloodLevel < MAXBLOOD;
    }

    /**
     * Updated the blood level
     * Only call this if the player is a vampire
     *
     * @return Whether it changed or not
     */
    public boolean onUpdate() {
        FoodStats foodStats = player.getFoodStats();
        foodStats.setFoodLevel(10);
        EnumDifficulty enumDifficulty = player.worldObj.getDifficulty();
        float e;
        try {
            e = ReflectionHelper.getPrivateValue(FoodStats.class, foodStats, "foodExhaustionLevel", SRGNAMES.FoodStats_foodExhaustionLevel);
            addExhaustion(e);
            ReflectionHelper.setPrivateValue(FoodStats.class, foodStats, 0, "foodExhaustionLevel", SRGNAMES.FoodStats_foodExhaustionLevel);
        } catch (Exception e1) {
            VampirismMod.log.e(TAG, e1, "Failed to access foodExhaustionLevel");
            throw e1;
        }
        this.prevBloodLevel = bloodLevel;
        if (this.bloodExhaustionLevel > 4.0F) {
            this.bloodExhaustionLevel -= 4.0F;
            if (bloodSaturationLevel > 0) {
                bloodSaturationLevel = Math.max(bloodSaturationLevel - 1F, 0F);
            } else if (enumDifficulty != EnumDifficulty.PEACEFUL || Balance.vp.BLOOD_USAGE_PEACEFUL) {
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
        } else if (this.bloodLevel <= 0) {
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
        if (changed || this.prevBloodLevel != this.bloodLevel) {
            changed = false;
            return true;
        }
        return false;
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

    /**
     * Write only the blood level to nbt
     *
     * @param nbt
     */
    public void writeNBTBlood(NBTTagCompound nbt) {
        nbt.setInteger("bloodLevel", bloodLevel);
    }

    protected void addExhaustion(float amount) {
        IAttributeInstance attribute = player.getEntityAttribute(VReference.bloodExhaustion);
        float mult;
        if (attribute == null) {
            VampirismMod.log.w(TAG, "Blood exhaustion attribute is null");
            mult = (float) VReference.bloodExhaustion.getDefaultValue();
        } else {
            mult = (float) attribute.getAttributeValue();
        }
        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount * mult, 40F);
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

}
