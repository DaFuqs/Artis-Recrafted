package de.dafuqs.artis.compat.rei.crafting;

import com.google.common.collect.*;
import de.dafuqs.artis.api.*;
import de.dafuqs.artis.compat.rei.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.*;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.common.category.*;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.util.*;
import net.fabricmc.api.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ArtisRecipeCategory implements DisplayCategory<ArtisRecipeDisplay> {

    private final ArtisTableType artisTableType;

    public ArtisRecipeCategory(ArtisTableType artisTableType) {
        this.artisTableType = artisTableType;
    }

    public static int getSlotWithSize(@NotNull ArtisRecipeDisplay recipeDisplay, int num, int craftingGridWidth) {
        int x = num % recipeDisplay.getDisplay().getWidth();
        int y = (num - x) / recipeDisplay.getDisplay().getWidth();
        return craftingGridWidth * y + x;
    }

    @Override
    public CategoryIdentifier<? extends ArtisRecipeDisplay> getCategoryIdentifier() {
        return artisTableType.getCategoryIdentifier();
    }

    @Override
    public Renderer getIcon() {
        if (artisTableType instanceof ArtisExistingItemType) {
            return EntryStacks.of(Registries.ITEM.get(artisTableType.getId()));
        } else {
            return EntryStacks.of(Registries.BLOCK.get(artisTableType.getId()));
        }
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.category." + artisTableType.getId().getPath());
    }

    @Override
    public List<Widget> setupDisplay(ArtisRecipeDisplay recipeDisplay, @NotNull Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - (getDisplayWidth(recipeDisplay) / 2) + 17, bounds.getCenterY() - (getDisplayHeight() / 2) + 15);

        if (artisTableType.hasCatalystSlot() && artisTableType.getHeight() == 1) {
            bounds.setSize(bounds.width, bounds.height + 18);
        }

        List<Widget> widgets;
        if (artisTableType.hasColor()) {
            widgets = new LinkedList<>(List.of(Widgets.createRecipeBase(bounds).color(artisTableType.getColor())));
        } else {
            widgets = new LinkedList<>(List.of(Widgets.createRecipeBase(bounds)));
        }

        List<EntryIngredient> input = recipeDisplay.getInputEntries();
        List<ColorableEntryWidget> slots = Lists.newArrayList();

        for (int y = 0; y < artisTableType.getHeight(); y++)
            for (int x = 0; x < artisTableType.getWidth(); x++)
                if (artisTableType.hasColor()) {
                    slots.add(ColorableEntryWidget.create(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18, artisTableType.getColor()));
                } else {
                    slots.add(ColorableEntryWidget.create(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18, 0xFFFFFF));
                }
        for (int i = 0; i < input.size(); i++) {
            if (!input.get(i).isEmpty()) {
                slots.get(getSlotWithSize(recipeDisplay, i, artisTableType.getWidth())).entries(input.get(i));
            }
        }
        widgets.addAll(slots);

        List<EntryIngredient> output = recipeDisplay.getOutputEntries();
        EntryIngredient catalyst = EntryIngredients.ofIngredient(recipeDisplay.getCatalyst());
        widgets.add(TransparentArrowWidget.create(new Point(slots.get(slots.size() - 1).getX() + 24, startPoint.y + (getDisplayHeight() / 2) - 23)).disableAnimation());
        if (artisTableType.hasColor()) {
            widgets.add(ColorableEntryWidget.create(slots.get(slots.size() - 1).getX() + 55, startPoint.y + (getDisplayHeight() / 2) - 22, artisTableType.getColor()).markOutput().entries(output.get(0)));
            if (artisTableType.hasCatalystSlot()) {
                widgets.add(ColorableEntryWidget.create(slots.get(slots.size() - 1).getX() + 28, startPoint.y + (getDisplayHeight() / 2) - 4, artisTableType.getColor()).entries(catalyst));
            }
        } else {
            widgets.add(Widgets.createSlot(new Point(slots.get(slots.size() - 1).getX() + 55, startPoint.y + (getDisplayHeight() / 2) - 22)).markOutput().entries(output.get(0)));
            if (artisTableType.hasCatalystSlot()) {
                widgets.add(Widgets.createSlot(new Point(slots.get(slots.size() - 1).getX() + 28, startPoint.y + (getDisplayHeight() / 2) - 4)).entries(catalyst));
            }
        }

        if (artisTableType.hasCatalystSlot()) {
            widgets.add(Widgets.createLabel(new Point(slots.get(slots.size() - 1).getX() + 35, startPoint.y + (getDisplayHeight() / 2) + 14), Text.literal(Formatting.RED + "-" + recipeDisplay.getCatalystCost())).centered());
        }

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 29 + (artisTableType.getHeight() * 18);
    }

    @Override
    public int getDisplayWidth(ArtisRecipeDisplay display) {
        return 90 + (artisTableType.getWidth() * 18);
    }

}