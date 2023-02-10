package de.dafuqs.artis.inventory.crafting;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.slot.*;
import io.github.cottonmc.cotton.gui.*;
import io.github.cottonmc.cotton.gui.client.*;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.*;
import net.fabricmc.api.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class ArtisRecipeProvider extends SyncedGuiDescription implements RecipeProvider {
    private final ArtisTableType tableType;
    private final PlayerEntity player;
    private final ArtisCraftingInventory craftInv;
    private final CraftingResultInventory resultInv;
    private final ScreenHandlerContext context;

    private final WPlainPanel mainPanel;
    private final WItemSlot craftingGrid;
    private final WArtisResultSlot resultSlot;
    private final WPlayerInvPanel playerInv;
    private WItemSlot catalystSlot;

    public ArtisRecipeProvider(ScreenHandlerType type, ArtisTableType tableType, int syncId, PlayerEntity player, ScreenHandlerContext context) {
        super(type, syncId, player.getInventory(), getBlockInventory(context), getBlockPropertyDelegate(context));

        this.tableType = tableType;
        this.player = player;
        this.context = context;

        this.resultInv = new CraftingResultInventory();
        this.craftInv = new ArtisCraftingInventory(this, tableType.getWidth(), tableType.getHeight());
        if (tableType.hasBlockEntity()) {
            for (int i = 0; i < blockInventory.size(); i++) {
                craftInv.setStack(i, blockInventory.getStack(i));
            }
        }
        ContainerLayout layout = new ContainerLayout(tableType.getWidth(), tableType.getHeight(), tableType.hasCatalystSlot());

        mainPanel = new WPlainPanel();
        setRootPanel(mainPanel);

        this.resultSlot = new WArtisResultSlot(player, craftInv, resultInv, 0, 1, 1, true);
        int offsetX = 8;
        mainPanel.add(resultSlot, layout.getResultX() + offsetX, layout.getResultY() + 5);

        if (getTableType().hasCatalystSlot()) {
            this.catalystSlot = WItemSlot.of(craftInv, craftInv.size() - 1);
            mainPanel.add(catalystSlot, layout.getCatalystX() + offsetX, layout.getCatalystY() + 1);

            WLabel catalystCost = new WLabel(Text.empty(), 0xAA0000).setHorizontalAlignment(HorizontalAlignment.CENTER);
            mainPanel.add(catalystCost, layout.getCatalystX() + offsetX, layout.getCatalystY() + 19);
        }

        this.craftingGrid = WItemSlot.of(craftInv, 0, getTableType().getWidth(), getTableType().getHeight());
        mainPanel.add(craftingGrid, layout.getGridX() + offsetX, layout.getGridY() + 1);

        this.playerInv = this.createPlayerInventoryPanel();
        mainPanel.add(playerInv, layout.getPlayerX() + offsetX, layout.getPlayerY());

        WLabel label = new WLabel(tableType.getName(), 0x404040);
        mainPanel.add(label, 8, 6);

        WSprite arrow = new WSprite(new Identifier(Artis.MODID, "textures/gui/translucent_arrow.png"));
        mainPanel.add(arrow, layout.getArrowX() + offsetX, layout.getArrowY() + 5, 22, 15);

        mainPanel.validate(this);
        craftInv.setCheckMatrixChanges(true);

        int width = Math.max(176, 74 + tableType.getWidth() * 18);
        int height;
        if (tableType.hasCatalystSlot()) {
            height = Math.max(150, 120 + tableType.getHeight() * 18);
        } else {
            height = Math.max(140, 120 + tableType.getHeight() * 18);
        }
        mainPanel.setSize(width, height);
    }

    private static BackgroundPainter slotColor(int color) {
        return (matrices, left, top, panel) -> {
            int lo = ScreenDrawing.multiplyColor(color, 0.5F);
            int bg = 0x4C000000;
            int hi = ScreenDrawing.multiplyColor(color, 1.25F);
            if (!(panel instanceof WItemSlot slot)) {
                ScreenDrawing.drawBeveledPanel(matrices, left - 1, top - 1, panel.getWidth() + 2, panel.getHeight() + 2, lo, bg, hi);
            } else {
                for (int x = 0; x < slot.getWidth() / 18; ++x) {
                    for (int y = 0; y < slot.getHeight() / 18; ++y) {
                        if (slot.isBigSlot()) {
                            ScreenDrawing.drawBeveledPanel(matrices, x * 18 + left - 3, y * 18 + top - 3, 24, 24, lo, bg, hi);
                        } else {
                            ScreenDrawing.drawBeveledPanel(matrices, x * 18 + left, y * 18 + top, 18, 18, lo, bg, hi);
                        }
                    }
                }
            }
        };
    }

    public ArtisCraftingInventory getCraftInv() {
        return craftInv;
    }

    public CraftingResultInventory getResultInv() {
        return resultInv;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addPainters() {
        int color = tableType.getColor();
        if (tableType.hasColor()) {
            mainPanel.setBackgroundPainter(BackgroundPainter.createColorful(color));
            craftingGrid.setBackgroundPainter(slotColor(color));
            if (tableType.hasCatalystSlot())
                catalystSlot.setBackgroundPainter(slotColor(color));
            resultSlot.setBackgroundPainter(slotColor(color));
            playerInv.setBackgroundPainter(slotColor(color));
        } else {
            mainPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
            craftingGrid.setBackgroundPainter(BackgroundPainter.SLOT);
            if (tableType.hasCatalystSlot())
                catalystSlot.setBackgroundPainter(BackgroundPainter.SLOT);
            resultSlot.setBackgroundPainter(BackgroundPainter.SLOT);
            playerInv.setBackgroundPainter(BackgroundPainter.SLOT);
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, pos) -> {
            if (!tableType.hasBlockEntity()) {
                dropInventory(player, craftInv);
            } else {
                for (int i = 0; i < craftInv.size(); i++) {
                    blockInventory.setStack(i, craftInv.getStack(i));
                }
            }
        });
    }

    @Override
    public int getCraftingWidth() {
        return tableType.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return tableType.getHeight();
    }

    @Override
    public boolean matches(Recipe recipe) {
        return recipe.matches(craftInv, player.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingSlotCount() {
        return getTableType().getWidth() * getTableType().getHeight();
    }

    public static void updateResult(World world, ArtisCraftingInventory inv, CraftingResultInventory resultInv, ArtisTableType artisTableType) {
        ItemStack itemstack = ItemStack.EMPTY;

        Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) resultInv.getLastRecipe();
        //find artis recipe first
        if (recipe == null || (recipe instanceof ArtisCraftingRecipe ? !recipe.matches(inv, world) : !recipe.matches(inv.getCraftingInventory(), world))) {
            recipe = findArtisRecipe(artisTableType, inv, world);
        }
        //else fall back to vanilla
        if (recipe == null && artisTableType.shouldIncludeNormalRecipes()) {
            recipe = findVanillaRecipe(inv, world);
        }

        if (recipe != null) {
            itemstack = recipe.craft(inv);
        }

        resultInv.setStack(0, itemstack);
        if (recipe != null) {
            resultInv.setLastRecipe(recipe);
        }
    }

    @Override
    public void onContentChanged(Inventory inv) {
        super.onContentChanged(resultInv);
        updateResult(world, craftInv, resultInv, tableType);
    }

    @Override
    public ArtisTableType getTableType() {
        return tableType;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            int tableSlotCount = getCraftingSlotCount() + 1 + (tableType.hasCatalystSlot() ? 1 : 0);
            if (slotIndex == getCraftingResultSlotIndex()) {
                return handleShiftCraft(player, this, slot, craftInv, resultInv, tableSlotCount, tableSlotCount + 36);
            }
            ItemStack toTake = slot.getStack();
            stack = toTake.copy();

            if (slotIndex < tableSlotCount) {
                if (!this.insertItem(stack, tableSlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack, 0, tableSlotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotIndex == getCraftingResultSlotIndex()) {
                player.dropItem(toTake, false);
            }

            slot.onTakeItem(player, toTake);

        }

        return stack;
    }

    @Override
    public void onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        if (slotNumber == getCraftingResultSlotIndex() && action == SlotActionType.QUICK_MOVE) {
            transferSlot(player, slotNumber);
        } else {
            super.onSlotClick(slotNumber, button, action, player);
        }
    }

    @Override
    public void populateRecipeMatcher(RecipeMatcher finder) {
        this.craftInv.provideRecipeInputs(finder);
    }

    @Override
    public void clearCraftingSlots() {
        this.craftInv.clear();
        this.resultInv.clear();
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
    }

    public static ItemStack handleShiftCraft(PlayerEntity player, ArtisRecipeProvider container, Slot resultSlot, ArtisCraftingInventory input, CraftingResultInventory craftResult, int outStart, int outEnd) {
        ItemStack outputCopy = ItemStack.EMPTY;
        input.setCheckMatrixChanges(false);
        if (resultSlot != null && resultSlot.hasStack()) {

            Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) craftResult.getLastRecipe();
            if (recipe == null && container.tableType.shouldIncludeNormalRecipes()) {
                recipe = findVanillaRecipe(input, player.world);
            }
            while (recipe != null && recipe.matches(input, player.world)) {
                ItemStack recipeOutput = resultSlot.getStack().copy();
                outputCopy = recipeOutput.copy();

                recipeOutput.getItem().onCraft(recipeOutput, player.world, player);

                if (!player.world.isClient && !container.insertItem(recipeOutput, outStart, outEnd, true)) {
                    input.setCheckMatrixChanges(true);
                    return ItemStack.EMPTY;
                }

                resultSlot.onQuickTransfer(recipeOutput, outputCopy);
                resultSlot.markDirty();

                if (!player.world.isClient && recipeOutput.getCount() == outputCopy.getCount()) {
                    input.setCheckMatrixChanges(true);
                    return ItemStack.EMPTY;
                }

                resultSlot.onTakeItem(player, recipeOutput);
                player.dropItem(recipeOutput, false);
            }
            input.setCheckMatrixChanges(true);
            updateResult(player.world, input, craftResult, container.tableType);
        }
        input.setCheckMatrixChanges(true);
        return craftResult.getLastRecipe() == null ? ItemStack.EMPTY : outputCopy;
    }

    public static Recipe<CraftingInventory> findArtisRecipe(ArtisTableType tableType, ArtisCraftingInventory inv, World world) {
        return (Recipe<CraftingInventory>) world.getRecipeManager().getFirstMatch(tableType, inv, world).orElse(null);
    }

    public static Recipe<CraftingInventory> findVanillaRecipe(ArtisCraftingInventory inv, World world) {
        return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv.getCraftingInventory(), world).orElse(null);
    }

}
