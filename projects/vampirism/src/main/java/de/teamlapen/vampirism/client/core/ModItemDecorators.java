package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import net.neoforged.neoforge.client.IItemDecorator;
import org.joml.Matrix3x2fStack;

public class ModItemDecorators {

    public static final IItemDecorator CROSSBOW_AMMUNITION = (graphics, font, stack, xOffset, yOffset) -> {
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
}
