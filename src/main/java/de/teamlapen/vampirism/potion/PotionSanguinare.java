package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;


public class PotionSanguinare extends VampirismPotion {
    /**
     * @param entity
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(LivingEntity entity, boolean player) {
        int avgDuration = 20 * (player ? Balance.vp.SANGUINARE_AVG_DURATION : Balance.mobProps.SANGUINARE_AVG_DURATION);
        int duration = (int) ((entity.getRNG().nextFloat() + 0.5F) * avgDuration);
        EffectInstance effect = new PotionSanguinareEffect(ModPotions.sanguinare, duration);
        if (!Balance.general.CAN_CANCEL_SANGUINARE) {
            effect.setCurativeItems(new ArrayList<>());
        }
        entity.addPotionEffect(effect);

    }

    public PotionSanguinare(String name, boolean badEffect, int potionColor) {
        super(name, badEffect, potionColor);
        setIconIndex(1, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "22663B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
    }

    @Override
    public boolean isReady(int duration, int p_76397_2_) {
        return duration == 2;
    }

    @Override
    public void performEffect(LivingEntity entity, int p_76394_2_) {

        if (entity instanceof CreatureEntity) {
            IExtendedCreatureVampirism creature = ExtendedCreature.get((CreatureEntity) entity);
            creature.makeVampire();
        }
        if (entity instanceof PlayerEntity) {
            VampirePlayer.get((PlayerEntity) entity).onSanguinareFinished();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderInventoryEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z) {
//        https://github.com/MinecraftForge/MinecraftForge/issues/2473
        String s1 = I18n.format(getName());

        Minecraft.getInstance().fontRenderer.drawStringWithShadow(s1, (float) (x + 10 + 18), (float) (y + 6), 16777215);
        String s = "Unknown";
        Minecraft.getInstance().fontRenderer.drawStringWithShadow(s, (float) (x + 10 + 18), (float) (y + 6 + 10), 8355711);

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderInvText(EffectInstance effect) {
        return false;
    }
}
