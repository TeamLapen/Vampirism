package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.IItemDecorator;
import org.joml.Matrix3x2fStack;

import java.util.stream.IntStream;

public class ModItemDecorators {

    private static final int DURABILITY_TIMER_SIZE = 6;
    private static final int DURABILITY_TIMER_MAX_SPRITE_INDEX = 6;
    private static final Identifier[] DURABILITY_TIMER_SPRITES = IntStream.rangeClosed(0, DURABILITY_TIMER_MAX_SPRITE_INDEX)
            .mapToObj(i -> VIdentifier.mod("textures/gui/sprites/widget/durability_timer/durability_timer_" + i + ".png"))
            .toArray(Identifier[]::new);
    private static final Identifier DURABILITY_TIMER_OVER = VIdentifier.mod("textures/gui/sprites/widget/durability_timer/durability_timer_over.png");

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
        Long deadline = stack.get(ModDataComponents.DETRITION_DEFERRED_UNTIL);
        if (deadline == null) return false;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        Identifier sprite;

        int seconds = (int) Math.ceil((deadline - level.getGameTime()) / 20.0);
        if (seconds > 0) {
            float progress = 1.0f - seconds / (float) ModConfig.balance().hsDetritionDefermentDuration.get();
            sprite = DURABILITY_TIMER_SPRITES[Mth.clamp(Math.round(progress * DURABILITY_TIMER_MAX_SPRITE_INDEX), 0, DURABILITY_TIMER_MAX_SPRITE_INDEX)];
        } else {
            sprite = DURABILITY_TIMER_OVER;
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, xOffset + 16 - DURABILITY_TIMER_SIZE, yOffset + 16 - DURABILITY_TIMER_SIZE, 0, 0, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE, DURABILITY_TIMER_SIZE);

        return false;
    };
}
