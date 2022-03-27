package de.teamlapen.vampirism.entity.minion.management;

import com.google.common.collect.ImmutableList;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;


public class MinionInventory implements de.teamlapen.vampirism.api.entity.minion.IMinionInventory {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(25, ItemStack.EMPTY);
    private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.inventoryHands, this.inventoryArmor, this.inventory);
    private int availableSize;

    public MinionInventory(int availableSize) {
        assert availableSize == 9 || availableSize == 12 || availableSize == 15; //See {@link MinionContainer}
        this.availableSize = availableSize;
    }

    public MinionInventory() {
        this(9);
    }

    @Override
    public void addItemStack(@Nonnull ItemStack stack) {

        while (!stack.isEmpty()) {
            int slot = InventoryHelper.getFirstSuitableSlotToAdd(inventory, this.getContainerSize() - 6 /*access only main inventory*/, stack, this.getMaxStackSize());
            if (slot == -1) {
                break;
            }
            int oldSize = stack.getCount();
            InventoryHelper.addStackToSlotWithoutCheck(this, slot + 6 /*access main inventory*/, stack);
            if (stack.getCount() >= oldSize) {
                break;
            }
        }
    }

    public void clearContent() {
        for (List<ItemStack> list : this.allInventories) {
            list.clear();
        }

    }

    public void damageArmor(DamageSource source, float damage, MinionEntity<?> entity) {
        if (damage > 0) {
            damage = damage / 6.0F;
            if (damage < 1.0F && damage >= 0.5f) {
                damage = 1.0F;
            }
            if (damage >= 1) {
                for (int i = 0; i < this.inventoryArmor.size(); ++i) {
                    ItemStack itemstack = this.inventoryArmor.get(i);
                    if ((!source.isFire() || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
                        final int i_final = i;
                        itemstack.hurtAndBreak((int) damage, entity, (e) -> {
                            e.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i_final));
                        });
                    }
                }
            }

        }
    }

    @Override
    public int getContainerSize() {
        return 6 + availableSize;
    }

    @Override
    public int getAvailableSize() {
        return availableSize;
    }

    public MinionInventory setAvailableSize(int newSize) {
        assert newSize == 9 || newSize == 12 || newSize == 15;
        this.availableSize = newSize;
        return this;
    }

    @Override
    public NonNullList<ItemStack> getInventoryArmor() {
        return inventoryArmor;
    }

    @Override
    public NonNullList<ItemStack> getInventoryHands() {
        return inventoryHands;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        assert index >= 0;
        if (index < 2) {
            return inventoryHands.get(index);
        } else if (index < 6) {
            return inventoryArmor.get(index - 2);
        } else if (index < 6 + availableSize) {
            return inventory.get(index - 6);
        }
        return ItemStack.EMPTY;
    }

    public void read(ListTag nbtTagListIn) {
        this.inventory.clear();
        this.inventoryArmor.clear();
        this.inventoryHands.clear();

        for (int i = 0; i < nbtTagListIn.size(); ++i) {
            CompoundTag compoundnbt = nbtTagListIn.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundnbt);
            if (!itemstack.isEmpty()) {
                if (j < this.inventoryHands.size()) {
                    this.inventoryHands.set(j, itemstack);
                } else if (j >= 10 && j < this.inventoryArmor.size() + 10) {
                    this.inventoryArmor.set(j - 10, itemstack);
                } else if (j >= 20 && j < this.inventory.size() + 20) {
                    this.inventory.set(j - 20, itemstack);
                }
            }
        }

    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack1 : this.inventoryHands) {
            if (!itemstack1.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack2 : this.inventoryArmor) {
            if (!itemstack2.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack s = getItem(index);
        return !s.isEmpty() && count > 0 ? s.split(count) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = getItem(index);
        if (!s.isEmpty()) {
            this.setItem(index, ItemStack.EMPTY);
        }
        return s;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        assert index >= 0;
        if (index < 2) {
            inventoryHands.set(index, stack);
        } else if (index < 6) {
            inventoryArmor.set(index - 2, stack);
        } else if (index < 6 + availableSize) {
            inventory.set(index - 6, stack);
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    public ListTag write(ListTag nbt) {
        for (int i = 0; i < this.inventoryHands.size(); ++i) {
            if (!this.inventoryHands.get(i).isEmpty()) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putByte("Slot", (byte) i);
                this.inventoryHands.get(i).save(compoundnbt);
                nbt.add(compoundnbt);
            }
        }

        for (int j = 0; j < this.inventoryArmor.size(); ++j) {
            if (!this.inventoryArmor.get(j).isEmpty()) {
                CompoundTag compoundnbt1 = new CompoundTag();
                compoundnbt1.putByte("Slot", (byte) (j + 10));
                this.inventoryArmor.get(j).save(compoundnbt1);
                nbt.add(compoundnbt1);
            }
        }

        for (int k = 0; k < this.inventory.size(); ++k) {
            if (!this.inventory.get(k).isEmpty()) {
                CompoundTag compoundnbt2 = new CompoundTag();
                compoundnbt2.putByte("Slot", (byte) (k + 20));
                this.inventory.get(k).save(compoundnbt2);
                nbt.add(compoundnbt2);
            }
        }
        return nbt;
    }
}
