package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.*;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of ISkill. Handles entity modifiers and actions
 */
public class Skill<T extends IFactionPlayer<T> & ISkillPlayer<T>> implements ISkill<T> {

    private final String descriptionId;
    private final Holder.Reference<ISkill<?>> builtInRegistryHolder;

    private final Map<Holder<Attribute>, SkillAttributeHolder> attributeModifierMap;

    public Skill(SkillProperties<T> properties) {
        this.builtInRegistryHolder = ModRegistries.SKILLS.createIntrusiveHolder(this);
        this.descriptionId = properties.effectiveNameId();
        this.attributeModifierMap = properties.attributes();

        //noinspection unchecked
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add((ResourceKey<ISkill<T>>) (Object) properties.skillIdOrThrow(), properties.finalizeInitializer(Component.translatable(properties.effectiveNameId())));
    }

    public DataComponentMap components() {
        return this.builtInRegistryHolder.components();
    }

    @Override
    public Component getName() {
        return this.components().getOrDefault(FactionDataComponents.SKILL_NAME, Component.empty());
    }

    @Override
    public @Nullable Component getDescription() {
        return this.components().get(FactionDataComponents.SKILL_DESCRIPTION);
    }

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return this.components().getOrDefault(FactionDataComponents.SKILL_FACTIONS, FactionTags.ALL_FACTIONS);
    }

    @Override
    public String getDescriptionId() {
        return this.descriptionId;
    }

    @Override
    public final void onDisable(T player) {
        removeAttributesModifiersFromEntity(player.asEntity());
        player.getActionHandler().relockActionHolder(getActions());
        if (IFaction.is(player.getFaction(), this.factions())) {
            onDisabled(player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " for action " + FactionRegistries.SKILL.get().getKey(this));
        }
    }

    @Override
    public final void onEnable(T player) {
        applyAttributesModifiersToEntity(player.asEntity());

        if (IFaction.is(player.getFaction(), this.factions())) {
            player.getActionHandler().unlockActionHolder(getActions());
            onEnabled(player);
        } else {
            throw new IllegalArgumentException("skill is not allowed for player for skill " + FactionRegistries.SKILL.get().getKey(this));
        }
    }

    /**
     * Called when the skill is being disabled.
     */
    protected void onDisabled(T player) {
        var consumer = components().get(FactionDataComponents.SKILL_DISABLE_CONSUMABLE);
        if (consumer != null) {
            consumer.value().accept(player);
        }
    }

    /**
     * Called when the skill is being enabled
     */
    protected void onEnabled(T player) {
        var consumer = components().get(FactionDataComponents.SKILL_ENABLE_CONSUMABLE);
        if (consumer != null) {
            consumer.value().accept(player);
        }
    }

    @Override
    public Holder<? extends IAction<T>> getAction() {
        //noinspection unchecked
        return (Holder<? extends IAction<T>>) this.components().get(FactionDataComponents.SKILL_ACTION);
    }

    @Override
    public List<Holder<? extends IAction<T>>> getActions() {
        //noinspection unchecked
        return (List<Holder<? extends IAction<T>>>) this.components().getOrDefault(FactionDataComponents.SKILL_ACTIONS, List.of());
    }

    private void applyAttributesModifiersToEntity(Player player) {
        for (Map.Entry<Holder<Attribute>, SkillAttributeHolder> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());

            if (instance != null) {
                AttributeModifier attributeModifier = entry.getValue().create();
                instance.addOrReplacePermanentModifier(attributeModifier);
            }
        }
    }

    private void removeAttributesModifiersFromEntity(Player player) {
        for (Map.Entry<Holder<Attribute>, SkillAttributeHolder> entry : this.attributeModifierMap.entrySet()) {
            AttributeInstance attribute = player.getAttribute(entry.getKey());

            if (attribute != null) {
                attribute.removeModifier(entry.getValue().skillId());
            }
        }
    }

    @Override
    public int getSkillPointCost() {
        return this.components().getOrDefault(FactionDataComponents.SKILL_COST, 0);
    }

    @Override
    public SkillTreeRequirement allowedSkillTrees() {
        return Objects.requireNonNull(this.components().get(FactionDataComponents.SKILL_TREE_REQUIREMENT));
    }
}
