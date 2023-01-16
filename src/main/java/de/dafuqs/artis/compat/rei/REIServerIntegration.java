package de.dafuqs.artis.compat.rei;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.ArtisTableType;
import de.dafuqs.artis.compat.rei.crafting.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class REIServerIntegration implements REIServerPlugin {
	
	// For shift-clicking into crafting gui
	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		for(ArtisTableType type : ArtisBlocks.ARTIS_TABLE_TYPES) {
			registry.register(type.getCategoryIdentifier(), ArtisRecipeDisplay.serializer());
		}
	}
	
}
