package de.dafuqs.artis;

import de.dafuqs.artis.inventory.*;
import net.fabricmc.api.*;

public class ArtisClient implements ClientModInitializer {
	
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ArtisScreenHandlers.registerClient();
	}
	
}
