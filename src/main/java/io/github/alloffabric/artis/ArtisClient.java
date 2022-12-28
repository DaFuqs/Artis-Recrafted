package io.github.alloffabric.artis;

import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingScreen;
import io.github.alloffabric.artis.inventory.ArtisRecipeProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;

public class ArtisClient implements ClientModInitializer {
	
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
			ScreenHandlerType<ArtisRecipeProvider> screenHandlerType = (ScreenHandlerType<ArtisRecipeProvider>) Registries.SCREEN_HANDLER.get(type.getId());
			ScreenRegistry.<ArtisRecipeProvider, ArtisCraftingScreen>register(screenHandlerType, ArtisCraftingScreen::new);
			
			if (!(type instanceof ArtisExistingBlockType) && !(type instanceof ArtisExistingItemType)) {
				if (type.hasColor()) {
					ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> type.getColor(), Registries.BLOCK.get(type.getId()));
					ColorProviderRegistry.ITEM.register((stack, index) -> type.getColor(), Registries.ITEM.get(type.getId()));
				}
				
				BlockRenderLayerMap.INSTANCE.putBlock(Registries.BLOCK.get(type.getId()), RenderLayer.getCutout());
			}
		}
	}
	
}
