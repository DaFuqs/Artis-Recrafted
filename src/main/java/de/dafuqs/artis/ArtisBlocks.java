package de.dafuqs.artis;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.block.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.screen.*;
import net.minecraft.util.*;

import java.util.*;

public class ArtisBlocks {
	
	public static final SimpleRegistry<ArtisCraftingRecipeType> ARTIS_TABLE_TYPES = FabricRegistryBuilder.createSimple(ArtisCraftingRecipeType.class, new Identifier(Artis.MODID, "artis_table_types")).buildAndRegister();
	public static BlockEntityType<ArtisTableBlockEntity> ARTIS_BLOCK_ENTITY;
	
	public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BLOCKS = new ArrayList<>();
	public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BE_BLOCKS = new ArrayList<>();
	
	public static Block CONDENSER_BLOCK = new CondenserBlock(FabricBlockSettings.copyOf(Blocks.FURNACE).nonOpaque());
	public static BlockEntityType<CondenserBlockEntity> CONDENSER_BLOCK_ENTITY;
	
	public static void registerTable(ArtisCraftingRecipeType type, Block.Settings settings) {
		Identifier id = type.getId();
		ExtendedScreenHandlerType<ArtisRecipeProvider> screenHandlerType = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new ArtisRecipeProvider(null, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.getWorld(), buf.readBlockPos())));
		ScreenHandlerRegistry.registerExtended(id, (syncId, playerInventory, buf) -> new ArtisRecipeProvider(screenHandlerType, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.getWorld(), buf.readBlockPos())));
		
		if (type instanceof ArtisExistingBlockType artisExistingBlockType) {
			ArtisResources.registerDataForExistingBlock(artisExistingBlockType);
		} else if (type instanceof ArtisExistingItemType artisExistingItemType) {
			ArtisResources.registerDataForExistingItem(artisExistingItemType);
		} else {
			ArtisTableBlock block;
			if (type.hasBlockEntity()) {
				block = Registry.register(Registries.BLOCK, id, new ArtisTableBEBlock(type, settings));
				ARTIS_TABLE_BE_BLOCKS.add(block);
				
			} else {
				block = Registry.register(Registries.BLOCK, id, new ArtisTableBlock(type, settings));
			}
			ARTIS_TABLE_BLOCKS.add(block);
			Registry.register(Registries.ITEM, id, new ArtisTableItem(block, new Item.Settings()));
			ArtisResources.registerDataForTable(type, block);
		}
		
		Registry.register(ARTIS_TABLE_TYPES, id, type);
	}
	
	public static void registerBlockWithItem(String name, Block block, Item.Settings itemSettings) {
		Registry.register(Registries.BLOCK, new Identifier(Artis.MODID, name), block);
		BlockItem blockItem = new BlockItem(block, itemSettings);
		Registry.register(Registries.ITEM, new Identifier(Artis.MODID, name), blockItem);
	}
	
	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Artis.MODID, name), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
	}
	
	public static void register() {
		Block[] artisBlocks = Arrays.copyOf(ARTIS_TABLE_BLOCKS.toArray(), ARTIS_TABLE_BLOCKS.size(), ArtisTableBlock[].class);
		ARTIS_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Artis.MODID, "artis_table"), FabricBlockEntityTypeBuilder.create(ArtisTableBlockEntity::new, artisBlocks).build());
		
		registerBlockWithItem("condenser", CONDENSER_BLOCK, new Item.Settings());
		CONDENSER_BLOCK_ENTITY = registerBlockEntity("condenser", CondenserBlockEntity::new, CONDENSER_BLOCK);
		
		ItemStorage.SIDED.registerForBlockEntity((condenserBlockEntity, direction) -> {
			switch (direction) {
				case UP -> {
					return condenserBlockEntity.input;
				}
				case DOWN -> {
					return condenserBlockEntity.output;
				}
				default -> {
					return condenserBlockEntity.fuel;
				}
			}
		}, CONDENSER_BLOCK_ENTITY);
	}
	
}
