package de.dafuqs.artis.inventory.crafting;

import de.dafuqs.artis.api.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.registry.*;
import net.minecraft.screen.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.hit.*;
import org.jetbrains.annotations.*;

public record ArtisScreenFactory(ArtisCraftingRecipeType tableType, Block block,
								 BlockHitResult blockHitResult) implements ExtendedScreenHandlerFactory {
	
	@Contract("_, _, _ -> new")
	@Override
	public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		if (this.block == null || this.blockHitResult == null) {
			return new ArtisRecipeProvider(Registries.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(player.getWorld(), player.getBlockPos()));
		} else {
			return new ArtisRecipeProvider(Registries.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(player.getWorld(), blockHitResult.getBlockPos()));
		}
	}
	
	@Contract(" -> new")
	@Override
	public @NotNull Text getDisplayName() {
		return tableType.getName();
	}
	
	@Override
	public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
		if (blockHitResult == null) {
			packetByteBuf.writeBlockPos(serverPlayerEntity.getBlockPos());
		} else {
			packetByteBuf.writeBlockPos(blockHitResult.getBlockPos());
		}
	}
	
}
