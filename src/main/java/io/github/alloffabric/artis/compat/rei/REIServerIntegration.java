package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class REIServerIntegration implements REIServerPlugin {
	
	// For shift-clicking into crafting gui
	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
			registry.register(type.getCategoryIdentifier(), ArtisRecipeDisplay.serializer());
		}
	}
	
}
