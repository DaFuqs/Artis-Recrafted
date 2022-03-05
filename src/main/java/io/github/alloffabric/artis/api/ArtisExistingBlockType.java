package io.github.alloffabric.artis.api;

import net.minecraft.util.Identifier;

import java.util.List;

public class ArtisExistingBlockType extends ArtisTableType {
    
    public ArtisExistingBlockType(Identifier id, String name, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, List<Identifier> blockTags) {
        super(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, blockTags);
    }

    public ArtisExistingBlockType(Identifier id, String name, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, int color, List<Identifier> blockTags) {
        super(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, color, blockTags);
    }
    
}
