package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends IFactionPlayer<T>> implements ISkill<T> {

    private final Map<Attribute, LazyOptional<AttributeModifier>> attributeModifierMap = new HashMap<>();
    private int renderRow;
    private int renderColumn;
    private Component name;

    @Override
    public Component getName() {
        return name == null ? name = Component.translatable(getTranslationKey()) : name;
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
    public final void onDisable(T player) {
        removeAttributesModifiersFromEntity(player.getRepresentingPlayer());
        player.getActionHandler().relockActions(getActions());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onDisabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }

    @Override
    public final void onEnable(T player) {
        applyAttributesModifiersToEntity(player.getRepresentingPlayer());

        player.getActionHandler().unlockActions(getActions());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onEnabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
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

    private void applyAttributesModifiersToEntity(Player player) {
        for (Map.Entry<Attribute, LazyOptional<AttributeModifier>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());

            if (instance != null) {
                AttributeModifier attributemodifier = entry.getValue().orElseThrow(IllegalStateException::new);
                instance.removeModifier(attributemodifier);
                instance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getRegistryName().toString(), attributemodifier.getAmount(), attributemodifier.getOperation()));
            }
        }
    }

    private Collection<IAction<T>> getActions() {
        Collection<IAction<T>> collection = new ArrayList<>();
        getActions(collection);
        collection.forEach((iAction -> {
            if (iAction.getFaction().isPresent() && iAction.getFaction().get() != this.getFaction().orElse(null))
                throw new IllegalArgumentException("Can't register action of faction " + iAction.getFaction().map(Object::toString).orElse(null) + " for skill of faction" + this.getFaction().map(Object::toString).orElse("all"));
            }));
        return collection;
    }

    private void removeAttributesModifiersFromEntity(Player player) {
        for (Map.Entry<Attribute, LazyOptional<AttributeModifier>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance attribute = player.getAttribute(entry.getKey());

            if (attribute != null) {
                attribute.removeModifier(entry.getValue().orElseThrow(IllegalStateException::new));
            }
        }
    }

    private ResourceLocation getRegistryName() {
        return VampirismRegistries.SKILLS.get().getKey(this);
    }


}
