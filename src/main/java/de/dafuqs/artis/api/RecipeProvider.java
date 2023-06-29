package de.dafuqs.artis.api;

import net.fabricmc.api.*;
import net.minecraft.recipe.*;

public interface RecipeProvider {
	void populateRecipeMatcher(RecipeMatcher finder);
	
	void clearCraftingSlots();
	
	boolean matches(Recipe recipe);
	
	int getCraftingResultSlotIndex();
	
	ArtisCraftingRecipeType getArtisCraftingRecipeType();
	
	int getCraftingWidth();
	
	int getCraftingHeight();
	
	@Environment(EnvType.CLIENT)
	int getCraftingSlotCount();
}
