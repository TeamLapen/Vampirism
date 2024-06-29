package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.lib.lib.storage.ISyncableSaveData;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.mixin.accessor.AttributeInstanceAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefinementHandler<T extends IRefinementPlayer<T>> implements IRefinementHandler<T>, ISyncableSaveData {

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
        Registry<Enchantment> enchantments = this.player.asEntity().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> unbreaking = enchantments.getHolderOrThrow(Enchantments.UNBREAKING);
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
            if (this.faction.equals(refinementItem.getExclusiveFaction(stack))) {
                @Nullable IRefinementSet newSet = refinementItem.getRefinementSet(stack);
                IRefinementItem.AccessorySlotType setSlot = refinementItem.getSlotType();

                removeRefinementItem(setSlot);
                this.dirty = true;

                applyRefinementItem(stack, setSlot.getSlot());
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
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag refinements = new ListTag();
        for (int i = 0; i < this.refinementItems.size(); i++) {
            ItemStack stack = this.refinementItems.get(i);
            if (!stack.isEmpty()) {
                CompoundTag stackNbt = new CompoundTag();
                stackNbt.putInt("slot", i);
                var tag = stack.save(provider);
                stackNbt.put("stack", tag);
                refinements.add(stackNbt);
            }
        }
        nbt.put("refinement_items", refinements);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("refinement_items")) {
            ListTag refinements = nbt.getList("refinement_items", 10);
            for (int i = 0; i < refinements.size(); i++) {
                CompoundTag stackNbt = refinements.getCompound(i);
                int slot = stackNbt.getInt("slot");
                ItemStack stack = ItemStack.parseOptional(provider, stackNbt.getCompound("stack"));
                if (stack.getItem() instanceof IRefinementItem refinementItem) {
                    TagKey<IFaction<?>> exclusiveFaction = refinementItem.getExclusiveFaction(stack);
                    if (IFaction.is(this.faction, exclusiveFaction)) {
                        applyRefinementItem(stack, slot);
                    }
                }
            }
        }
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("refinement_items", Tag.TAG_LIST)) {
            ListTag refinements = nbt.getList("refinement_items", Tag.TAG_LIST);
            for (int i = 0; i < refinements.size(); i++) {
                CompoundTag stackNbt = refinements.getCompound(i);
                int slot = stackNbt.getInt("slot");
                ItemStack stack = ItemStack.parseOptional(provider, stackNbt.getCompound("stack"));
                if (stack.getItem() instanceof IRefinementItem refinementItem) {
                    TagKey<IFaction<?>> exclusiveFaction = refinementItem.getExclusiveFaction(stack);
                    if (IFaction.is(this.faction, exclusiveFaction)) {
                        applyRefinementItem(stack, slot);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider) {
        if (!this.dirty) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        ListTag refinementItems = new ListTag();
        for (int i = 0; i < this.refinementItems.size(); i++) {
            ItemStack stack = this.refinementItems.get(i);
            if (!stack.isEmpty()) {
                CompoundTag stackNbt = new CompoundTag();
                stackNbt.putInt("slot", i);
                var tag = stack.save(provider);
                stackNbt.put("stack", tag);
                refinementItems.add(stackNbt);
            }
        }
        nbt.put("refinement_items", refinementItems);
        return nbt;
    }

    @Override
    public String nbtKey() {
        return "";
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
                            ((AttributeInstanceAccessor) attributeInstance).invoke_removeModifier(t);
                            double value = t.amount() - refinement.getModifierValue();
                            if (value != 0) {
                                attributeInstance.addTransientModifier(t = refinement.createAttributeModifier(value));
                                this.refinementModifier.put(key, t);
                                this.activeRefinements.add(x);
                            }
                        }
                    });
                }
            }
        }
    }
}
