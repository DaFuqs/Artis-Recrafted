package io.github.alloffabric.artis;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.*;
import de.dafuqs.artis.block.*;
import de.dafuqs.artis.event.*;
import de.dafuqs.artis.inventory.crafting.*;
import io.github.alloffabric.artis.block.*;
import io.github.alloffabric.artis.block.ArtisTableItem;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.itemgroup.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
        ArtisConfig.loadConfig();
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
