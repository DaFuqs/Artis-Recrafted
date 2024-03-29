package de.dafuqs.artis.recipe.crafting;

import com.google.gson.*;
import de.dafuqs.artis.api.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import org.jetbrains.annotations.*;

public class ShapelessArtisSerializer implements RecipeSerializer<ShapelessArtisRecipe> {
    private final ArtisTableType type;

    public ShapelessArtisSerializer(ArtisTableType type) {
        this.type = type;
    }

    private static @NotNull DefaultedList<Ingredient> getIngredients(@NotNull JsonArray array) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    @Override
    public ShapelessArtisRecipe read(Identifier id, JsonObject jsonObject) {
        String group = JsonHelper.getString(jsonObject, "group", "");
        DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        } else if (ingredients.size() > type.getWidth() * type.getHeight()) {
            throw new JsonParseException("Too many ingredients for shapeless " + type.getId().toString() + " recipe");
        } else {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            Ingredient catalyst = JsonHelper.hasElement(jsonObject, "catalyst") ? Ingredient.fromJson(jsonObject.get("catalyst")) : Ingredient.ofStacks(ItemStack.EMPTY);
            int cost = JsonHelper.hasElement(jsonObject, "cost") ? JsonHelper.getInt(jsonObject, "cost") : 0;
            return new ShapelessArtisRecipe(type, this, id, group, ingredients, output, catalyst, cost);
        }
    }

    @Override
    public ShapelessArtisRecipe read(Identifier id, @NotNull PacketByteBuf buf) {
        String group = buf.readString(32767);
        int size = buf.readVarInt();

        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
        ingredients.replaceAll(ignored -> Ingredient.fromPacket(buf));

        ItemStack output = buf.readItemStack();

        Ingredient catalyst = Ingredient.fromPacket(buf);
        int cost = buf.readInt();

        return new ShapelessArtisRecipe(type, this, id, group, ingredients, output, catalyst, cost);
    }

    @Override
    public void write(@NotNull PacketByteBuf buf, @NotNull ShapelessArtisRecipe recipe) {
        buf.writeString(recipe.getGroup());
        buf.writeVarInt(recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }

        buf.writeItemStack(recipe.getOutput());

        recipe.getCatalyst().write(buf);
        buf.writeInt(recipe.getCatalystCost());
    }

}
