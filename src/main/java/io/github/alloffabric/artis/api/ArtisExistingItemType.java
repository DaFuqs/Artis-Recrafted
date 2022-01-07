package io.github.alloffabric.artis.api;

import net.minecraft.util.Identifier;

import java.util.List;

public class ArtisExistingItemType extends ArtisTableType {
    public ArtisExistingItemType(Identifier id, String name, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets, List<Identifier> blockTags) {
        super(id, name, width, height, false, catalystSlot, includeNormalRecipes, makeAssets, blockTags);
    }

    public ArtisExistingItemType(Identifier id, String name, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets, int color, List<Identifier> blockTags) {
        super(id, name, width, height, false, catalystSlot, includeNormalRecipes, makeAssets, color, blockTags);
    }
}
