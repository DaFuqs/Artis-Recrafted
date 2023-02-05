package de.dafuqs.artis.api;

import net.minecraft.recipe.*;

public interface ArtisCraftingRecipe extends CraftingRecipe {
    Ingredient getCatalyst();

    int getCatalystCost();

    int getWidth();

    int getHeight();
}
