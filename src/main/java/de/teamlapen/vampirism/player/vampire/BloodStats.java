package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles VP's blood stats. Very similar to {@link FoodStats}
 */
public class BloodStats implements IBloodStats {
    private final static Logger LOGGER = LogManager.getLogger(BloodStats.class);
    private final EntityPlayer player;
    private int maxBlood = 20;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel;
    private int bloodTimer;
    private int prevBloodLevel = 20;
    private boolean changed = false;

    BloodStats(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int getBloodLevel() {
        return bloodLevel;
    }

    void setBloodLevel(int amt) {
        bloodLevel = amt < 0 ? 0 : (amt > maxBlood ? maxBlood : amt);
        changed = true;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    /**
     * Change the maximum storeable amount of blood
     * Also caps the current blood at this level
     *
     * @param maxBlood Should be a even number
     */
    void setMaxBlood(int maxBlood) {
        this.maxBlood = Math.max(1, maxBlood);
        if (this.bloodLevel > maxBlood) {
            bloodLevel = maxBlood;
        }
        changed = true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getPrevBloodLevel() {
        return prevBloodLevel;
    }

    @Override
    public boolean needsBlood() {
        return bloodLevel < maxBlood;
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
        float exhaustion = foodStats.foodExhaustionLevel;
        foodStats.foodExhaustionLevel = 0;
        addExhaustion(exhaustion);
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
        if (regen && this.bloodSaturationLevel > 0 && player.shouldHeal() && this.bloodLevel >= maxBlood) {
            ++this.bloodTimer;
            if (this.bloodTimer >= 10) {
                float f = Math.min(this.bloodSaturationLevel, 4F);
                player.heal(f / 4F);
                this.addExhaustion(f, true);
                this.bloodTimer = 0;
            }
        } else if (regen && this.bloodLevel >= (Balance.vp.BLOOD_HEALING_LEVEL) && player.shouldHeal()) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(2.8F, true);
                this.bloodTimer = 0;
            }
        } else if (this.bloodLevel <= 0) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                if (player.getHealth() > 10.0F || enumDifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumDifficulty == EnumDifficulty.NORMAL) {
                    player.attackEntityFrom(DamageSource.STARVE, 1.5F);
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
        if (nbt.contains("bloodLevel")) {
            bloodLevel = nbt.getInt("bloodLevel");
            if (nbt.contains("bloodTimer")) {
                bloodTimer = nbt.getInt("bloodTimer");
                bloodSaturationLevel = nbt.getFloat("bloodSaturation");
                bloodExhaustionLevel = nbt.getFloat("bloodExhaustion");
            }
            if (nbt.contains("maxBlood")) {
                maxBlood = nbt.getInt("maxBlood");
            }
        }
    }

    int addBlood(int amount, float saturationModifier) {
        int add = Math.min(amount, maxBlood - bloodLevel);
        bloodLevel += add;
        bloodSaturationLevel = Math.min(this.bloodSaturationLevel + (float) add * saturationModifier * 2.0F, (float) bloodLevel);
        changed = true;
        return amount - add;
    }

    /**
     * Add exhaustion. Value is multiplied with the EntityAttribute {@link VReference#bloodExhaustion}
     *
     * @param amount
     */
    void addExhaustion(float amount) {
        this.addExhaustion(amount, false);
    }

    /**
     * Add exhaustion
     *
     * @param amount
     * @param ignoreModifier If the entity exhaustion attribute {@link VReference#bloodExhaustion} should be ignored
     */
    void addExhaustion(float amount, boolean ignoreModifier) {
        IAttributeInstance attribute = player.getAttribute(VReference.bloodExhaustion);
        float mult;
        if (ignoreModifier) {
            mult = 1F;
        } else {
            if (attribute == null) {
                //Probably not needed anymore TODO remove
                LOGGER.warn("Blood exhaustion attribute is null for player %s (%s)", player, player == null ? null : player.getAttributeMap());
                mult = (float) VReference.bloodExhaustion.getDefaultValue();
            } else {
                mult = (float) attribute.getValue();
            }
        }

        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount * mult, 40F);
    }

    void loadUpdate(NBTTagCompound nbt) {
        if (nbt.contains("maxBlood")) {
            setMaxBlood(nbt.getInt("maxBlood"));
        }
        if (nbt.contains("bloodLevel")) {
            setBloodLevel(nbt.getInt("bloodLevel"));
        }
    }

    boolean removeBlood(int a, boolean allowPartial) {
        if (bloodLevel >= a) {
            bloodLevel -= a;
            changed = true;
            return true;
        } else if (allowPartial) {
            bloodLevel = 0; //a is larger than the blood level, so use up as much as possible
        }
        return false;
    }

    /**
     * Write all relevant data to nbt
     *
     * @param nbt
     */
    void writeNBT(NBTTagCompound nbt) {
        writeNBTBlood(nbt);
        nbt.putInt("bloodTimer", bloodTimer);
        nbt.putFloat("bloodSaturation", bloodSaturationLevel);
        nbt.putFloat("bloodExhaustion", bloodExhaustionLevel);
        nbt.putInt("maxBlood", maxBlood);
    }

    /**
     * Write only the blood level to nbt
     *
     * @param nbt
     */
    void writeNBTBlood(NBTTagCompound nbt) {
        nbt.putInt("bloodLevel", bloodLevel);
    }

    NBTTagCompound writeUpdate(NBTTagCompound nbt) {
        nbt.putInt("bloodLevel", bloodLevel);
        nbt.putInt("maxBlood", maxBlood);
        return nbt;
    }


}
