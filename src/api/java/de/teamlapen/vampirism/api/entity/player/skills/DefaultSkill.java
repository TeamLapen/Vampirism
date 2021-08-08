package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends IFactionPlayer> extends ForgeRegistryEntry<ISkill> implements ISkill {

    private final Map<Attribute, LazyOptional<AttributeModifier>> attributeModifierMap = new HashMap<>();
    private int renderRow;
    private int renderColumn;
    private Component name;

    @Override
    public Component getName() {
        return name == null ? name = new TranslatableComponent(getTranslationKey()) : name;
    }

    public DefaultSkill<T> setName(Component name) {
        this.name = name;
        return this;
    }

    @Override
    public int getRenderColumn() {
        return renderColumn;
    }

    @Override
    public int getRenderRow() {
        return renderRow;
    }

    @Deprecated
    @Override
    public String getTranslationKey() {
        return "skill." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }

    @Override
    public final void onDisable(IFactionPlayer player) {
        removeAttributesModifiersFromEntity(player.getRepresentingPlayer());
        player.getActionHandler().relockActions(getActions());
        if (this.getFaction().getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            onDisabled((T) player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().getFactionPlayerInterface());
        }
    }

    @Override
    public final void onEnable(IFactionPlayer player) {
        applyAttributesModifiersToEntity(player.getRepresentingPlayer());

        player.getActionHandler().unlockActions(getActions());
        if (this.getFaction().getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            onEnabled((T) player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().getFactionPlayerInterface());
        }
    }


    public DefaultSkill<T> registerAttributeModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
        final AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(uuid), this.getRegistryName().toString(), amount, operation);
        this.attributeModifierMap.put(attribute, LazyOptional.of(() -> attributemodifier));
        return this;
    }

    public DefaultSkill<T> registerAttributeModifier(Attribute attribute, String uuid, Supplier<Double> amountSupplier, AttributeModifier.Operation operation) {
        this.attributeModifierMap.put(attribute, LazyOptional.of(() -> new AttributeModifier(UUID.fromString(uuid), this.getRegistryName().toString(), amountSupplier.get(), operation)));
        return this;
    }

    @Override
    public void setRenderPos(int row, int column) {
        this.renderRow = row;
        this.renderColumn = column;
    }

    @Override
    public String toString() {
        return getRegistryName() + "(" + getClass().getSimpleName() + ")";
    }

    /**
     * Add actions that should be added to the list
     */
    protected void getActions(Collection<IAction> list) {

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

    private void applyAttributesModifiersToEntity(Player player) {
        for (Map.Entry<Attribute, LazyOptional<AttributeModifier>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance iattributeinstance = player.getAttribute(entry.getKey());

            if (iattributeinstance != null) {
                AttributeModifier attributemodifier = entry.getValue().orElseThrow(IllegalStateException::new);
                iattributeinstance.removeModifier(attributemodifier);
                iattributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getRegistryName().toString(), attributemodifier.getAmount(), attributemodifier.getOperation()));
            }
        }
    }

    private Collection<IAction> getActions() {
        Collection<IAction> collection = new ArrayList<>();
        getActions(collection);
        collection.forEach((iAction -> {
            if (!iAction.getFaction().equals(this.getFaction())) {
                throw new IllegalArgumentException("Can't register action of faction " + iAction.getFaction() + " for skill of faction" + this.getFaction());
            }
        }));
        return collection;
    }

    private void removeAttributesModifiersFromEntity(Player player) {
        for (Map.Entry<Attribute, LazyOptional<AttributeModifier>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance iattributeinstance = player.getAttribute(entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.removeModifier(entry.getValue().orElseThrow(IllegalStateException::new));
            }
        }
    }


}
