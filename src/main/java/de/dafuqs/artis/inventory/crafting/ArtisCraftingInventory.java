package de.dafuqs.artis.inventory.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public class ArtisCraftingInventory extends CraftingInventory {

    private final CraftingInventory craftingInventory;
    private final DefaultedList<ItemStack> catalystInventory;
    private final ArtisRecipeProvider container;
    private boolean checkMatrixChanges = false;

    private final int catalystSlotID;

    public ArtisCraftingInventory(ArtisRecipeProvider container, int width, int height) {
        super(container, width, height);
        this.catalystSlotID = width * height;

        this.craftingInventory = new CraftingInventory(container, width, height);
        this.catalystInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.container = container;
    }

    @Override
    public int size() {
        return this.craftingInventory.size() + catalystInventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.craftingInventory.isEmpty() && catalystInventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if(slot == catalystSlotID) {
            return catalystInventory.get(0);
        } else {
            return craftingInventory.getStack(slot);
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        if(slot == catalystSlotID) {
            return Inventories.removeStack(catalystInventory, 0);
        } else {
            return craftingInventory.removeStack(slot);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot == catalystSlotID) {
            ItemStack stack = Inventories.splitStack(this.catalystInventory, 0, amount);
            if (!stack.isEmpty() && checkMatrixChanges) {
                this.container.onContentChanged(this);
            }
            return stack;
        } else {
            return craftingInventory.removeStack(slot, amount);
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot == catalystSlotID) {
            catalystInventory.set(0, stack);
        } else {
            craftingInventory.setStack(slot,stack);
        }
        if (checkMatrixChanges) {
            this.container.onContentChanged(this);
        }
    }

    @Override
    public void clear() {
        this.craftingInventory.clear();
        this.catalystInventory.clear();
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        this.craftingInventory.provideRecipeInputs(finder);
    }

    public ItemStack getCatalyst() {
        return getStack(getWidth() * getHeight());
    }

    public RecipeType<? extends CraftingRecipe> getType() {
        Optional<CraftingRecipe> opt = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(container.getTableType(), container.getCraftInv(), getPlayer().getEntityWorld());
        Optional<CraftingRecipe> optCrafting = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, container.getCraftInv(), getPlayer().getEntityWorld());
        if (opt.isPresent()) {
            return (container).getTableType();
        } else if (optCrafting.isPresent()) {
            return RecipeType.CRAFTING;
        }
        return (container).getTableType();
    }

    public boolean shouldCompareCatalyst() {
        return container.getTableType().hasCatalystSlot();
    }

    public PlayerEntity getPlayer() {
        return container.getPlayer();
    }

    public void setCheckMatrixChanges(boolean b) {
        this.checkMatrixChanges = b;
    }

    public CraftingInventory getCraftingInventory() {
        return this.craftingInventory;
    }

    public DefaultedList<ItemStack> getCatalystInventory() {
        return this.catalystInventory;
    }

}
