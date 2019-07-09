package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;


public class PotionThirst extends VampirismPotion {
    public PotionThirst(String name, boolean badEffect, int potionColor) {
        super(name, badEffect, potionColor);
        setIconIndex(0, 0);
        addAttributesModifier(VReference.bloodExhaustion, "f6d9889e-dfdc-11e5-b86d-9a79f06e9478", 0.5F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            VampirePlayer.get((PlayerEntity) entity).addExhaustion(0.005F * (amplifier + 1));
        }
    }
}
