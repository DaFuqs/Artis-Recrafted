package de.dafuqs.artis.compat.rei.crafting;

import de.dafuqs.artis.api.*;
import me.shedaniel.rei.api.common.category.*;
import me.shedaniel.rei.api.common.display.*;
import me.shedaniel.rei.api.common.display.basic.*;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.registry.*;
import me.shedaniel.rei.api.common.util.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public class ArtisRecipeDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

    private final ArtisCraftingRecipe display;
    private final ArtisTableType type;
    private final Ingredient catalyst;
    private final int catalystCost;
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public ArtisRecipeDisplay(@NotNull ArtisCraftingRecipe recipe) {
        super(recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toCollection(ArrayList::new)), Collections.singletonList(EntryIngredients.of(recipe.getOutput())));
        this.display = recipe;
        this.type = (ArtisTableType) recipe.getType();
        this.input = recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toCollection(ArrayList::new));
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        this.catalyst = recipe.getCatalyst();
        this.catalystCost = recipe.getCatalystCost();
    }

    /**
     * When using Shift click on the plus button in the REI gui to autofill crafting grids
     */
    public ArtisRecipeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @NotNull ArtisCraftingRecipe recipe) {
        super(inputs, outputs);
        this.display = recipe;
        this.type = (ArtisTableType) recipe.getType();
        this.input = recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toCollection(ArrayList::new));
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        this.catalyst = recipe.getCatalyst();
        this.catalystCost = recipe.getCatalystCost();
    }

    public static BasicDisplay.Serializer<ArtisRecipeDisplay> serializer() {
        return ArtisRecipeDisplay.Serializer.ofSimple(ArtisRecipeDisplay::simple).inputProvider(ArtisRecipeDisplay::getInputEntries);
    }

    private static @NotNull ArtisRecipeDisplay simple(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @NotNull Optional<Identifier> identifier) {
        Recipe<?> optionalRecipe = identifier.flatMap(resourceLocation -> RecipeManagerContext.getInstance().getRecipeManager().get(resourceLocation)).orElse(null);
        return new ArtisRecipeDisplay(inputs, outputs, (ArtisCraftingRecipe) optionalRecipe);
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
