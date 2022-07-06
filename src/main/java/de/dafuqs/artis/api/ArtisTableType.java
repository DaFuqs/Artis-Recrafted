package de.dafuqs.artis.api;

import de.dafuqs.artis.compat.rei.crafting.*;
import de.dafuqs.artis.recipe.crafting.*;
import io.github.alloffabric.artis.Artis;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArtisTableType implements RecipeType {
    private final Identifier id;
    private String name;
    private final int width;
    private final int height;
    private int color = 0;
    private final boolean blockEntity;
    private final boolean catalystSlot;
    private final boolean includeNormalRecipes;
    private boolean hasColor = false;
    private final RecipeSerializer shaped;
    private final RecipeSerializer shapeless;
    private final List<Identifier> blockTags;

    public ArtisTableType(Identifier id, String name, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, int color, List<Identifier> blockTags) {
        this(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, blockTags);
        this.color = 0xFF000000 | color;
        this.hasColor = true;
    }

    public ArtisTableType(@NotNull Identifier id, String name, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, List<Identifier> blockTags) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.blockEntity = blockEntity;
        this.catalystSlot = catalystSlot;
        this.includeNormalRecipes = includeNormalRecipes;
        Identifier shapedId = new Identifier(id.getNamespace(), id.getPath() + "_shaped");
        Identifier shapelessId = new Identifier(id.getNamespace(), id.getPath() + "_shapeless");
        this.shaped = Registry.register(Registry.RECIPE_SERIALIZER, shapedId, new ShapedArtisSerializer(this));
        this.shapeless = Registry.register(Registry.RECIPE_SERIALIZER, shapelessId, new ShapelessArtisSerializer(this));
        this.blockTags = blockTags;
    }

    public Identifier getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasBlockEntity() {
        return blockEntity;
    }

    public boolean hasCatalystSlot() {
        return catalystSlot;
    }

    public boolean shouldIncludeNormalRecipes() {
        return includeNormalRecipes;
    }

    public boolean hasColor() {
        return hasColor;
    }

    public int getColor() {
        return color;
    }
    
    public List<Identifier> getBlockTags() {
        return this.blockTags;
    }
    
    public CategoryIdentifier<ArtisRecipeDisplay> getCategoryIdentifier() {
        return CategoryIdentifier.of(id);
    }

    public Rectangle getREIClickArea() {
        ContainerLayout containerLayout = new ContainerLayout(getWidth(), getHeight(), hasCatalystSlot());
        return new Rectangle(containerLayout.getArrowX() + 8, containerLayout.getArrowY() + 2, 21,16);
    }
    
    public String getRawName() {
        return this.name;
    }
    
    public Text getName() {
        return Text.translatable(getTranslationString());
    }

    public String getTableIDPath() {
        return getId().getPath();
    }

    public String getTranslationString() {
        return "block." + Artis.MODID + "." + getTableIDPath();
    }

    public String getREITranslationString() {
        return "rei.category." + getTableIDPath();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
    
}
