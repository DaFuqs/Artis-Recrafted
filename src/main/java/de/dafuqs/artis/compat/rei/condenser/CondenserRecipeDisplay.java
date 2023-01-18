package de.dafuqs.artis.compat.rei.condenser;

import de.dafuqs.artis.compat.rei.*;
import de.dafuqs.artis.recipe.condenser.*;
import me.shedaniel.rei.api.common.category.*;
import me.shedaniel.rei.api.common.display.basic.*;
import me.shedaniel.rei.api.common.util.*;

import java.util.*;

public class CondenserRecipeDisplay extends BasicDisplay {

    protected final int time;
    protected final int fuelPerTick;
    protected final boolean preservesInput;

    public CondenserRecipeDisplay(CondenserRecipe recipe) {
        super(Collections.singletonList(EntryIngredients.ofItemStacks(recipe.getInput().getStacks())), Collections.singletonList(EntryIngredients.of(recipe.getOutput())));
        this.time = recipe.getTime();
        this.fuelPerTick = recipe.getFuelPerTick();
        this.preservesInput = recipe.preservesInput();
    }

    /**
     * When using Shift click on the plus button in the REI gui to autofill crafting grids
     */
    /*public CondenserRecipeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @NotNull CondenserRecipe recipe) {
        super(inputs, outputs);
        this.display = recipe;
    }

    public static Serializer<CondenserRecipeDisplay> serializer() {
        return Serializer.ofSimple(CondenserRecipeDisplay::simple).inputProvider(CondenserRecipeDisplay::getInputEntries);
    }

    private static @NotNull CondenserRecipeDisplay simple(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @NotNull Optional<Identifier> identifier) {
        Recipe<?> optionalRecipe = identifier.flatMap(resourceLocation -> RecipeManagerContext.getInstance().getRecipeManager().get(resourceLocation)).orElse(null);
        return new CondenserRecipeDisplay(inputs, outputs, (CondenserRecipe) optionalRecipe);
    }*/

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ArtisPlugins.CONDENSER;
    }

}
