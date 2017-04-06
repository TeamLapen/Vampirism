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

import java.lang.reflect.Field;

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
    /**
     * Caches an accessor to {@link FoodStats#foodExhaustionLevel}
     */
    private Field field_foodExhaustionLevel = null;

    public BloodStats(EntityPlayer player) {
        this.player = player;
    }

    /**
     * Adds blood to the stats
     * Consider using {@link VampirePlayer#drinkBlood(int, float)} instead
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
        EnumDifficulty enumDifficulty = player.getEntityWorld().getDifficulty();
        float exhaustion;
        try {
            if (field_foodExhaustionLevel == null)
            {
                field_foodExhaustionLevel = ReflectionHelper.findField(FoodStats.class, "foodExhaustionLevel", SRGNAMES.FoodStats_foodExhaustionLevel);
            }
            exhaustion = (float) field_foodExhaustionLevel.get(foodStats);
            addExhaustion(exhaustion);
            field_foodExhaustionLevel.set(foodStats, 0);
        }
        catch (Exception e)
        {
            VampirismMod.log.e(TAG, e, "Failed to access foodExhaustionLevel (%s)", SRGNAMES.FoodStats_foodExhaustionLevel);
            throw new RuntimeException(e);
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
        boolean regen = player.getEntityWorld().getGameRules().getBoolean("naturalRegeneration");
        if (regen && this.bloodSaturationLevel > 0 && player.shouldHeal() && this.bloodLevel >= 20) {
            ++this.bloodTimer;
            if (this.bloodTimer >= 10) {
                float f = Math.min(this.bloodSaturationLevel, 4F);
                player.heal(f / 4F);
                this.addExhaustion(f, true);
                this.bloodTimer = 0;
            }
        } else if (regen && this.bloodLevel >= 18 && player.shouldHeal()) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(3.0F, true);
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

    /**
     * Add exhaustion. Value is multiplied with the EntityAttribute {@link VReference#bloodExhaustion}
     *
     * @param amount
     */
    protected void addExhaustion(float amount) {
        this.addExhaustion(amount, false);
    }

    /**
     * Add exhaustion
     *
     * @param amount
     * @param ignoreModifier If the entity exhaustion attribute {@link VReference#bloodExhaustion} should be ignored
     */
    protected void addExhaustion(float amount, boolean ignoreModifier) {
        VampirePlayer.get(player).checkAttributes(VReference.bloodExhaustion);
        IAttributeInstance attribute = player.getEntityAttribute(VReference.bloodExhaustion);
        float mult;
        if (ignoreModifier) {
            mult= 1F;
        } else {
            if (attribute == null) {
                //Probably not needed anymore TODO remove
                VampirismMod.log.w(TAG, "Blood exhaustion attribute is null for player %s (%s)", player, player == null ? null : player.getAttributeMap());
                mult = (float) VReference.bloodExhaustion.getDefaultValue();
            } else {
                mult = (float) attribute.getAttributeValue();
            }
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
