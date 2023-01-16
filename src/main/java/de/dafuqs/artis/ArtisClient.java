package de.dafuqs.artis;

import de.dafuqs.artis.inventory.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.network.*;
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

        ClientSidePacketRegistry.INSTANCE.register(Artis.RECIPE_SYNC_IDENTIFIER, (packetContext, attachedData) -> {
            Identifier location = attachedData.readIdentifier();
            packetContext.getTaskQueue().execute(() -> {
                ScreenHandler container = packetContext.getPlayer().currentScreenHandler;
                if (container instanceof ArtisRecipeProvider) {
                    Recipe<?> r = MinecraftClient.getInstance().world.getRecipeManager().get(location).orElse(null);
                    updateLastRecipe((ArtisRecipeProvider) packetContext.getPlayer().currentScreenHandler, (Recipe<CraftingInventory>) r);
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
