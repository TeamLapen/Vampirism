package de.teamlapen.vampirism.effects;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;


public class SanguinareEffect extends VampirismEffect {
    /**
     * @param entity
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(LivingEntity entity, boolean player) {
        int avgDuration = 20 * (player ? VampirismConfig.BALANCE.vpSanguinareAverageDuration.get() : BalanceMobProps.mobProps.SANGUINARE_AVG_DURATION);
        int duration = (int) ((entity.getRandom().nextFloat() + 0.5F) * avgDuration);
        EffectInstance effect = new SanguinareEffectInstance(duration);
        Preconditions.checkNotNull(effect);
        if (!VampirismConfig.BALANCE.canCancelSanguinare.get()) {
            effect.setCurativeItems(new ArrayList<>());
        }
        entity.addEffect(effect);

    }

    public SanguinareEffect(EffectType effectType, int potionColor) {
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
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level.isClientSide || !entity.isAlive()) return;
        if (entity instanceof CreatureEntity) {
            ExtendedCreature.getSafe(entity).ifPresent(IExtendedCreatureVampirism::makeVampire);
        }
        if (entity instanceof PlayerEntity) {
            VampirePlayer.getOpt((PlayerEntity) entity).ifPresent(VampirePlayer::onSanguinareFinished);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 2;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
        String s = UtilLib.translate(effect.getEffect().getDescriptionId());
        gui.font
                .drawShadow/*drawStringWithShadow*/
                (mStack, s, (float) (x + 10 + 18), (float) (y + 6), 16777215);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderInvText(EffectInstance effect) {
        return false;
    }
}
