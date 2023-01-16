package de.dafuqs.artis.inventory.condenser;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.slot.*;

public class OverfillSlot extends Slot {

    private final int index;

    public OverfillSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.index = index;
    }

    @Override
    public ItemStack insertStack(ItemStack insertStack, int insertCount) {
        if (!insertStack.isEmpty() && this.canInsert(insertStack)) {
            ItemStack existingStack = this.getStack();
            int availableAmount = Math.min(insertCount, insertStack.getCount());
            if (existingStack.isEmpty()) {
                long finalInsertAmount = Math.min(
                        availableAmount, // how much the stack can hold
                        getMaxItemCount() // how much room there is in this
                );
                ItemStack setStack = insertStack.split((int) finalInsertAmount);
                this.setStack(setStack);
            } else if (ItemStack.canCombine(existingStack, insertStack)) {
                long leftover = addAmount(availableAmount);
                insertStack.decrement((int) (availableAmount - leftover));
            }
        }
        return insertStack;
    }

    public int getMaxItemCount() {
        if(this.inventory instanceof VariantBackedInventory variantBackedInventory) {
            return variantBackedInventory.getMaxCountPerStackForSlot(this.index);
        } else {
            return this.inventory.getMaxCountPerStack();
        }
    }

    public long getRealItemCount() {
        if(this.inventory instanceof VariantBackedInventory variantBackedInventory) {
            return variantBackedInventory.getRealAmount(this.index);
        } else {
            return this.inventory.getMaxCountPerStack();
        }
    }

    public long addAmount(long amount) {
        if(this.inventory instanceof VariantBackedInventory variantBackedInventory) {
            return variantBackedInventory.addAmount(this.index, amount);
        }
        return 0;
    }

}
