package de.dafuqs.artis.recipe.crafting;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

public class ShapelessArtisRecipe extends ShapelessRecipe implements ArtisCraftingRecipe {
    private final RecipeType type;
    private final RecipeSerializer serializer;
    private final Ingredient catalyst;
    private final int catalystCost;

    public ShapelessArtisRecipe(RecipeType type, RecipeSerializer serializer, Identifier id, String group, DefaultedList<Ingredient> ingredients, ItemStack output, Ingredient catalyst, int catalystCost) {
        super(id, group, output, ingredients);
        this.type = type;
        this.serializer = serializer;
        this.catalyst = catalyst;
        this.catalystCost = catalystCost;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (!(inventory instanceof ArtisCraftingInventory artisCraftingInventory)) return false;
        ItemStack toTest = artisCraftingInventory.getCatalyst();
        if (artisCraftingInventory.shouldCompareCatalyst()) {
            if (!catalyst.test(toTest)) return false;
            if (toTest.isDamageable()) {
                if (toTest.getMaxDamage() - toTest.getDamage() < catalystCost) return false;
            } else if (toTest.getItem() instanceof SpecialCatalyst) {
                if (!((SpecialCatalyst) toTest.getItem()).matches(toTest, catalystCost)) return false;
            } else {
                if (toTest.getCount() < catalystCost) return false;
            }
        }
        return super.matches(inventory, world);
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return this.getOutput().copy();
    }

    @Override
    public RecipeType getType() {
        return type;
    }

    @Override
    public RecipeSerializer getSerializer() {
        return serializer;
    }

    @Override
    public Ingredient getCatalyst() {
        return catalyst;
    }

    @Override
    public int getCatalystCost() {
        return catalystCost;
    }

    @Override
    public int getWidth() {
        return ((ArtisTableType) type).getWidth();
    }

    @Override
    public int getHeight() {
        return ((ArtisTableType) type).getHeight();
    }

}
