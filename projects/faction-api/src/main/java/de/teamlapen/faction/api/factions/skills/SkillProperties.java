package de.teamlapen.faction.api.factions.skills;

import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SkillProperties<T extends ISkillPlayer<T>> {

    private static final DependantName<ISkill<?>, String> NAME_ID = (id) -> Util.makeDescriptionId("skill", id.identifier());
    private static final DependantName<IAction<?>, String> ACTION_NAME_ID = (id) -> Util.makeDescriptionId("action", id.identifier());

    private DataComponentInitializers.Initializer<ISkill<T>> componentInitializer = (builder, context, id) -> {};
    private @Nullable ResourceKey<ISkill<?>> id;
    private @Nullable Map<Holder<Attribute>, SkillAttributeHolder> attributeModifier;
    @Nullable
    private ResourceKey<IAction<?>> action;
    private boolean addDefaultDescription;


    //<editor-fold desc="Value Getter">
    public SkillProperties<T> setId(ResourceKey<ISkill<?>> id) {
        this.id = id;
        return this;
    }

    public ResourceKey<ISkill<?>> skillIdOrThrow() {
        return Objects.requireNonNull(this.id, "Skill id not set");
    }

    public String effectiveNameId() {
        return this.action != null ? ACTION_NAME_ID.get(action) : NAME_ID.get(this.skillIdOrThrow());
    }

    public Map<Holder<Attribute>, SkillAttributeHolder> attributes() {
        return Objects.requireNonNullElseGet(this.attributeModifier, Map::of);
    }

    //</editor-fold>

    //<editor-fold desc="Additional">

    public SkillProperties<T> attribute(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        return this.attribute(attribute, () -> amount, operation);
    }

    public SkillProperties<T> attribute(Holder<Attribute> attribute, Supplier<Double> amountSupplier, AttributeModifier.Operation operation) {
        if (this.attributeModifier == null) {
            this.attributeModifier = new HashMap<>();
        }

        this.attributeModifier.put(attribute, new SkillAttributeHolder(skillIdOrThrow().identifier(), attribute, amountSupplier, operation));

        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Components">

    public SkillProperties<T> onEnable(Holder<FactionPlayerConsumer> consumer) {
        return component(FactionDataComponents.SKILL_ENABLE_CONSUMABLE, consumer);
    }

    public SkillProperties<T> onDisable(Holder<FactionPlayerConsumer> consumer) {
        return component(FactionDataComponents.SKILL_DISABLE_CONSUMABLE, consumer);
    }

    public SkillProperties<T> tree(ResourceKey<ISkillTree> key) {
        return component(FactionDataComponents.SKILL_TREE_REQUIREMENT, new SkillTreeRequirement(key));
    }

    public SkillProperties<T> tree(TagKey<ISkillTree> key) {
        return component(FactionDataComponents.SKILL_TREE_REQUIREMENT, new SkillTreeRequirement(key));
    }

    public SkillProperties<T> cost(int cost) {
        return component(FactionDataComponents.SKILL_COST, cost);
    }

    public SkillProperties<T> factions(TagKey<IFaction<?>> key) {
        return component(FactionDataComponents.SKILL_FACTIONS, key);
    }

    public SkillProperties<T> withDescription() {
        this.addDefaultDescription = true;
        return this;
    }

    public SkillProperties<T> withDescription(Component component) {
        return component(FactionDataComponents.SKILL_DESCRIPTION, component);
    }

    /**
     * mark the skill as representation of the action (using name, texture, etc...)
     */
    public SkillProperties<T> actionSkill(Holder<? extends IAction<T>> action) {
        //noinspection unchecked,RedundantCast
        this.action = (ResourceKey<IAction<?>>) (Object)action.getKey();
        this.component(FactionDataComponents.SKILL_ACTION, action);
        return unlocks(action);
    }

    /**
     * add action to be unlocked by the skill
     */
    public SkillProperties<T> unlocks(Holder<? extends IAction<T>> action) {
        this.componentInitializer.andThen((builder,_,_) -> {
            List<Holder<? extends IAction<?>>> actions = builder.get(FactionDataComponents.SKILL_ACTIONS);
            if (actions == null) {
                actions = List.of();
            }
            builder.set(FactionDataComponents.SKILL_ACTIONS, Stream.concat(actions.stream(), Stream.of(action)).toList());
        });
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Generic Components">

    public <Z> SkillProperties<T> component(Supplier<DataComponentType<Z>> type, Z value) {
        return component(type.get(), value);
    }

    public <Z> SkillProperties<T> component(DataComponentType<Z> type, Z value) {
        this.componentInitializer = this.componentInitializer.add(type, value);
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Finalizer">

    public DataComponentInitializers.Initializer<ISkill<T>> finalizeInitializer(Component name) {
        return this.componentInitializer
                .andThen((builder, _, key) -> {
                    if (!builder.has(FactionDataComponents.SKILL_FACTIONS)) {
                        throw new IllegalStateException("Skill factions are not set for: " + key);
                    }
                    if (this.addDefaultDescription) {
                        builder.set(FactionDataComponents.SKILL_DESCRIPTION, Component.translatable(effectiveNameId() + ".desc"));
                    }
                    if (!builder.has(FactionDataComponents.SKILL_TREE_REQUIREMENT)) {
                        builder.set(FactionDataComponents.SKILL_TREE_REQUIREMENT, new SkillTreeRequirement(FactionSkillTreeTags.DEFAULT));
                    }
                    if (!builder.has(FactionDataComponents.SKILL_DESCRIPTION) && builder.has(FactionDataComponents.SKILL_ACTION)) {
                        builder.set(FactionDataComponents.SKILL_DESCRIPTION, Component.translatable("gui.factionapi.skills.unlocks_action"));
                    }
                    builder.set(FactionDataComponents.SKILL_NAME, name);
                    Integer i = builder.get(FactionDataComponents.SKILL_COST);
                    if (i != null && i < 0) {
                        throw new IllegalStateException("Skill cost must be positive for: " + key);
                    }
                });
    }

    public SkillProperties<T> withValidator(Consumer<DataComponentMap> validator) {
        this.componentInitializer = this.componentInitializer.andThen((builder, _, _) -> builder.addValidator(validator));

        return this;
    }

    //</editor-fold>
}
