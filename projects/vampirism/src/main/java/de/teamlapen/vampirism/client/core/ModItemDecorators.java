package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.IItemDecorator;
import org.joml.Matrix3x2fStack;

public class ModItemDecorators {

    private static final int DURABILITY_TIMER_SIZE = 6;
    private static final int DURABILITY_TIMER_MAX_SPRITE_INDEX = 6;

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

    public static final IItemDecorator DESTRUCTION_DEFERMENT = (graphics, font, stack, xOffset, yOffset) -> {
        Long deadline = stack.get(ModDataComponents.DESTRUCTION_DEFERRED_UNTIL);
        if (deadline == null || deadline == -1) return false;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        int seconds = (int) Math.ceil((deadline - level.getGameTime()) / 20.0);
        if (seconds <= 0) return false;

        float progress = 1.0f - seconds / (float) ModConfig.balance().hsDestructionDefermentDuration.get();
        int spriteId = Math.round(progress * DURABILITY_TIMER_MAX_SPRITE_INDEX);
        graphics.blit(RenderPipelines.GUI_TEXTURED, VIdentifier.mod("textures/gui/sprites/widget/durability_timer/durability_timer_" + spriteId + ".png"), xOffset + 16 - DURABILITY_TIMER_SIZE, yOffset + 16 - DURABILITY_TIMER_SIZE, 0, 0, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE);

        return false;
    };
}
