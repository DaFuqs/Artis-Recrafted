package io.github.alloffabric.artis.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;

public interface RecipeProvider {
    void populateRecipeMatcher(RecipeMatcher finder);

    void clearCraftingSlots();

    boolean matches(Recipe recipe);

    int getCraftingResultSlotIndex();

    ArtisTableType getTableType();

    int getCraftingWidth();

    int getCraftingHeight();

    @Environment(EnvType.CLIENT)
    int getCraftingSlotCount();
}
