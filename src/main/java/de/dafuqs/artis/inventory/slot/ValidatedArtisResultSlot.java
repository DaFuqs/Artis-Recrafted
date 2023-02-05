package de.dafuqs.artis.inventory.slot;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.crafting.*;
import io.github.cottonmc.cotton.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;

import java.util.*;

public class ValidatedArtisResultSlot extends ValidatedSlot {

    private final ArtisCraftingInventory craftingInv;
    private final PlayerEntity player;
    private int amount;

    public ValidatedArtisResultSlot(PlayerEntity player, ArtisCraftingInventory inventory, Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftingInv = inventory;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onTake(int amount) {
        this.amount += amount;
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        if (this.amount > 0) {
            stack.onCraft(this.player.world, this.player, this.amount);
        }
        this.amount = 0;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);

        if (this.inventory instanceof CraftingResultInventory craftingResultInventory && craftingResultInventory.getLastRecipe() instanceof ArtisCraftingRecipe artisCraftingRecipe) {
            List<ItemStack> catalystInventory = this.craftingInv.getCatalystInventory();
            for (int i = 0; i < catalystInventory.size(); i++) {
                ItemStack catalystStack = catalystInventory.get(i);

                ItemStack remainder = catalystStack.getRecipeRemainder();
                if (!remainder.isEmpty()) {
                    catalystInventory.set(i, remainder);
                } else {
                    if (catalystStack.isDamageable()) {
                        catalystStack.setDamage(catalystStack.getDamage() + artisCraftingRecipe.getCatalystCost());
                        if (catalystStack.getDamage() >= catalystStack.getMaxDamage()) {
                            catalystInventory.set(i, ItemStack.EMPTY);
                        }
                    } else if (catalystStack.getItem() instanceof SpecialCatalyst specialCatalyst) {
                        specialCatalyst.consume(catalystStack, artisCraftingRecipe.getCatalystCost());
                    } else {
                        catalystStack.decrement(artisCraftingRecipe.getCatalystCost());
                    }
                }
            }
        }

        List<ItemStack> leftovers = new ArrayList<>();
        for (int i = 0; i < this.craftingInv.getCraftingInventory().size(); i++) {
            ItemStack slotStack = this.craftingInv.getStack(i);
            Item item = slotStack.getItem();
            if (item.hasRecipeRemainder()) {
                if (slotStack.getCount() == 1) {
                    this.craftingInv.setStack(i, new ItemStack(item.getRecipeRemainder()));
                } else {
                    slotStack.decrement(1);
                    leftovers.add(slotStack);
                    this.craftingInv.setStack(i, ItemStack.EMPTY);
                }
            } else {
                this.craftingInv.removeStack(i, 1);
            }
        }

        for (ItemStack leftover : leftovers) {
            if (!this.player.getInventory().insertStack(leftover)) {
                this.player.dropItem(leftover, false);
            }
        }

    }

}