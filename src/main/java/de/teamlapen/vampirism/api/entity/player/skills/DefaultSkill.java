package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends ISkillPlayer> implements ISkill<T> {

    private final Map<IAttribute, AttributeModifier> attributeModifierMap = new HashMap<>();

    @Override
    public final void onDisable(T player) {
        removeAttributesModifiersFromEntity(player.getRepresentingPlayer());
        onDisabled(player);
    }

    @Override
    public final void onEnable(T player) {
        applyAttributesModifiersToEntity(player.getRepresentingPlayer());
        onEnabled(player);
    }

    public void removeAttributesModifiersFromEntity(EntityPlayer player) {
        for (Map.Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
            IAttributeInstance iattributeinstance = player.getAttributeMap().getAttributeInstance(entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.removeModifier(entry.getValue());
            }
        }
    }

    /**
     * @return Can be null
     */
    protected abstract List<IAction<T>> getActions();

    protected void onDisabled(T player) {
    }

    protected void onEnabled(T player) {
    }

    private void applyAttributesModifiersToEntity(EntityPlayer player) {
        for (Map.Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
            IAttributeInstance iattributeinstance = player.getAttributeMap().getAttributeInstance(entry.getKey());

            if (iattributeinstance != null) {
                AttributeModifier attributemodifier = entry.getValue();
                iattributeinstance.removeModifier(attributemodifier);
                iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getID(), attributemodifier.getAmount(), attributemodifier.getOperation()));
            }
        }
    }

    private void registerAttributeModifier(IAttribute attribute, String name, double amount, int operation) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(name), this.getID(), amount, operation);
        this.attributeModifierMap.put(attribute, attributemodifier);
    }


}
