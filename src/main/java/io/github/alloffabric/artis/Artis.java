package io.github.alloffabric.artis;

import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBEBlock;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import io.github.alloffabric.artis.block.ArtisTableBlockEntity;
import io.github.alloffabric.artis.block.ArtisTableItem;
import io.github.alloffabric.artis.event.ArtisEvents;
import io.github.alloffabric.artis.inventory.ArtisRecipeProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Artis implements ModInitializer {
    
    public static final String MODID = "artis";
    
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Identifier RECIPE_SYNC_IDENTIFIER = new Identifier(MODID,"sync_recipe");
    public static final Identifier REQUEST_SYNC_IDENTIFIER = new Identifier(MODID,"request_sync");
    public static final Identifier NULL_IDENTIFIER = new Identifier("null","null");
    
    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BLOCKS = new ArrayList<>();
    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BE_BLOCKS = new ArrayList<>();

    public static final SimpleRegistry<ArtisTableType> ARTIS_TABLE_TYPES = FabricRegistryBuilder.createSimple(ArtisTableType.class, new Identifier(MODID, "artis_table_types")).buildAndRegister();
    
    public static final ItemGroup ARTIS_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "group"), () -> {
        if(!ARTIS_TABLE_TYPES.isEmpty()) {
            ArtisTableType firstTableType = ARTIS_TABLE_TYPES.get(0);
            if(firstTableType instanceof ArtisExistingItemType) {
                return new ItemStack(Registry.ITEM.get(firstTableType.getId()));
            } else {
                return new ItemStack(Registry.BLOCK.get(firstTableType.getId()).asItem());
            }
        } else {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
    });
    public static BlockEntityType<ArtisTableBlockEntity> ARTIS_BLOCK_ENTITY;
    
    public static void log(Level logLevel, String message) {
        LOGGER.log(logLevel, "[Artis-Recrafted] " + message);
    }

    public static ArtisTableType registerTable(ArtisTableType type, Block.Settings settings) {
        return registerTable(type, settings, ARTIS_GROUP);
    }

    public static ArtisTableType registerTable(@NotNull ArtisTableType type, Block.Settings settings, ItemGroup group) {
        Identifier id = type.getId();
        ExtendedScreenHandlerType<ArtisRecipeProvider> screenHandlerType = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new ArtisRecipeProvider(null, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
        ScreenHandlerRegistry.registerExtended(id, (syncId, playerInventory, buf) -> new ArtisRecipeProvider(screenHandlerType, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
        
        if(type instanceof ArtisExistingBlockType artisExistingBlockType) {
            ArtisResources.registerDataForExistingBlock(artisExistingBlockType);
        } else if(type instanceof ArtisExistingItemType artisExistingItemType) {
            ArtisResources.registerDataForExistingItem(artisExistingItemType);
        } else {
            ArtisTableBlock block;
            if (type.hasBlockEntity()) {
                block = Registry.register(Registry.BLOCK, id, new ArtisTableBEBlock(type, settings));
                ARTIS_TABLE_BE_BLOCKS.add(block);
        
            } else {
                block = Registry.register(Registry.BLOCK, id, new ArtisTableBlock(type, settings));
            }
            ARTIS_TABLE_BLOCKS.add(block);
            Registry.register(Registry.ITEM, id, new ArtisTableItem(block, new Item.Settings().group(group)));
            ArtisResources.registerDataForTable(type, block);
        }
        
        return Registry.register(ARTIS_TABLE_TYPES, id, type);
    }
    
    @Override
    public void onInitialize() {
        ArtisData.loadConfig();
        ArtisEvents.init();

        Block[] artisBlocks = Arrays.copyOf(ARTIS_TABLE_BLOCKS.toArray(), ARTIS_TABLE_BLOCKS.size(), ArtisTableBlock[].class);
        ARTIS_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "artis_table"), FabricBlockEntityTypeBuilder.create(ArtisTableBlockEntity::new, artisBlocks).build());
        
        //seems to be required to not have the recipe vanish when initially opened
        /*ServerSidePacketRegistry.INSTANCE.register(Artis.REQUEST_SYNC_IDENTIFIER, (packetContext, attachedData) -> {
            packetContext.getTaskQueue().execute(() -> {
                ScreenHandler container = packetContext.getPlayer().currentScreenHandler;
                if (container instanceof ArtisRecipeProvider) {
                    container.onContentChanged(null);
                }
            });
        });*/
    }
    
}
