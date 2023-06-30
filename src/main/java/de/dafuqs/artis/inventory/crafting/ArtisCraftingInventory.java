package de.dafuqs.artis.inventory.crafting;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.collection.*;

import java.util.*;

public class ArtisCraftingInventory extends CraftingInventory {
	
	private final CraftingInventory craftingInventory;
	private final DefaultedList<ItemStack> catalystInventory;
	private final ArtisRecipeProvider artisRecipeProvider;
	
	private final int catalystSlotID;
	
	public ArtisCraftingInventory(ArtisRecipeProvider artisRecipeProvider, int width, int height) {
		super(artisRecipeProvider, width, height);
		this.catalystSlotID = width * height;
		
		this.craftingInventory = new CraftingInventory(artisRecipeProvider, width, height);
		this.catalystInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
		this.artisRecipeProvider = artisRecipeProvider;
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
		if (slot == catalystSlotID) {
			return catalystInventory.get(0);
		} else {
			return craftingInventory.getStack(slot);
		}
	}
	
	@Override
	public ItemStack removeStack(int slot) {
		if (slot == catalystSlotID) {
			return Inventories.removeStack(catalystInventory, 0);
		} else {
			return craftingInventory.removeStack(slot);
		}
	}
	
	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == catalystSlotID) {
			ItemStack stack = Inventories.splitStack(this.catalystInventory, 0, amount);
			onContentChanged();
			return stack;
		} else {
			return craftingInventory.removeStack(slot, amount);
		}
	}
	
	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot == catalystSlotID) {
			catalystInventory.set(0, stack);
		} else {
			craftingInventory.setStack(slot, stack);
		}
		onContentChanged();
	}
	
	public void onContentChanged() {
		this.artisRecipeProvider.onContentChanged(this);
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
	
	public RecipeType getType() {
		Optional opt = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(artisRecipeProvider.getArtisCraftingRecipeType(), artisRecipeProvider.getCraftInv(), getPlayer().getEntityWorld());
		if (opt.isPresent()) {
			return artisRecipeProvider.getArtisCraftingRecipeType();
		}
		
		Optional<CraftingRecipe> optCrafting = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, artisRecipeProvider.getCraftInv(), getPlayer().getEntityWorld());
		if (optCrafting.isPresent()) {
			return RecipeType.CRAFTING;
		}
		return artisRecipeProvider.getArtisCraftingRecipeType();
	}
	
	public boolean shouldCompareCatalyst() {
		return artisRecipeProvider.getArtisCraftingRecipeType().hasCatalystSlot();
	}
	
	public PlayerEntity getPlayer() {
		return artisRecipeProvider.getPlayer();
	}
	
	public RecipeInputInventory getCraftingInventory() {
		return this.craftingInventory;
	}
	
	public DefaultedList<ItemStack> getCatalystInventory() {
		return this.catalystInventory;
	}
	
}
