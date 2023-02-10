package de.dafuqs.artis.compat.rei;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.*;
import de.dafuqs.artis.compat.rei.crafting.*;
import me.shedaniel.rei.api.common.display.*;
import me.shedaniel.rei.api.common.plugins.*;

public class REIServerIntegration implements REIServerPlugin {

    // For shift-clicking into crafting gui
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        for (ArtisTableType type : ArtisBlocks.ARTIS_TABLE_TYPES) {
            registry.register(type.getCategoryIdentifier(), ArtisRecipeDisplay.serializer());
        }
    }

}
