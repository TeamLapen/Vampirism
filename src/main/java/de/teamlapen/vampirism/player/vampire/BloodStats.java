package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles VP's blood stats. Very similar to {@link FoodStats}
 */
public class BloodStats implements IBloodStats {
    private final static String TAG = "BloodStats";

    private int maxBlood = 20;

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
    public void setMaxBlood(int maxBlood) {
        this.maxBlood = Math.max(1, maxBlood);
        if (this.bloodLevel > maxBlood) {
            bloodLevel = maxBlood;
        }
        changed = true;
    }
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

    @Override
    public int addBlood(int amount, float saturationModifier) {
        int add = Math.min(amount, maxBlood - bloodLevel);
        bloodLevel += add;
        bloodSaturationLevel = Math.min(this.bloodSaturationLevel + (float) add * saturationModifier * 2.0F, (float) bloodLevel);
        changed = true;
        return amount - add;
    }


    @Override
    public boolean consumeBlood(int a) {
        int blood = getBloodLevel();
        int bloodToRemove = Math.min(a, blood);

        bloodLevel -= bloodToRemove;
        changed = true;
        return bloodToRemove <= blood;
    }

    @Override
    public int getBloodLevel() {
        return bloodLevel;
    }

    @Override
    public void setBloodLevel(int amt) {
        bloodLevel = amt < 0 ? 0 : (amt > maxBlood ? maxBlood : amt);
        changed = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
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
        } else if (regen && this.bloodLevel >= (maxBlood * 0.9f) && player.shouldHeal()) {
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
                    player.attackEntityFrom(DamageSource.STARVE, 1.0F);
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
            if (nbt.hasKey("maxBlood")) {
                maxBlood = nbt.getInteger("maxBlood");
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
        nbt.setInteger("maxBlood", maxBlood);
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
            mult = 1F;
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
        if (nbt.hasKey("maxBlood")) {
            setMaxBlood(nbt.getInteger("maxBlood"));
        }
        if (nbt.hasKey("bloodLevel")) {
            setBloodLevel(nbt.getInteger("bloodLevel"));
        }
    }


    NBTTagCompound writeUpdate(NBTTagCompound nbt) {
        nbt.setInteger("bloodLevel", bloodLevel);
        nbt.setInteger("maxBlood", maxBlood);
        return nbt;
    }


}
