package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ArtisRecipeDisplay implements SimpleGridMenuDisplay {
    
    private final ArtisCraftingRecipe display;
    private final ArtisTableType type;
    private final Ingredient catalyst;
    private final int catalystCost;
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public ArtisRecipeDisplay(ArtisCraftingRecipe recipe, ArtisTableType type) {
        this.display = recipe;
        this.type = type;
        this.input = recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toCollection(ArrayList::new));
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        this.catalyst = recipe.getCatalyst();
        this.catalystCost = recipe.getCatalystCost();
    }

    public ArtisCraftingRecipe getDisplay() {
        return display;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.output;
    }
    
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return type.getCategoryIdentifier();
    }
    
    @Override
    public int getWidth() {
        return display.getWidth();
    }

    @Override
    public int getHeight() {
        return display.getHeight();
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public int getCatalystCost() {
        return catalystCost;
    }
    
}
