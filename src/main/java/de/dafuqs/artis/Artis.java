package de.dafuqs.artis;

import de.dafuqs.artis.event.*;
import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.inventory.crafting.*;
import de.dafuqs.artis.recipe.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.screen.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;

public class Artis implements ModInitializer {

    public static final String MODID = "artis";

    private static final Logger LOGGER = LogManager.getLogger();

    public static final Identifier RECIPE_SYNC_IDENTIFIER = new Identifier(MODID, "sync_recipe");
    public static final Identifier REQUEST_SYNC_IDENTIFIER = new Identifier(MODID, "request_sync");

    public static void log(Level logLevel, String message) {
        LOGGER.log(logLevel, "[Artis-Recrafted] " + message);
    }

    @Override
    public void onInitialize() {
        ArtisConfig.loadConfig();
        ArtisEvents.init();
        ArtisBlocks.register();
        ArtisRecipeTypes.register();
        ArtisScreenHandlers.register();

        //seems to be required to not have the recipe vanish when initially opened
        ServerPlayNetworking.registerGlobalReceiver(Artis.REQUEST_SYNC_IDENTIFIER, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ScreenHandler container = player.currentScreenHandler;
                if (container instanceof ArtisRecipeProvider) {
                    container.onContentChanged(null);
                }
            });
        });
    }

}
