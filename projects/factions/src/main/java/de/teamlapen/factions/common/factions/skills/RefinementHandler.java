package de.teamlapen.factions.common.factions.skills;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.refinements.IRefinement;
import de.teamlapen.factions.api.factions.refinements.IRefinementHandler;
import de.teamlapen.factions.api.factions.refinements.IRefinementPlayer;
import de.teamlapen.factions.api.factions.refinements.IRefinementSet;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.world.items.IRefinementItem;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.util.collections.SetView;
import de.teamlapen.sync.PropertySync;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RefinementHandler<T extends IRefinementPlayer<T>> extends PropertySync implements IRefinementHandler<T> {

    private final NonNullList<ItemStack> refinementItems = NonNullList.withSize(IRefinementItem.AccessorySlotType.values().length, ItemStack.EMPTY);
    private final Map<IRefinementItem.AccessorySlotType, Set<Holder<IRefinement>>> refinementSets = new HashMap<>();

    private final Set<Holder<IRefinement>> activeRefinements = new SetView<>(refinementSets);
    private final T player;

    public RefinementHandler(T player, Holder<? extends IPlayableFaction<T>> faction) {
        this.player = player;
    }

    @Override
    public void sync() {
        this.player.sync();
    }

    @Override
    public NonNullList<ItemStack> getRefinementItems() {
        return this.refinementItems;
    }

    @Override
    public void damageRefinements() {
        Registry<Enchantment> enchantments = this.player.asEntity().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> unbreaking = enchantments.getOrThrow(Enchantments.UNBREAKING);
        for (IRefinementItem.AccessorySlotType slot : IRefinementItem.AccessorySlotType.values()) {
            ItemStack stack = refinementItems.get(slot.getSlot());
            if (stack.isEmpty()) continue;

            IRefinementSet set = ((IRefinementItem) stack.getItem()).getRefinementSet(stack);
            int damage;
            if (set == null) {
                damage = stack.getMaxDamage();
            } else {
                damage = 40 + (set.getRarity().weight - 1) * 10 + this.player.asEntity().getRandom().nextInt(60);
            }
            int unbreakingLevel = stack.getEnchantmentLevel(unbreaking);
            if (unbreakingLevel > 0) {
                damage = (int) (damage / (1f / (1.6f / (unbreakingLevel + 1f))));
            }
            stack.setDamageValue(stack.getDamageValue() + damage);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                removeRefinement(slot);
                stack.setCount(0);
            }
        }
        sync();
    }

    @Override
    public boolean equipRefinement(ItemStack stack) {
        if (!(stack.getItem() instanceof IRefinementItem refinementItem)) return false;
        if (!FactionRestriction.canUse(player.asEntity(), stack, false)) return false;

        var slotType = refinementItem.getSlotType();
        IRefinementSet refinementSet = refinementItem.getRefinementSet(stack);
        Set<Holder<IRefinement>> refinements;
        if (refinementSet == null) {
            refinements = Set.of();
        } else {
            refinements = refinementSet.getRefinements();
        }

        removeRefinement(slotType);

        refinementSets.put(slotType, refinements);
        refinements.forEach(this::addAttributes);
        sync();

        return true;
    }

    @Override
    public void removeRefinement(IRefinementItem.AccessorySlotType slot) {
        ItemStack stack = refinementItems.set(slot.getSlot(), ItemStack.EMPTY);
        if (stack.isEmpty()) return;


        Set<Holder<IRefinement>> remove = refinementSets.remove(slot);
        if (remove == null) {
            remove = Set.of();
        }

        remove.stream().filter(x -> !activeRefinements.contains(x)).forEach(this::removeAttributes);
        this.sync();
    }

    private void addAttributes(Holder<IRefinement> refinement) {
        Holder<Attribute> attribute = refinement.value().getAttribute();
        if (attribute == null) return;

        var instance = this.player.asEntity().getAttribute(attribute);
        if (instance == null) return;
        var factory = refinement.value().attributeFactory();
        if (factory == null) return;
        AttributeModifier attributeModifier = factory.apply(refinement.unwrapKey().orElseThrow().location(), refinement.value().getModifierValue());
        if (attributeModifier == null) return;

        instance.addTransientModifier(attributeModifier);
    }

    private void removeAttributes(Holder<IRefinement> refinement) {
        Holder<Attribute> attribute = refinement.value().getAttribute();
        if (attribute == null) return;

        AttributeInstance instance = this.player.asEntity().getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(refinement.unwrapKey().orElseThrow().location());
    }

    @Override
    public boolean isRefinementEquipped(Holder<IRefinement> refinement) {
        return this.activeRefinements.contains(refinement);
    }

    @Override
    public void updateItems() {
        for (IRefinementItem.AccessorySlotType value : IRefinementItem.AccessorySlotType.values()) {
            var refinements = this.refinementSets.remove(value);
            if (refinements != null) {
                refinements.forEach(this::removeAttributes);
            }
        }

        for (ItemStack refinementItem : this.refinementItems) {
            if (refinementItem.getItem() instanceof IRefinementItem refinement) {
                IRefinementItem.AccessorySlotType slotType = refinement.getSlotType();
                IRefinementSet refinementSet = refinement.getRefinementSet(refinementItem);
                Set<Holder<IRefinement>> refinements;
                if (refinementSet == null) {
                    refinements = Set.of();
                } else {
                    refinements = refinementSet.getRefinements();
                }
                refinementSets.put(slotType, refinements);
                refinements.forEach(this::addAttributes);
            }
        }
        sync();
    }

    @Override
    public void resetRefinements() {
        for (IRefinementItem.AccessorySlotType slot : IRefinementItem.AccessorySlotType.values()) {
            removeRefinement(slot);
        }
        this.refinementItems.clear();
        this.sync();
    }

    @Override
    public void reset() {
        resetRefinements();
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(FResourceLocation.mod("refinement_items")).list(ItemStackWithSlot.CODEC).provider(() -> {
            ArrayList<ItemStackWithSlot> itemStacks = new ArrayList<>(this.refinementItems.size());
            for (int i = 0; i < refinementItems.size(); i++) {
                var stack = refinementItems.get(i);
                if (stack.isEmpty()) continue;
                itemStacks.add(new ItemStackWithSlot(i, stack));
            }
            return itemStacks;
        }).commonLoader(x -> {
            Map<Integer, ItemStack> updatedItems = x.stream().collect(Collectors.toMap(ItemStackWithSlot::slot, ItemStackWithSlot::stack));
            for (int i = 0; i < refinementItems.size(); i++) {
                refinementItems.set(i, updatedItems.getOrDefault(i, ItemStack.EMPTY));
            }
            return true;
        }).register();
    }

}
