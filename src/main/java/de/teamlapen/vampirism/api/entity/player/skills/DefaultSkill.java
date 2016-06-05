package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends ISkillPlayer> implements ISkill<T> {

    private final Map<IAttribute, AttributeModifier> attributeModifierMap = new HashMap<>();
    private int renderRow;
    private int renderColumn;


    @Override
    public int getRenderColumn() {
        return renderColumn;
    }

    @Override
    public int getRenderRow() {
        return renderRow;
    }


    @Override
    public final void onDisable(T player) {
        removeAttributesModifiersFromEntity(player.getRepresentingPlayer());
        player.getActionHandler().ununlockActions(getActions());
        onDisabled(player);
    }

    @Override
    public final void onEnable(T player) {
        applyAttributesModifiersToEntity(player.getRepresentingPlayer());

        player.getActionHandler().unlockActions(getActions());
        onEnabled(player);
    }

    public DefaultSkill<T> registerAttributeModifier(IAttribute attribute, String uuid, double amount, int operation) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(uuid), this.getID(), amount, operation);
        this.attributeModifierMap.put(attribute, attributemodifier);
        return this;
    }

    @Override
    public void setRenderPos(int row, int column) {
        this.renderRow = row;
        this.renderColumn = column;
    }

    @Override
    public String toString() {
        return getID() + "(" + getClass().getSimpleName() + ")";
    }

    /**
     * Add actions that should be added to the list
     */
    protected void getActions(Collection<IAction<T>> list) {

    }

    /**
     * Called when the skill is being disabled.
     */
    protected void onDisabled(T player) {
    }

    /**
     * Called when the skill is being enabled
     */
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

    private Collection<IAction<T>> getActions() {
        Collection<IAction<T>> collection = new ArrayList<>();
        getActions(collection);
        return collection;
    }

    private void removeAttributesModifiersFromEntity(EntityPlayer player) {
        for (Map.Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
            IAttributeInstance iattributeinstance = player.getAttributeMap().getAttributeInstance(entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.removeModifier(entry.getValue());
            }
        }
    }
}
