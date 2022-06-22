package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles VP's blood stats. Very similar to {@link FoodStats}
 */
public class BloodStats implements IBloodStats {
    private final static Logger LOGGER = LogManager.getLogger(BloodStats.class);
    private final PlayerEntity player;
    private int maxBlood = 20;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel;
    private int bloodTimer;
    private int prevBloodLevel = 20;
    private boolean changed = false;

    BloodStats(PlayerEntity player) {
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
        FoodStats foodStats = player.getFoodData();
        foodStats.setFoodLevel(10);
        Difficulty enumDifficulty = player.getCommandSenderWorld().getDifficulty();
        float exhaustion = foodStats.exhaustionLevel;
        foodStats.exhaustionLevel = 0;
        addExhaustion(exhaustion);
        this.prevBloodLevel = bloodLevel;
        float bloodExhaustionGate = player.getCommandSenderWorld().getBiomeName(player.blockPosition()).filter(key -> key == ModBiomes.VAMPIRE_FOREST_KEY).map(key -> 6f).orElse(4f); //TODO 1.18 use biome tag
        if (this.bloodExhaustionLevel > bloodExhaustionGate) {
            this.bloodExhaustionLevel -= bloodExhaustionGate;
            if (bloodSaturationLevel > 0) {
                bloodSaturationLevel = Math.max(bloodSaturationLevel - 1F, 0F);
            } else if (enumDifficulty != Difficulty.PEACEFUL || VampirismConfig.BALANCE.vpBloodUsagePeaceful.get()) {
                this.bloodLevel = Math.max(bloodLevel - 1, 0);
            }
        }
        boolean regen = player.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (regen && this.bloodSaturationLevel > 0 && player.isHurt() && this.bloodLevel >= maxBlood) {
            ++this.bloodTimer;
            if (this.bloodTimer >= 10) {
                float f = Math.min(this.bloodSaturationLevel, 4F);
                player.heal(f / 4F);
                this.addExhaustion(f, true);
                this.bloodTimer = 0;
            }
        } else if (regen && this.bloodLevel >= (18) && player.isHurt()) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(2.8F, true);
                this.bloodTimer = 0;
            }
        } else if (this.bloodLevel <= 0) {
            ++this.bloodTimer;

            if (this.bloodTimer >= 80) {
                if (player.getHealth() > 10.0F || enumDifficulty == Difficulty.HARD || player.getHealth() > 1.0F && enumDifficulty == Difficulty.NORMAL) {
                    player.hurt(DamageSource.STARVE, 1.5F);
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
     * Reads nbt written by either {@link #writeNBTBlood(CompoundNBT)} or {@link #writeNBT(CompoundNBT)}
     *
     * @param nbt
     */
    public void readNBT(CompoundNBT nbt) {
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
     * Add exhaustion. Value is multiplied with the EntityAttribute {@link de.teamlapen.vampirism.core.ModAttributes#BLOOD_EXHAUSTION}
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
     * @param ignoreModifier If the entity exhaustion attribute {@link de.teamlapen.vampirism.core.ModAttributes#BLOOD_EXHAUSTION} should be ignored
     */
    void addExhaustion(float amount, boolean ignoreModifier) {
        if  (!ignoreModifier) {
            ModifiableAttributeInstance attribute = player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get());
            amount *= attribute.getValue();
        }
        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount, 40F);
    }

    void loadUpdate(CompoundNBT nbt) {
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
            changed = true;
        }
        return false;
    }

    /**
     * Write all relevant data to nbt
     *
     * @param nbt
     */
    void writeNBT(CompoundNBT nbt) {
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
    void writeNBTBlood(CompoundNBT nbt) {
        nbt.putInt("bloodLevel", bloodLevel);
    }

    CompoundNBT writeUpdate(CompoundNBT nbt) {
        nbt.putInt("bloodLevel", bloodLevel);
        nbt.putInt("maxBlood", maxBlood);
        return nbt;
    }


}
