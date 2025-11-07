package de.teamlapen.vampirism.common.entity.player.skills;

import de.teamlapen.sync.common.storage.ISyncableSaveData;
import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.common.mixin.accessor.AttributeInstanceAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefinementHandler<T extends IRefinementPlayer<T>> implements IRefinementHandler<T>, ISyncableSaveData {

    private static final String NBT_KEY = "refinement_handler";
    private final NonNullList<ItemStack> refinementItems = NonNullList.withSize(3, ItemStack.EMPTY);
    private final Set<Holder<IRefinement>> activeRefinements = new HashSet<>();
    private final Map<ResourceLocation, AttributeModifier> refinementModifier = new HashMap<>();
    private final T player;
    private final Holder<? extends IPlayableFaction<T>> faction;
    private boolean dirty = false;

    public RefinementHandler(T player, Holder<? extends IPlayableFaction<T>> faction) {
        this.player = player;
        this.faction = faction;
    }

    @Override
    public NonNullList<ItemStack> getRefinementItems() {
        return this.refinementItems;
    }

    @Override
    public void damageRefinements() {
        Registry<Enchantment> enchantments = this.player.asEntity().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> unbreaking = enchantments.getOrThrow(Enchantments.UNBREAKING);
        this.refinementItems.stream().filter(s -> !s.isEmpty()).forEach(stack -> {
            IRefinementSet set = ((IRefinementItem) stack.getItem()).getRefinementSet(stack);
            int damage = 40 + (set.getRarity().weight - 1) * 10 + this.player.asEntity().getRandom().nextInt(60);
            int unbreakingLevel = stack.getEnchantmentLevel(unbreaking);
            if (unbreakingLevel > 0) {
                damage = (int) (damage / (1f / (1.6f / (unbreakingLevel + 1f))));
            }
            stack.setDamageValue(stack.getDamageValue() + damage);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                stack.setCount(0);
            }
        });
    }

    @Override
    public boolean equipRefinementItem(@NotNull ItemStack stack) {
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            if (FactionRestriction.canUse(player.asEntity(), stack, false)) {
                IRefinementItem.AccessorySlotType setSlot = refinementItem.getSlotType();

                removeRefinementItem(setSlot);
                applyRefinementItem(stack, setSlot.getSlot());
                this.dirty = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeRefinementItem(IRefinementItem.@NotNull AccessorySlotType slot) {
        removeRefinementItem(slot.getSlot());
        this.dirty = true;
    }

    @Override
    public boolean isRefinementEquipped(Holder<IRefinement> refinement) {
        return this.activeRefinements.contains(refinement);
    }

    @Override
    public void resetRefinements() {
        for (int i = 0; i < this.refinementItems.size(); i++) {
            removeRefinementItem(i);
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
                    applyRefinementItem(itemSlot.stack(), itemSlot.slot());
                }
            }
        });
    }

    @Override
    public void deserializeUpdate(@NotNull ValueInput input) {
        input.list("refinement_items", ItemStackWithSlot.CODEC).stream().flatMap(ValueInput.TypedInputList::stream).forEach(itemSlot -> {
            if (itemSlot.stack().getItem() instanceof IRefinementItem) {
                applyRefinementItem(itemSlot.stack(), itemSlot.slot());
            }
        });
    }

    @Override
    public void serializeUpdateInternal(@NotNull ValueOutput output, @NotNull UpdateParams params) {
        var list = output.list("refinement_items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < this.refinementItems.size(); i++) {
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

    private void applyRefinementItem(@NotNull ItemStack stack, int slot) {
        this.refinementItems.set(slot, stack);
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            IRefinementSet set = refinementItem.getRefinementSet(stack);
            if (set != null) {
                set.getRefinements().forEach(x -> {
                    this.activeRefinements.add(x);
                    IRefinement refinement = x.value();
                    ResourceLocation key = x.unwrapKey().map(ResourceKey::location).orElseThrow();
                    if (!this.player.isRemote() && refinement.getAttribute() != null) {
                        AttributeInstance attributeInstance = this.player.asEntity().getAttribute(refinement.getAttribute());
                        double value = refinement.getModifierValue();
                        AttributeModifier t = attributeInstance.getModifier(key);
                        if (t != null) {
                            attributeInstance.removeModifier(key);
                            value += t.amount();
                        }
                        t = refinement.createAttributeModifier(value);
                        this.refinementModifier.put(key, t);
                        attributeInstance.addTransientModifier(t);
                    }
                });
            }
        }
    }

    private void removeRefinementItem(int slot) {
        ItemStack stack = this.refinementItems.get(slot);
        if (!stack.isEmpty()) {
            this.refinementItems.set(slot, ItemStack.EMPTY);
            if (stack.getItem() instanceof IRefinementItem refinementItem) {
                IRefinementSet set = refinementItem.getRefinementSet(stack);
                if (set != null) {
                    set.getRefinements().forEach(x -> {
                        this.activeRefinements.remove(x);
                        IRefinement refinement = x.value();
                        ResourceLocation key = x.unwrapKey().map(ResourceKey::location).orElseThrow();
                        if (!this.player.isRemote() && refinement.getAttribute() != null) {
                            AttributeInstance attributeInstance = this.player.asEntity().getAttribute(refinement.getAttribute());
                            AttributeModifier t = this.refinementModifier.remove(key);
                            if (t != null) {
                                ((AttributeInstanceAccessor) attributeInstance).invokeRemoveModifier(t);
                                double value = t.amount() - refinement.getModifierValue();
                                if (value != 0) {
                                    attributeInstance.addTransientModifier(t = refinement.createAttributeModifier(value));
                                    this.refinementModifier.put(key, t);
                                    this.activeRefinements.add(x);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
