package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class REIClientIntegration implements REIClientPlugin {
    
    public static final Map<ArtisTableType, ItemConvertible> iconMap = new HashMap<>();
    
    public REIClientIntegration() {
        for (ArtisTableBlock block : Artis.ARTIS_TABLE_BLOCKS) {
            iconMap.put(block.getType(), block);
        }
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            registry.add(new ArtisRecipeCategory<>(type));
            
            if(type instanceof ArtisExistingItemType) {
                Item item = Registry.ITEM.get(type.getId());
                registry.addWorkstations(type.getCategoryIdentifier(), EntryStacks.of(item));
                if (type.shouldIncludeNormalRecipes()) {
                    registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(item));
                }
            } else {
                Block block = Registry.BLOCK.get(type.getId());
                registry.addWorkstations(type.getCategoryIdentifier(), EntryStacks.of(block));
                if (type.shouldIncludeNormalRecipes()) {
                    registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(block));
                }
            }
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            registry.registerRecipeFiller(ArtisCraftingRecipe.class, type, ArtisRecipeDisplay::new);
        }
        
        /*registry.registerVisibilityPredicate(new DisplayVisibilityPredicate() {
            @Override
            public EventResult handleDisplay(DisplayCategory<?> category, Display display) {
                if (display.getDisplayLocation().isPresent() && MinecraftClient.getInstance().world.getRecipeManager().get(display.getDisplayLocation().get()).isPresent()) {
                    Recipe recipe = MinecraftClient.getInstance().world.getRecipeManager().get(display.getDisplayLocation().get()).get();
        
                    if (recipe.getType() instanceof ArtisTableType && display.getCategoryIdentifier().equals(DefaultPlugin.CRAFTING)) {
                        return EventResult.interruptTrue();
                    }
                }
                return EventResult.interruptFalse();
            }
    
            @Override
            public double getPriority() {
                return 10;
            }
        });*/
    }
    
    /**
     * Where in the screens gui the player has to click
     * to get to the recipe overview
     */
    @Override
    public void registerScreens(ScreenRegistry registry) {
        // TODO: since this one screen handles all different sizes there needs to be a better way to handle this
        //for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            //registry.registerContainerClickArea(type.getREIClickArea(), ArtisCraftingScreen.class, type.getCategoryIdentifier());
            //registry.registerContainerClickArea(type.getREIClickArea(), ArtisCraftingScreen.class, BuiltinPlugin.CRAFTING);
        //}
    }
    
}
