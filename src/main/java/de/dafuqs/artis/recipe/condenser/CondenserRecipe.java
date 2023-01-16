package de.dafuqs.artis.recipe.condenser;

import de.dafuqs.artis.*;
import de.dafuqs.artis.recipe.*;
import net.id.incubus_core.recipe.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

public class CondenserRecipe implements Recipe<Inventory> {

    protected final Identifier id;
    protected final String group;
    protected final IngredientStack input;
    protected final int fuelPerTick;
    protected final int time;
    protected final ItemStack output;

    public CondenserRecipe(Identifier id, String group, IngredientStack input, int fuelPerTick, int time, ItemStack output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.fuelPerTick = fuelPerTick;
        this.time = time;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ArtisBlocks.CONDENSER_BLOCK);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ArtisRecipeTypes.CONDENSER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ArtisRecipeTypes.CONDENSER;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input.getIngredient());
        return defaultedList;
    }

}
