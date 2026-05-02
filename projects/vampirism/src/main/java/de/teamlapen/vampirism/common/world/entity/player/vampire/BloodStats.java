package de.teamlapen.vampirism.common.world.entity.player.vampire;

import de.teamlapen.sync.PropertySync;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAttributes;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.util.BloodResourceHandler;
import de.teamlapen.vampirism.misc.mixin.accessor.FoodDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
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
            } else if (enumDifficulty != Difficulty.PEACEFUL || ModConfig.balance().vpBloodUsagePeaceful.get()) {
                this.bloodLevel = Math.max(bloodLevel - 1, 0);
            }
        }
        if (player.level() instanceof ServerLevel level) {
            boolean regen = level.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION);
            if (regen && this.bloodSaturationLevel > 0 && player.isHurt() && this.bloodLevel >= maxBlood) {
                ++this.bloodTimer;
                if (this.bloodTimer >= 10) {
                    float f = Math.min(this.bloodSaturationLevel, 6F);
                    player.heal((f / 6F) * getHealModifier());
                    this.addExhaustion(f);
                    this.bloodTimer = 0;
                }
            } else if (regen && this.bloodLevel > 0 && player.isHurt()) {
                ++this.bloodTimer;

                boolean betterHeal = this.bloodLevel >= (18) && this.bloodTimer >= 80;
                boolean heal = this.bloodTimer >= 300;
                if (betterHeal || heal) {
                    player.heal(betterHeal ? getHealModifier() : 0.5F * getHealModifier());
                    this.addExhaustion(betterHeal ? 6F : 3F);
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

    private float getHealModifier() {
        VampirePlayer vampire = VampirePlayer.get(this.player);
        return 1 + (vampire.getLevel() / (float) vampire.getMaxLevel()) * 0.5f;
    }

    public int addBlood(int amount, float saturationModifier) {
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

    public void eat(FoodProperties foodProperties) {
        this.addBlood(foodProperties.nutrition(), foodProperties.saturation());
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VIdentifier.mod("blood_level")).simple(0, () -> this.bloodLevel, level -> this.bloodLevel = level);
        this.registerProperty(VIdentifier.mod("blood_timer")).simple(0, () -> this.bloodTimer, level -> this.bloodTimer = level);
        this.registerProperty(VIdentifier.mod("blood_saturation")).simple(0F, () -> this.bloodSaturationLevel, level -> this.bloodSaturationLevel = level);
        this.registerProperty(VIdentifier.mod("blood_exhaustion")).simple(0F, () -> this.bloodExhaustionLevel, level -> this.bloodExhaustionLevel = level);
        this.registerProperty(VIdentifier.mod("max_blood")).simple(20, () -> this.maxBlood, level -> this.maxBlood = level);
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
