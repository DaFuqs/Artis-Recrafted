package io.github.alloffabric.artis;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screenhandler.v1.*;
import net.minecraft.client.render.*;
import net.minecraft.registry.*;
import net.minecraft.screen.*;

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
