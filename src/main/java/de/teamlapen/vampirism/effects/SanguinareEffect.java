package de.teamlapen.vampirism.effects;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.EffectRenderer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class SanguinareEffect extends VampirismEffect {
    /**
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(LivingEntity entity, boolean player) {
        int avgDuration = 20 * (player ? VampirismConfig.BALANCE.vpSanguinareAverageDuration.get() : BalanceMobProps.mobProps.SANGUINARE_AVG_DURATION);
        int duration = (int) ((entity.getRandom().nextFloat() + 0.5F) * avgDuration);
        MobEffectInstance effect = new SanguinareEffectInstance(duration);
        Preconditions.checkNotNull(effect);
        if (!VampirismConfig.BALANCE.canCancelSanguinare.get()) {
            effect.setCurativeItems(new ArrayList<>());
        }
        entity.addEffect(effect);

    }

    public SanguinareEffect(MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "22663B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> l = super.getCurativeItems();
        l.add(new ItemStack(ModItems.GARLIC_BREAD.get()));
        return l;
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        if (entity.level.isClientSide || !entity.isAlive()) return;
        if (entity instanceof PathfinderMob) {
            ExtendedCreature.getSafe(entity).ifPresent(IExtendedCreatureVampirism::makeVampire);
        }
        if (entity instanceof Player) {
            VampirePlayer.getOpt((Player) entity).ifPresent(VampirePlayer::onSanguinareFinished);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 2;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<EffectRenderer> consumer) {
        consumer.accept(new EffectRenderer() {
            @OnlyIn(Dist.CLIENT)
            @Override
            public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {
                String s = UtilLib.translate(effect.getEffect().getDescriptionId());
                gui.font.drawShadow
                        (mStack, s, (float) (x + 10 + 18), (float) (y + 6), 16777215);
            }

            @Override
            public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {

            }

            @OnlyIn(Dist.CLIENT)
            @Override
            public boolean shouldRenderInvText(MobEffectInstance effect) {
                return false;
            }
        });
    }

}
