package de.dafuqs.artis.inventory.condenser;

import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.inventory.variant_backed.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.world.*;

public class CondenserScreenHandler extends ScreenHandler {
	
	protected final World world;
	private final PropertyDelegate propertyDelegate;
	private final Inventory inventory;
	
	public CondenserScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(ArtisScreenHandlers.CONDENSER_SCREEN_HANDLER, syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(5));
	}
	
	public CondenserScreenHandler(int syncId, PlayerInventory playerInventory, CondenserBlockEntity condenserBlockEntity) {
		this(ArtisScreenHandlers.CONDENSER_SCREEN_HANDLER, syncId, playerInventory, new VariantBackedInventory(condenserBlockEntity, condenserBlockEntity.input, condenserBlockEntity.fuel, condenserBlockEntity.output), condenserBlockEntity.getPropertyDelegate());
	}
	
	protected CondenserScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(type, syncId);
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.getWorld();
		this.inventory = playerInventory;
		
		this.addProperties(propertyDelegate);
		
		// condenser inventory
		this.addSlot(new CondenserInputSlot(this, inventory, 0, 56, 17));
		this.addSlot(new CondenserFuelSlot(inventory, 1, 56, 53));
		this.addSlot(new CondenserOutputSlot(playerInventory.player, inventory, 2, 116, 35));
		
		// player inventory
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		// player hotbar
		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}
	
	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
		ItemStack leftoverStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			leftoverStack = slotStack.copy();
			if (slotIndex == 2) {
				if (!this.insertItem(slotStack, 3, 39, true)) {
					return ItemStack.EMPTY;
				}
				
				slot.onQuickTransfer(slotStack, leftoverStack);
			} else if (slotIndex != 1 && slotIndex != 0) {
				if (CondenserBlockEntity.isInput(world, slotStack)) {
					if (!ScreenHandlerTransferHelper.insertItemIntoVariantBacked(this, slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (CondenserBlockEntity.getFuelTime(slotStack.getItem()) > 0) {
					if (!ScreenHandlerTransferHelper.insertItemIntoVariantBacked(this, slotStack, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotIndex >= 3 && slotIndex < 30) {
					if (!this.insertItem(slotStack, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotIndex >= 30 && slotIndex < 39 && !this.insertItem(slotStack, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(slotStack, 3, 39, false)) {
				return ItemStack.EMPTY;
			}
			
			if (slotStack.isEmpty()) {
				if (slot instanceof VariantBackedSlot variantBackedSlot) {
					variantBackedSlot.addAmount(-leftoverStack.getCount());
				} else {
					slot.setStack(ItemStack.EMPTY);
				}
			} else {
				slot.markDirty();
			}
			
			if (slotStack.getCount() == leftoverStack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTakeItem(player, slotStack);
		}
		
		return leftoverStack;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}
	
	public int getCookProgress() {
		int i = this.propertyDelegate.get(2);
		int j = this.propertyDelegate.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}
	
	public int getFuelProgress() {
		int i = this.propertyDelegate.get(1);
		if (i == 0) {
			i = 200;
		}
		
		return this.propertyDelegate.get(0) * 13 / i;
	}
	
	public boolean isBurning() {
		return this.propertyDelegate.get(0) > 0;
	}
	
	public int getInputItemCount() {
		return this.propertyDelegate.get(4);
	}
	
}
