package de.dafuqs.artis.inventory.condenser;

import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.variant_backed.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;

public class CondenserInputSlot extends VariantBackedSlot {
	
	private final CondenserScreenHandler handler;
	
	public CondenserInputSlot(CondenserScreenHandler handler, Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		this.handler = handler;
	}
	
	public boolean canInsert(ItemStack stack) {
		return CondenserBlockEntity.isInput(handler.world, stack) && super.canInsert(stack);
	}
	
}
