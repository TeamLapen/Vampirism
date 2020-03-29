package de.teamlapen.vampirism.potion;


import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.EffectType;

public class PotionFreeze extends VampirismPotion {
    public PotionFreeze(String name) {
        super(name, EffectType.HARMFUL, 0xFFFFFF);
        this.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "ae1402d5-64b2-400a-ac1e-6c3a87a64305", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }


    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
