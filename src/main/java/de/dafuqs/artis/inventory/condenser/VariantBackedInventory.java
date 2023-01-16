package de.dafuqs.artis.inventory.condenser;

import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.base.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;

public class VariantBackedInventory implements Inventory {

    private BlockEntity blockEntity;
    private SingleVariantStorage<ItemVariant>[] storages;

    public VariantBackedInventory(BlockEntity blockEntity, SingleVariantStorage<ItemVariant>... slots) {
        this.blockEntity = blockEntity;
        this.storages = slots;
    }

    @Override
    public int size() {
        return storages.length;
    }

    @Override
    public boolean isEmpty() {
        for(SingleVariantStorage storage : storages) {
            if(storage.getAmount() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        SingleVariantStorage<ItemVariant> storage = storages[slot];
        if(storage.amount == 0) {
            return ItemStack.EMPTY;
        } else {
            int slotAmount = (int) Math.min(storage.variant.getItem().getMaxCount(), storage.amount);
            return storage.variant.toStack(slotAmount);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        SingleVariantStorage<ItemVariant> storage = storages[slot];
        int retrievedAmount = (int) Math.min(amount, storage.amount);
        storage.amount -= retrievedAmount;
        this.markDirty();
        return storage.variant.toStack(retrievedAmount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        SingleVariantStorage<ItemVariant> storage = storages[slot];
        int retrievedAmount = (int) Math.min(storage.variant.getItem().getMaxCount(), storage.amount);
        storage.amount -= retrievedAmount;
        this.markDirty();
        return storage.variant.toStack(retrievedAmount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.markDirty();
        SingleVariantStorage<ItemVariant> storage = storages[slot];
        storage.variant = ItemVariant.of(stack);
        storage.amount = stack.getCount();
    }

    @Override
    public void markDirty() {
        this.blockEntity.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.markDirty();
        for(SingleVariantStorage<ItemVariant> storage : storages) {
            storage.variant = ItemVariant.blank();
            storage.amount = 0;
        }
    }

    public int getMaxCountPerStackForSlot(int index) {
        return (int) storages[index].getCapacity();
    }

    /**
     * Adds a number of items to the slot
     * @param index The slot to add to
     * @param amount The amount to add
     * @return The amount that could not get added
     */
    public long addAmount(int index, long amount) {
        long existingAmount = storages[index].amount;
        long newAmount = Math.min(existingAmount + amount, getMaxCountPerStackForSlot(index));
        storages[index].amount = newAmount;
        markDirty();
        return Math.abs(newAmount - existingAmount - amount);
    }

    public long getRealAmount(int index) {
        return storages[index].amount;
    }

}
