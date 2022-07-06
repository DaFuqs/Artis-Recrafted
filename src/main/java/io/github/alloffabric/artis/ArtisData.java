package io.github.alloffabric.artis;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.util.BlockSettingsParser;
import io.github.cottonmc.jankson.JanksonFactory;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtisData {
    
    public static final Identifier artisTableBlockTagIdentifier = new Identifier(Artis.MODID, "blocks/crafting_tables");
    public static final Jankson jankson = JanksonFactory.createJankson();

    public static void loadConfig() {
        try {
            File file = FabricLoader.getInstance().getConfigDir().resolve("artis-recrafted.json5").toFile();
            if (!file.exists()) {
                Artis.log(Level.WARN, "Config file not found! Generating an empty file.");
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file, false);
                out.write("{ }".getBytes());
                out.flush();
                out.close();
                return;
            }
            JsonObject json = jankson.load(file);
            loadEntries(json.containsKey("tables") ? json.getObject("tables") : json);
        } catch (IOException | SyntaxError e) {
            Artis.log(Level.ERROR, "Error loading config: " + e.getMessage());
        }
    }

    private static void loadEntries(@NotNull JsonObject json) {
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if (Artis.ARTIS_TABLE_TYPES.containsId(new Identifier(key))) {
                Artis.log(Level.ERROR, "Table type named " + key + " already exists, skipping it...");
                continue;
            }
            JsonElement elem = json.get(key);
            if (elem instanceof JsonObject) {
                JsonObject config = (JsonObject) elem;
                ArtisTableType type = getType(key, config);
                Block.Settings settings;
                if (config.containsKey("settings")) {
                    try {
                        settings = BlockSettingsParser.parseSettings(config.getObject("settings"));
                    } catch (Exception e) {
                        Artis.log(Level.ERROR, "Table type named " + key + " has invalid block settings set. Using defaults." + e.getMessage());
                        settings = FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE);
                    }
                } else {
                    settings = FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE);
                }
                Artis.registerTable(type, settings);
            }
        }
        
        ArtisResources.registerPack();
    }
    
    @Contract("_, _ -> new")
    static @NotNull ArtisTableType getType(@NotNull String key, @NotNull JsonObject json) {
        String tableType = json.containsKey("type") ? json.get(String.class, "type") : "normal";
        Identifier id;
        String name;
        boolean blockEntity;
        if(!key.contains(":")) {
            id = new Identifier(Artis.MODID, key);
            name = json.get(String.class, "display_name");
        
            blockEntity = json.getBoolean("block_entity", false);
        } else {
            id = new Identifier(key);
            if(json.containsKey("display_name")) {
                name = json.get(String.class, "display_name");
            } else {
                if(tableType.equals("existing_block")) {
                    name = Language.getInstance().get(Registry.BLOCK.get(id).getName().getString());
                } else if(tableType.equals("existing_item")) {
                    name = Language.getInstance().get(Registry.ITEM.get(id).getName().getString());
                } else {
                    name = key;
                }
            }
        
            blockEntity = false;
        }
        int width = json.getInt("width", 3);
        int height = json.getInt("height", 3);
        if (width > 7) {
            Artis.log(Level.WARN, "Only tables up to 7 columns are supported. Anything higher may break visually.");
            if (width > 9) {
                Artis.log(Level.ERROR, "Table type named " + key + " has too many columns, clamping it to 9");
                width = 9;
            }
        }
        if (height > 7) {
            Artis.log(Level.WARN, "Only tables up to 7 rows are supported. Anything higher may, and likely will, break visually.");
            if (height > 9) {
                Artis.log(Level.ERROR, "Table type named " + key + " has too many rows, clamping it to 9");
                height = 9;
            }
        }
        boolean catalystSlot = json.getBoolean("catalyst_slot", false);
        boolean includeNormalRecipes = json.getBoolean("normal_recipes", false);
    
        List<Identifier> blockTags = new ArrayList<>();
        blockTags.add(artisTableBlockTagIdentifier);
        if(json.containsKey("tags")) {
            JsonArray blockTagArray = (JsonArray) json.get("tags");
            for (JsonElement currentElement : blockTagArray) {
                String currentString = ((JsonPrimitive) currentElement).getValue().toString();
                Identifier identifier = Identifier.tryParse(currentString);
        
                if (identifier == null) {
                    Artis.log(Level.WARN, "Tag " + currentElement + " could not be applied. Valid identifier?");
                } else {
                    blockTags.add(identifier);
                }
            }
        }
        
        if (tableType.equals("existing_block")) {
            if (json.containsKey("color")) {
                return new ArtisExistingBlockType(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, Integer.decode(json.get(String.class, "color").replace("#", "0x")), blockTags);
            }
            return new ArtisExistingBlockType(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, blockTags);
        } else if (tableType.equals("existing_item")) {
            if (json.containsKey("color")) {
                return new ArtisExistingItemType(id, name, width, height, catalystSlot, includeNormalRecipes, Integer.decode(json.get(String.class, "color").replace("#", "0x")), blockTags);
            }
            return new ArtisExistingItemType(id, name, width, height, catalystSlot, includeNormalRecipes, blockTags);
        }
        if (json.containsKey("color")) {
            return new ArtisTableType(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, Integer.decode(json.get(String.class, "color").replace("#", "0x")), blockTags);
        }
        
        return new ArtisTableType(id, name, width, height, blockEntity, catalystSlot, includeNormalRecipes, blockTags);
    }

}
