package de.teamlapen.vampirism.common.entity.player.skills;

import de.teamlapen.lib.util.collections.SetView;
import de.teamlapen.sync.common.storage.ISyncableSaveData;
import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RefinementHandler<T extends IRefinementPlayer<T>> implements IRefinementHandler<T>, ISyncableSaveData {

    private static final String NBT_KEY = "refinement_handler";
    private final NonNullList<ItemStack> refinementItems = NonNullList.withSize(IRefinementItem.AccessorySlotType.values().length, ItemStack.EMPTY);
    private final Map<IRefinementItem.AccessorySlotType, Set<Holder<IRefinement>>> refinementSets = new HashMap<>();

    private final Set<Holder<IRefinement>> activeRefinements = new SetView<>(refinementSets);
    private final T player;
    private boolean dirty = false;

    public RefinementHandler(T player, Holder<? extends IPlayableFaction<T>> faction) {
        this.player = player;
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
        this.dirty = true;
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
        this.dirty = true;

        return true;
    }

    @Override
    public void removeRefinement(IRefinementItem.@NotNull AccessorySlotType slot) {
        ItemStack stack = refinementItems.remove(slot.getSlot());
        if (stack.isEmpty()) return;

        Set<Holder<IRefinement>> remove = refinementSets.remove(slot);
        if (remove == null) {
            remove = Set.of();
        }

        remove.stream().filter(x -> !activeRefinements.contains(x)).forEach(this::removeAttributes);
        this.dirty = true;
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
    public void resetRefinements() {
        for (IRefinementItem.AccessorySlotType slot : IRefinementItem.AccessorySlotType.values()) {
            removeRefinement(slot);
        }
        this.refinementItems.clear();
        this.dirty = true;
    }

    @Override
    public void reset() {
        resetRefinements();
    }

    @Override
    public void serialize(@NotNull ValueOutput output) {
        var list = output.list("refinement_items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < this.refinementItems.size(); i++) {
            if (this.refinementItems.get(i).isEmpty()) continue;
            list.add(new ItemStackWithSlot(i, this.refinementItems.get(i)));
        }
    }

    @Override
    public void deserialize(@NotNull ValueInput input) {
        var list = input.listOrEmpty("refinement_items", ItemStackWithSlot.CODEC);
        this.refinementItems.clear();
        list.stream().forEach(itemSlot -> {
            if (itemSlot.stack().getItem() instanceof IRefinementItem) {
                if (FactionRestriction.canUse(player.asEntity(), itemSlot.stack(), false)) {
                    equipRefinement(itemSlot.stack());
                }
            }
        });
    }

    @Override
    public void deserializeUpdate(@NotNull ValueInput input) {
        input.list("refinement_items", ItemStackWithSlot.CODEC).stream().flatMap(ValueInput.TypedInputList::stream).forEach(itemSlot -> {
            if (itemSlot.stack().getItem() instanceof IRefinementItem) {
                equipRefinement(itemSlot.stack());
            }
        });
    }

    @Override
    public void serializeUpdateInternal(@NotNull ValueOutput output, @NotNull UpdateParams params) {
        var list = output.list("refinement_items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < this.refinementItems.size(); i++) {
            if (this.refinementItems.get(i).isEmpty()) continue;
            list.add(new ItemStackWithSlot(i, this.refinementItems.get(i)));
        }
    }

    @Override
    public boolean needsUpdate() {
        return this.dirty;
    }

    @Override
    public void updateSend() {
        this.dirty = false;
    }

    @Override
    public @NotNull String nbtKey() {
        return NBT_KEY;
    }
}
