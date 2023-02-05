package de.dafuqs.artis;

import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.client.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.*;
import net.minecraft.util.*;

public class ArtisClient implements ClientModInitializer {

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ArtisScreenHandlers.registerClient();

        ClientPlayNetworking.registerGlobalReceiver(Artis.RECIPE_SYNC_IDENTIFIER, (client, handler, buf, responseSender) -> {
            Identifier location = buf.readIdentifier();

            client.execute(() -> {
                ScreenHandler container = client.player.currentScreenHandler;
                if (container instanceof ArtisRecipeProvider) {
                    Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
                    updateLastRecipe((ArtisRecipeProvider) client.player.currentScreenHandler, (Recipe<CraftingInventory>) r);
                }
            });
        });
    }

    public static void updateLastRecipe(ArtisRecipeProvider container, Recipe<CraftingInventory> rec) {
        CraftingInventory craftInput = container.getCraftInv();
        CraftingResultInventory craftResult = container.getResultInv();

        craftResult.setLastRecipe(rec);
        if (rec != null) {
            craftResult.setStack(0, rec.craft(craftInput));
        } else {
            craftResult.setStack(0, ItemStack.EMPTY);
        }
    }
}
