package de.dafuqs.artis.inventory.condenser;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.slot.*;

/**
 * A slot that can be combined with a VariantBackedInventory
 * to store an amount > 64 into the slot.
 * This slot will display a max of 64 items to the user to extract at once
 * You will need a different way to show that > 64 count to the user, like syncing it via PropertyDelegate
 */
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
                long finalInsertAmount = Math.min(availableAmount, getMaxItemCount());
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

    public long addAmount(long amount) {
        if(this.inventory instanceof VariantBackedInventory variantBackedInventory) {
            return variantBackedInventory.addAmount(this.index, amount);
        }
        return 0;
    }

}
