package de.teamlapen.vampirism.api.entity.player.skills;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public abstract class DefaultSkill<T extends IFactionPlayer<T>> implements ISkill<T> {

    private final Map<Holder<Attribute>, List<AttributeHolder>> attributeModifierMap = new HashMap<>();
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
        player.getActionHandler().relockActions(getActions());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onDisabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }

    @Override
    public final void onEnable(@NotNull T player) {
        applyAttributesModifiersToEntity(player.asEntity());

        player.getActionHandler().unlockActions(getActions());
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            onEnabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }


    public @NotNull DefaultSkill<T> registerAttributeModifier(Holder<Attribute> attribute, double amount, AttributeModifier.@NotNull Operation operation) {
        return registerAttributeModifier(attribute, () -> amount, operation);
    }

    public @NotNull DefaultSkill<T> registerAttributeModifier(Holder<Attribute> attribute, @NotNull Supplier<Double> amountSupplier, AttributeModifier.@NotNull Operation operation) {
        List<AttributeHolder> holders = this.attributeModifierMap.computeIfAbsent(attribute, a -> new ArrayList<>());
        holders.add(new AttributeHolder(this, attribute, amountSupplier, operation, holders.size()));
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
        for (Map.Entry<Holder<Attribute>, List<AttributeHolder>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());

            if (instance != null) {
                for (AttributeHolder holder : entry.getValue()) {
                    instance.addOrReplacePermanentModifier(holder.create());
                }
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

    private void removeAttributesModifiersFromEntity(@NotNull Player player) {
        for (Map.Entry<Holder<Attribute>, List<AttributeHolder>> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance attribute = player.getAttribute(entry.getKey());

            if (attribute != null) {
                for (AttributeHolder holder : entry.getValue()) {
                    attribute.removeModifier(holder.getId());
                }
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

    protected static class AttributeHolder {
        public final Holder<Attribute> attribute;
        public final @NotNull Supplier<Double> amountSupplier;
        public final AttributeModifier.@NotNull Operation operation;
        private final ISkill<?> skill;
        private final int index;

        private AttributeHolder(ISkill<?> skill, Holder<Attribute> attribute, @NotNull Supplier<Double> amountSupplier, AttributeModifier.@NotNull Operation operation, int index) {
            this.attribute = attribute;
            this.amountSupplier = amountSupplier;
            this.operation = operation;
            this.skill = skill;
            this.index = index;
        }

        /**
         * The first modifier per attribute uses the skill id, further ones on the same attribute get a suffix to avoid id collisions
         */
        public ResourceLocation getId() {
            ResourceLocation id = VampirismRegistries.SKILL.get().getKey(this.skill);
            return this.index == 0 || id == null ? id : id.withSuffix("_" + this.index);
        }

        public AttributeModifier create() {
            return new AttributeModifier(this.getId(), this.amountSupplier.get(), this.operation);
        }
    }
}
