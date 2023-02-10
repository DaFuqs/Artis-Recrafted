package de.dafuqs.artis;

import de.dafuqs.artis.api.*;
import de.dafuqs.artis.block.*;
import net.devtech.arrp.api.*;
import net.devtech.arrp.json.blockstate.*;
import net.devtech.arrp.json.lang.*;
import net.devtech.arrp.json.models.*;
import net.devtech.arrp.json.tags.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static net.devtech.arrp.json.loot.JLootTable.*;

public class ArtisResources {

    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("artis:resources");
    public static final JLang translations = JLang.lang();
    public static final HashMap<Identifier, JTag> blockTags = new HashMap<>();

    public static void registerDataForTable(@NotNull ArtisTableType artisTableType, @NotNull ArtisTableBlock block) {
        // loot table (drops)
        RESOURCE_PACK.addLootTable(block.getLootTableId(),
                loot("minecraft:block")
                        .pool(pool()
                                .rolls(1)
                                .entry(entry()
                                        .type("minecraft:item")
                                        .name(Registry.ITEM.getId(block.asItem()).toString()))
                                .condition(predicate("minecraft:survives_explosion"))));

        // localisation
        translations.entry(artisTableType.getTranslationString(), artisTableType.getRawName());
        translations.entry(artisTableType.getREITranslationString(), artisTableType.getRawName() + " Crafting");

        // block tags (like mineable / break by tool, if set via the config)
        for (Identifier identifier : artisTableType.getBlockTags()) {
            if (blockTags.containsKey(identifier)) {
                blockTags.get(identifier).add(artisTableType.getId());
            } else {
                blockTags.put(identifier, JTag.tag().add(artisTableType.getId()));
            }
        }

        // block and item models
        JBlockModel blockModel = JState.model(new Identifier(Artis.MODID, "block/table" + (artisTableType.hasColor() ? "_overlay" : "")));
        JModel model = JModel.model(new Identifier(Artis.MODID, "block/table" + (artisTableType.hasColor() ? "_overlay" : "")));
        RESOURCE_PACK.addBlockState(JState.state(JState.variant(blockModel)), new Identifier(Artis.MODID, artisTableType.getTableIDPath()));
        RESOURCE_PACK.addModel(model, new Identifier(Artis.MODID, "item/" + artisTableType.getTableIDPath()));
    }

    public static void registerPack() {
        RESOURCE_PACK.addLang(new Identifier(Artis.MODID, "en_us"), translations);
        for (Map.Entry<Identifier, JTag> tags : blockTags.entrySet()) {
            RESOURCE_PACK.addTag(tags.getKey(), tags.getValue());
        }

        RRPCallback.BEFORE_VANILLA.register(a -> a.add(RESOURCE_PACK));
    }

    public static void registerDataForExistingBlock(@NotNull ArtisExistingBlockType artisTableType) {
        String tableIdPath = artisTableType.getId().getPath();
        translations.entry("rei.category." + tableIdPath, artisTableType.getRawName() + " Crafting");
    }

    public static void registerDataForExistingItem(@NotNull ArtisExistingItemType artisTableType) {
        String tableIdPath = artisTableType.getId().getPath();
        translations.entry("rei.category." + tableIdPath, artisTableType.getRawName() + " Crafting");
    }

}
