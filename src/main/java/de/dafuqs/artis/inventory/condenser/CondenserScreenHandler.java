package de.dafuqs.artis.inventory.condenser;

import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.recipe.*;
import dev.architectury.registry.fuel.*;
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
		this.world = playerInventory.player.world;
		this.inventory = playerInventory;

		this.addProperties(propertyDelegate);

		// condenser inventory
		this.addSlot(new OverfillSlot(inventory, 0, 56, 17));
		this.addSlot(new Slot(inventory, 1, 56, 53));
		this.addSlot(new CondenserOutputSlot(playerInventory.player, inventory, 2, 116, 35));

		// player inventory
		int i;
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// player hotbar
		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}
	
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			} else if (index != 1 && index != 0) {
				FuelRegistry.get(itemStack2);
				if (isInput(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (FuelRegistry.get(itemStack2) > 0) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 3 && index < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	protected boolean isInput(ItemStack itemStack) {
		return this.world.getRecipeManager().getFirstMatch(ArtisRecipeTypes.CONDENSER, new SimpleInventory(itemStack), this.world).isPresent();
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
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
