package de.dafuqs.artis.inventory.condenser;

import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.variantbacked.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;

public class CondenserFuelSlot extends VariantBackedSlot {

    public CondenserFuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public boolean canInsert(ItemStack stack) {
        return CondenserBlockEntity.getFuelTime(stack.getItem()) > 0;
    }

}
