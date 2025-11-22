package de.teamlapen.vampirism.common.entity.player.vampire;

import com.mojang.serialization.Codec;
import de.teamlapen.sync.PropertySync;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAttributes;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.items.consume.BloodFoodProperties;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.util.BloodResourceHandler;
import de.teamlapen.vampirism.misc.mixin.accessor.FoodDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles VP's blood stats. Very similar to {@link FoodData}
 */
public class BloodStats extends PropertySync implements IBloodStats, BloodResourceHandler {
    private final VampirePlayer factionPlayer;
    private final Player player;
    private int maxBlood = 20;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel;
    private int bloodTimer;
    private int prevBloodLevel = 20;
    private boolean changed = false;
    private final SnapshotJournal<Integer> bloodJournal = new BloodJournal();

    BloodStats(VampirePlayer player) {
        this.player = player.asEntity();
        this.factionPlayer = player;
    }

    @Override
    public void sync() {
        this.factionPlayer.sync();
    }

    @Override
    public int getBloodLevel() {
        return bloodLevel;
    }

    void setBloodLevel(int amt) {
        bloodLevel = amt < 0 ? 0 : (Math.min(amt, maxBlood));
        changed = true;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    /**
     * Change the maximum storable amount of blood
     * Also caps the current blood at this level
     *
     * @param maxBlood Should be an even number
     */
    void setMaxBlood(int maxBlood) {
        this.maxBlood = Math.max(1, maxBlood);
        if (this.bloodLevel > maxBlood) {
            bloodLevel = maxBlood;
        }
        changed = true;
    }

    @Override
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
     */
    public void onUpdate() {
        FoodData foodStats = player.getFoodData();
        foodStats.setFoodLevel(10);
        Difficulty enumDifficulty = player.level().getDifficulty();
        float exhaustion = ((FoodDataAccessor)foodStats).getExhaustionLevel();
        ((FoodDataAccessor)foodStats).setExhaustionLevel(0);
        addExhaustion(exhaustion);
        this.prevBloodLevel = bloodLevel;
        float bloodExhaustionGate = player.level().getBiome(player.blockPosition()).is(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME) ? 6f : 4f;
        if (this.bloodExhaustionLevel > bloodExhaustionGate) {
            this.bloodExhaustionLevel -= bloodExhaustionGate;
            if (bloodSaturationLevel > 0) {
                bloodSaturationLevel = Math.max(bloodSaturationLevel - 1F, 0F);
            } else if (enumDifficulty != Difficulty.PEACEFUL || ModConfig.BALANCE.vpBloodUsagePeaceful.get()) {
                this.bloodLevel = Math.max(bloodLevel - 1, 0);
            }
        }
        if (player.level() instanceof ServerLevel level) {
            boolean regen = level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
            if (regen && this.bloodSaturationLevel > 0 && player.isHurt() && this.bloodLevel >= maxBlood) {
                ++this.bloodTimer;
                if (this.bloodTimer >= 10) {
                    float f = Math.min(this.bloodSaturationLevel, 6F);
                    player.heal(f / 6F);
                    this.addExhaustion(f);
                    this.bloodTimer = 0;
                }
            } else if (regen && this.bloodLevel >= (18) && player.isHurt()) {
                ++this.bloodTimer;

                if (this.bloodTimer >= 80) {
                    player.heal(1.0F);
                    this.addExhaustion(6F);
                    this.bloodTimer = 0;
                }
            } else if (this.bloodLevel <= 0) {
                ++this.bloodTimer;

                if (this.bloodTimer >= 80) {
                    if (player.getHealth() > 10.0F || enumDifficulty == Difficulty.HARD || player.getHealth() > 1.0F && enumDifficulty == Difficulty.NORMAL) {
                        this.player.addEffect(new MobEffectInstance(ModEffects.NO_BLOOD, 150, 0));
                    }

                    this.bloodTimer = 0;
                }
            } else {
                this.bloodTimer = 0;
            }
        }
        if (this.prevBloodLevel != this.bloodLevel) {
            this.changed = true;
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
     * Add exhaustion. Value is multiplied with the EntityAttribute {@link ModAttributes#BLOOD_EXHAUSTION}
     */
    void addExhaustion(float amount) {
        this.addExhaustion(amount, false);
    }

    /**
     * Add exhaustion
     *
     * @param ignoreModifier If the entity exhaustion attribute {@link ModAttributes#BLOOD_EXHAUSTION} should be ignored
     */
    void addExhaustion(float amount, @SuppressWarnings("SameParameterValue") boolean ignoreModifier) {
        if (!ignoreModifier) {
            amount *= (float) player.getAttributeValue(ModAttributes.BLOOD_EXHAUSTION);
        }
        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount, 40F);
    }

    public void eat(BloodFoodProperties bloodFoodProperties) {
        this.addBlood(bloodFoodProperties.blood(), bloodFoodProperties.saturation());
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VResourceLocation.mod("blood_level"), 0, () -> this.bloodLevel, level -> bloodLevel = level, true);
        this.registerProperty(VResourceLocation.mod("blood_timer"), 0, () -> this.bloodTimer, level -> bloodTimer = level, true);
        this.registerProperty(VResourceLocation.mod("blood_saturation"), 0F, () -> this.bloodSaturationLevel, level -> bloodSaturationLevel = level, true);
        this.registerProperty(VResourceLocation.mod("blood_exhaustion"), 0F, () -> this.bloodExhaustionLevel, level -> bloodExhaustionLevel = level, true);
        this.registerProperty(VResourceLocation.mod("max_blood"), 20, () -> this.maxBlood, level -> maxBlood = level, true);
    }

    boolean removeBlood(int a, boolean allowPartial) {
        if (bloodLevel >= a) {
            bloodLevel -= a;
            changed = true;
            return true;
        } else if (allowPartial) {
            bloodLevel = 0; //an is larger than the blood level, so use up as much as possible
            changed = true;
        }
        return false;
    }

    @Override
    public int getAmount() {
        return this.bloodLevel;
    }

    @Override
    public int addBlood(int amount) {
        this.bloodLevel += amount;
        return amount;
    }

    @Override
    public int extractBlood(int amount) {
        return 0;
    }

    @Override
    public int getCapacity() {
        return this.maxBlood;
    }

    @Override
    public SnapshotJournal<Integer> getJournal() {
        return this.bloodJournal;
    }

    private class BloodJournal extends SnapshotJournal<Integer> {
        @Override
        protected Integer createSnapshot() {
            return bloodLevel;
        }

        @Override
        protected void revertToSnapshot(@Nullable Integer snapshot) {
            if (snapshot == null)  {
                bloodLevel = 0;
                return;
            }
            bloodLevel = snapshot;
        }

        @Override
        protected void onRootCommit(@Nullable Integer originalState) {
            changed = true;
        }
    }
}
