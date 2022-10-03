package de.teamlapen.vampirism.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.effects.IHiddenEffectInstance;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
    public void initializeClient(@NotNull Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return !(instance instanceof IHiddenEffectInstance);
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) {
                return !(instance instanceof IHiddenEffectInstance);
            }

            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
                return true;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, PoseStack poseStack, int x, int y, float z, float alpha) {
                return true;
            }
        });
    }


}
