package de.teamlapen.vampirism.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.EffectRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends MobEffect {

    private final static Logger LOGGER = LogManager.getLogger(VampirismNightVisionPotion.class);

    public VampirismNightVisionPotion() {
        super(MobEffectCategory.BENEFICIAL, 2039713);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<EffectRenderer> consumer) {
        consumer.accept(new EffectRenderer() {
            @Override
            public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {

            }

            @Override
            public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {

            }

            @Override
            public boolean shouldRender(MobEffectInstance effect) {
                return !(effect instanceof VampireNightVisionEffectInstance) && super.shouldRender(effect);
            }

            @Override
            public boolean shouldRenderHUD(MobEffectInstance effect) {
                return !(effect instanceof VampireNightVisionEffectInstance) && super.shouldRender(effect);
            }
        });
    }


}
