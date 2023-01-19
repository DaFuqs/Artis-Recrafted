package de.dafuqs.artis.inventory.condenser;

import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.recipe.*;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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
		ItemStack returnStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			returnStack = slotStack.copy();
			if (index == 2) {
				if (!this.insertItem(slotStack, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(slotStack, returnStack);
			} else if (index != 1 && index != 0) {
				if (this.isInput(slotStack)) {
					if (!this.insertItemOverfill(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (FuelRegistry.INSTANCE.get(slotStack.getItem()) > 0) {
					if (!this.insertItem(slotStack, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 3 && index < 30) {
					if (!this.insertItem(slotStack, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.insertItem(slotStack, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(slotStack, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (slotStack.getCount() == returnStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, slotStack);
		}

		return returnStack;
	}

	protected boolean insertItemOverfill(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean bl = false;
		int i = startIndex;
		if (fromLast) {
			i = endIndex - 1;
		}

		Slot slot;
		ItemStack slotStack;
		if (stack.isStackable()) {
			while(!stack.isEmpty()) {
				if (fromLast) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				slot = this.slots.get(i);
				slotStack = slot.getStack();
				if (!slotStack.isEmpty() && ItemStack.canCombine(stack, slotStack)) {
					int j = slotStack.getCount() + stack.getCount();
					if(slot instanceof OverfillSlot overfillSlot) {
						int maxCount = overfillSlot.getMaxItemCount();
						if (j <= maxCount) {
							overfillSlot.addAmount(stack.getCount());
							stack.setCount(0);
							slot.markDirty();
							bl = true;
						} else if (slotStack.getCount() < maxCount) {
							stack.decrement(maxCount - slotStack.getCount());
							overfillSlot.addAmount(maxCount - slotStack.getCount());
							slot.markDirty();
							bl = true;
						}
					} else {
						if (j <= stack.getMaxCount()) {
							stack.setCount(0);
							slotStack.setCount(j);
							slot.markDirty();
							bl = true;
						} else if (slotStack.getCount() < stack.getMaxCount()) {
							stack.decrement(stack.getMaxCount() - slotStack.getCount());
							slotStack.setCount(stack.getMaxCount());
							slot.markDirty();
							bl = true;
						}
					}
				}

				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while(true) {
				if (fromLast) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				slot = this.slots.get(i);
				slotStack = slot.getStack();
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

				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		return bl;
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
