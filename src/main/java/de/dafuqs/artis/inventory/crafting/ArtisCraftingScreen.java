package de.dafuqs.artis.inventory.crafting;

import io.github.cottonmc.cotton.gui.client.*;
import net.fabricmc.api.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;

@Environment(EnvType.CLIENT)
public class ArtisCraftingScreen extends CottonInventoryScreen<ArtisRecipeProvider> {

    public ArtisCraftingScreen(ArtisRecipeProvider gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }

    public ArtisCraftingScreen(ArtisRecipeProvider gui, PlayerInventory inventory, Text title) {
        super(gui, inventory.player, title);
    }

}