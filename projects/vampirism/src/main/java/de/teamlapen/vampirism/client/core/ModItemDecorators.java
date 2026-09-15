package de.teamlapen.vampirism.client.core;

import de.teamlapen.faction.api.client.ItemBar;
import de.teamlapen.faction.api.client.ItemBarProvider;
import de.teamlapen.faction.client.core.ItemBars;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.client.config.ClientConfig;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.items.VampireSwordItem;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import net.neoforged.neoforge.client.IItemDecorator;
import org.joml.Matrix3x2fStack;

public class ModItemDecorators {

    public static final IItemDecorator CROSSBOW_AMMUNITION = (graphics, _, stack, xOffset, yOffset) -> {
        ((IHunterCrossbow) stack.getItem()).getAmmunition(stack).ifPresent(ammo -> {
            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(xOffset, yOffset + 8);
            poseStack.scale(0.5f);
            graphics.item(ammo.getDefaultInstance(), 0, 0);
            poseStack.popMatrix();
        });

        return false;
    };

    public static final ItemBarProvider APPLIED_OIL = (stack, bars) -> {
        if (!itemBarsEnabled()) return;

        AppliedOilContent oil = AppliedOilContent.getAppliedOil(stack).orElse(null);
        if (oil == null || !oil.oil().value().hasDuration() || oil.duration() <= 0) return;
        int maxDuration = oil.oil().value().getMaxDuration(stack);
        if (maxDuration <= 0) return;

        float duration = (float) oil.duration() / maxDuration;
        bars.accept(new ItemBar(duration, ItemBars.staged(ModConfig.client().appliedOilColor.getARGB(), duration)));
    };

    public static final ItemBarProvider BLOOD_CHARGE = (stack, bars) -> {
        if (!itemBarsEnabled()) return;
        if (!(stack.getItem() instanceof IBloodChargeable chargeable)) return;

        float charge = chargeable.getChargePercentage(stack);
        bars.accept(new ItemBar(charge, ItemBars.staged(ModConfig.client().vampireSwordChargeColor.getARGB(), charge)));

        if (stack.getItem() instanceof VampireSwordItem sword) {
            float trained = sword.getTrained(stack);
            if (trained > 0 && trained < 1) {
                bars.accept(new ItemBar(trained, ItemBars.staged(ModConfig.client().vampireSwordTrainingColor.getARGB(), trained)));
            }
        }
    };

    private static boolean itemBarsEnabled() {
        return ModConfig.client().chargeBarDisplayType.get() == ClientConfig.ChargeBarDisplay.ITEM;
    }
}
