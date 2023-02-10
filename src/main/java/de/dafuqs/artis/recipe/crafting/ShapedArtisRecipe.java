package de.dafuqs.artis.recipe.crafting;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

public class ShapedArtisRecipe extends ShapedRecipe implements ArtisCraftingRecipe {
    private final RecipeType type;
    private final RecipeSerializer serializer;
    private final Ingredient catalyst;
    private final int catalystCost;

    public ShapedArtisRecipe(RecipeType type, RecipeSerializer serializer, Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output, Ingredient catalyst, int catalystCost) {
        super(id, group, width, height, ingredients, output);
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
        if (!(inventory instanceof ArtisCraftingInventory)) return false;
        ArtisCraftingInventory artis = (ArtisCraftingInventory) inventory;
        ItemStack toTest = artis.getCatalyst();
        if (artis.shouldCompareCatalyst()) {
            if (!catalyst.test(toTest)) return false;
            if (toTest.isDamageable()) {
                if (toTest.getMaxDamage() - toTest.getDamage() < catalystCost) return false;
            } else if (toTest.getItem() instanceof SpecialCatalyst) {
                if (!((SpecialCatalyst) toTest.getItem()).matches(toTest, catalystCost)) return false;
            } else {
                if (toTest.getCount() < catalystCost) return false;
            }
        }

        for (int i = 0; i <= artis.getWidth() - this.getWidth(); ++i) {
            for (int j = 0; j <= artis.getHeight() - this.getHeight(); ++j) {
                if (this.matchesSmall(artis, i, j, true)) {
                    return true;
                }

                if (this.matchesSmall(artis, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesSmall(CraftingInventory inventory, int maxWidth, int maxHeight, boolean tall) {
        for (int i = 0; i < inventory.getWidth(); ++i) {
            for (int j = 0; j < inventory.getHeight(); ++j) {
                int x = i - maxWidth;
                int y = j - maxHeight;
                Ingredient ingredient = Ingredient.EMPTY;
                if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
                    if (tall) {
                        ingredient = this.getIngredients().get(this.getWidth() - x - 1 + y * this.getWidth());
                    } else {
                        ingredient = this.getIngredients().get(x + y * this.getWidth());
                    }
                }

                if (!ingredient.test(inventory.getStack(i + j * inventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return this.getOutput().copy();
    }

    public int getWidth() {
        return super.getWidth();
    }

    public int getHeight() {
        return super.getHeight();
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

}
