package de.teamlapen.vampirism.api.entity.player.skills;

import com.google.common.base.Preconditions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Supplier;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends IFactionPlayer<T>> implements ISkill<T> {

    private final Map<Holder<Attribute>, AttributeHolder> attributeModifierMap = new HashMap<>();
    @Range(from = 0, to = 9)
    private final int skillPointCost;
    private String translationId;

    public DefaultSkill() {
        this(1);
    }

    public DefaultSkill(@Range(from = 0, to = 9) int skillPointCost) {
        this.skillPointCost = skillPointCost;
    }

    @Override
    public String getTranslationKey() {
        if (this.translationId == null) {
            this.translationId = Util.makeDescriptionId("skill", VampirismRegistries.SKILL.get().getKey(this));
        }
        return translationId;
    }

    @Override
    public final void onDisable(@NotNull T player) {
        removeAttributesModifiersFromEntity(player.asEntity());
        player.getActionHandler().relockActionHolder(getActionHolder());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onDisabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }

    @Override
    public final void onEnable(@NotNull T player) {
        applyAttributesModifiersToEntity(player.asEntity());

        player.getActionHandler().unlockActionHolder(getActionHolder());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onEnabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }


    public @NotNull DefaultSkill<T> registerAttributeModifier(Holder<Attribute> attribute, @NotNull String uuid, double amount, AttributeModifier.@NotNull Operation operation) {
        this.attributeModifierMap.put(attribute, new AttributeHolder(attribute, UUID.fromString(uuid), () -> amount, operation));
        return this;
    }

    public @NotNull DefaultSkill<T> registerAttributeModifier(Holder<Attribute> attribute, @NotNull String uuid, @NotNull Supplier<Double> amountSupplier, AttributeModifier.@NotNull Operation operation) {
        this.attributeModifierMap.put(attribute, new AttributeHolder(attribute, UUID.fromString(uuid), amountSupplier, operation));
        return this;
    }

    @Override
    public @NotNull String toString() {
        return getRegistryName() + "(" + getClass().getSimpleName() + ")";
    }

    /**
     * Add actions that should be added to the list
     */
    protected void getActions(Collection<IAction<T>> list) {

    }

    /**
     * Add actions that should be added to the list
     */
    protected void collectActions(Collection<Holder<? extends IAction<T>>> list) {

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

    private void applyAttributesModifiersToEntity(@NotNull Player player) {
        for (Map.Entry<Holder<Attribute>, AttributeHolder> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());

            if (instance != null) {
                instance.removeModifier(entry.getValue().uuid);
                instance.addPermanentModifier(entry.getValue().create());
            }
        }
    }

    public @NotNull Collection<IAction<T>> getActions() {
        Collection<IAction<T>> collection = new ArrayList<>();
        getActions(collection);
        collection.forEach((iAction -> {
            if (iAction.getFaction().isPresent() && iAction.getFaction().get() != this.getFaction().orElse(null)) {
                throw new IllegalArgumentException("Can't register action of faction " + iAction.getFaction().map(Object::toString).orElse(null) + " for skill of faction" + this.getFaction().map(Object::toString).orElse("all"));
            }
        }));
        return collection;
    }

    public @NotNull Collection<Holder<? extends IAction<T>>> getActionHolder() {
        Collection<Holder<? extends IAction<T>>> collection = new ArrayList<>();
        collectActions(collection);
        Preconditions.checkState(collection.stream().map(Holder::value).allMatch(h -> h.getFaction().isEmpty() || h.getFaction().get() == getFaction().orElse(null)), "Can't register action of faction for skill of faction");
        return collection;
    }

    private void removeAttributesModifiersFromEntity(@NotNull Player player) {
        for (Map.Entry<Holder<Attribute>, AttributeHolder> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance attribute = player.getAttribute(entry.getKey());

            if (attribute != null) {
                attribute.removeModifier(entry.getValue().uuid);
            }
        }
    }

    private @Nullable ResourceLocation getRegistryName() {
        return VampirismRegistries.SKILL.get().getKey(this);
    }

    @Range(from = 0, to = 9)
    @Override
    public int getSkillPointCost() {
        return this.skillPointCost;
    }

    protected class AttributeHolder {
        public final Holder<Attribute> attribute;
        public final @NotNull UUID uuid;
        public final @NotNull Supplier<Double> amountSupplier;
        public final AttributeModifier.@NotNull Operation operation;

        private AttributeHolder(Holder<Attribute> attribute, @NotNull UUID uuid, @NotNull Supplier<Double> amountSupplier, AttributeModifier.@NotNull Operation operation) {
            this.attribute = attribute;
            this.uuid = uuid;
            this.amountSupplier = amountSupplier;
            this.operation = operation;
        }

        public AttributeModifier create() {
            return new AttributeModifier(uuid, DefaultSkill.this.getRegistryName().toString(), amountSupplier.get(), operation);
        }
    }
}
