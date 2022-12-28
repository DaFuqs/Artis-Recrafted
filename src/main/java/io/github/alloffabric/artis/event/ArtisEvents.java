package io.github.alloffabric.artis.event;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisScreenFactory;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

public class ArtisEvents {
	
	public static void init() {
		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
			Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
			Identifier identifier = Registries.BLOCK.getId(block);
			if (Artis.ARTIS_TABLE_TYPES.containsId(identifier)) {
				ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(identifier);
				if (type instanceof ArtisExistingBlockType) {
					if (!world.isClient)
						playerEntity.openHandledScreen(new ArtisScreenFactory(type, block, blockHitResult));
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		});
		
		UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
			if (!playerEntity.getStackInHand(hand).isEmpty()) {
				Item item = playerEntity.getStackInHand(hand).getItem();
				Identifier identifier = Registries.ITEM.getId(item);
				if (Artis.ARTIS_TABLE_TYPES.containsId(identifier)) {
					ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(identifier);
					if (type instanceof ArtisExistingItemType) {
						if (!world.isClient) {
							playerEntity.openHandledScreen(new ArtisScreenFactory(type, null, null));
						}
						return TypedActionResult.success(playerEntity.getStackInHand(hand));
					}
				}
			}
			return TypedActionResult.pass(playerEntity.getStackInHand(hand));
		});
	}
	
}
