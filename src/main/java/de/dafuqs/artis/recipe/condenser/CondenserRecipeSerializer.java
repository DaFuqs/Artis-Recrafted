package de.dafuqs.artis.recipe.condenser;

import com.google.gson.*;
import net.id.incubus_core.recipe.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;

public class CondenserRecipeSerializer implements RecipeSerializer<CondenserRecipe> {

    public final CondenserRecipeSerializer.RecipeFactory<CondenserRecipe> recipeFactory;

    public CondenserRecipeSerializer(CondenserRecipeSerializer.RecipeFactory<CondenserRecipe> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<CondenserRecipe> {
        CondenserRecipe create(Identifier id, String group, IngredientStack input, int fuelPerTick, int time, ItemStack output);
    }

    @Override
    public CondenserRecipe read(Identifier identifier, JsonObject jsonObject) {
        String group = JsonHelper.getString(jsonObject, "group", "");
        IngredientStack input = RecipeParser.ingredientStackFromJson(JsonHelper.getObject(jsonObject, "input"));
        int fuelPerTick = JsonHelper.getInt(jsonObject, "fuel_per_tick", 0);
        int time = JsonHelper.getInt(jsonObject, "time", 200);
        ItemStack output = RecipeParser.getItemStackWithNbtFromJson(JsonHelper.getObject(jsonObject, "result"));
        return this.recipeFactory.create(identifier, group, input, fuelPerTick, time, output);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, CondenserRecipe recipe) {
        packetByteBuf.writeString(recipe.group);
        recipe.input.write(packetByteBuf);
        packetByteBuf.writeInt(recipe.fuelPerTick);
        packetByteBuf.writeInt(recipe.time);
        packetByteBuf.writeItemStack(recipe.output);
    }

    @Override
    public CondenserRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        IngredientStack input = IngredientStack.fromByteBuf(packetByteBuf);
        int fuelPerTick = packetByteBuf.readInt();
        int time = packetByteBuf.readInt();
        ItemStack output = packetByteBuf.readItemStack();
        return this.recipeFactory.create(identifier, group, input, fuelPerTick, time, output);
    }

}
