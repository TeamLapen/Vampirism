package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HolyWaterOil extends WeaponOil {

    private final EnumStrength strength;

    public HolyWaterOil(int color, int maxDuration, EnumStrength strength) {
        super(color, maxDuration);
        this.strength = strength;
    }

    private EnumStrength getHolyWaterStrength() {
        return strength;
    }

    @Override
    public float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        EnumStrength strength = getHolyWaterStrength();
        double additionalDamage = VampirismConfig.BALANCE.holyWaterSplashDamage.get();
        switch (strength){
            case STRONG:
                additionalDamage *= VampirismConfig.BALANCE.holyWaterTierDamageInc.get();
            case MEDIUM:
                additionalDamage *= VampirismConfig.BALANCE.holyWaterTierDamageInc.get();
            case WEAK:
                break;
            default:
                additionalDamage = 0;

        }
        if (target instanceof PlayerEntity) {
            int l = VampirismPlayerAttributes.get((PlayerEntity) target).vampireLevel;
            additionalDamage = DamageHandler.scaleDamageWithLevel(l, REFERENCE.HIGHEST_VAMPIRE_LEVEL, amount * 0.8, amount * 1.3);
        } else if (target instanceof VampireBaronEntity) {
            int l = ((VampireBaronEntity) target).getLevel();
            additionalDamage = DamageHandler.scaleDamageWithLevel(l, VampireBaronEntity.MAX_LEVEL, amount * 0.8, amount * 2);
        }
        target.hurt(VReference.HOLY_WATER, (float) additionalDamage);
        return 0;
    }
}
