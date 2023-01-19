package de.dafuqs.artis.inventory.variantbacked;

import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;

public class ScreenHandlerTransferHelper {

    public static boolean insertItemIntoVariantBacked(ScreenHandler screenHandler, ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            }

            while(true) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot = screenHandler.slots.get(i);
                slotStack = slot.getStack();
                if(slot instanceof VariantBackedSlot variantBackedSlot) {
                    if (slot.canInsert(stack)) {
                        if (slotStack.isEmpty()) {
                            variantBackedSlot.setStack(stack);
                            slot.setStack(stack.split(slot.getMaxItemCount()));
                        } else {
                            long leftover = variantBackedSlot.addAmount(stack.getCount());
                            stack.setCount((int) leftover);
                        }
                        slot.markDirty();
                        bl = true;
                        break;
                    }
                } else {
                    if (slotStack.isEmpty() && slot.canInsert(stack)) {
                        if (stack.getCount() > slot.getMaxItemCount()) {
                            slot.setStack(stack.split(slot.getMaxItemCount()));
                        } else {
                            slot.setStack(stack.split(stack.getCount()));
                        }

                        slot.markDirty();
                        bl = true;
                        break;
                    }
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return bl;
    }

}
