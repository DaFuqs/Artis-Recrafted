/*package io.github.alloffabric.artis.compat.rei;

import com.google.common.collect.Lists;
import io.github.alloffabric.artis.inventory.ArtisRecipeProvider;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.rei.RoughlyEnoughItemsNetwork;
import me.shedaniel.rei.api.AutoTransferHandler;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeDisplay;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.ContainerInfoHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ArtisRecipeTransferHandler implements AutoTransferHandler {

    public static boolean canUseMovePackets() {
        return ClientSidePacketRegistry.INSTANCE.canServerReceive(RoughlyEnoughItemsNetwork.MOVE_ITEMS_PACKET);
    }

    @Override
    public Result handle(Context context) {
        if (!(context.getContainer() instanceof ArtisRecipeProvider))
            return Result.createNotApplicable();
        if (context.getContainer() instanceof ArtisRecipeProvider && !(context.getRecipe() instanceof ArtisRecipeDisplay) && context.getRecipe().getRecipeCategory().equals(DefaultPlugin.CRAFTING) && !((ArtisRecipeProvider) context.getContainer()).getTableType().shouldIncludeNormalRecipes())
            return Result.createNotApplicable().blocksFurtherHandling(false);
        TransferRecipeDisplay recipe = (TransferRecipeDisplay) context.getRecipe();
        HandledScreen<?> containerScreen = context.getContainerScreen();
        ArtisRecipeProvider container = (ArtisRecipeProvider) context.getContainer();
        ContainerInfo<ScreenHandler> containerInfo = (ContainerInfo<ScreenHandler>) ContainerInfoHandler.getContainerInfo(recipe.getRecipeCategory(), container.getClass());
        RecipeProviderInfoWrapper<ArtisRecipeProvider> recipeProvider = (RecipeProviderInfoWrapper<ArtisRecipeProvider>) ContainerInfoHandler.getContainerInfo(recipe.getRecipeCategory(), container.getClass());
        if (containerInfo == null || context.getRecipe() instanceof ArtisRecipeDisplay && !recipeProvider.getTableType(container).getId().equals(recipe.getRecipeCategory()))
            return Result.createNotApplicable().blocksFurtherHandling(false);
        if (recipe.getHeight() > containerInfo.getCraftingHeight(container) || recipe.getWidth() > containerInfo.getCraftingWidth(container))
            return Result.createFailed(I18n.translate("error.rei.transfer.too_small", containerInfo.getCraftingWidth(container), containerInfo.getCraftingHeight(container)));
        List<List<EntryStack>> input = recipe.getOrganisedInputEntries(containerInfo, container);
        if (!(context.getRecipe() instanceof ArtisRecipeDisplay) && container.getTableType().hasCatalystSlot()) {
            List<List<EntryStack>> out = Lists.newArrayListWithCapacity(input.size() + 1);
            out.add(Stream.of(ItemStack.EMPTY).map(EntryStack::create).collect(Collectors.toList()));
            out.addAll(input);

            input = out;
        }
        IntList intList = hasItems(input);
        if (!intList.isEmpty())
            return Result.createFailed("error.rei.not.enough.materials", intList);
        if (!canUseMovePackets())
            return Result.createFailed("error.rei.not.on.server");
        if (!context.isActuallyCrafting())
            return Result.createSuccessful();

        context.getMinecraft().openScreen(containerScreen);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(recipe.getRecipeCategory());
        buf.writeBoolean(Screen.hasShiftDown());

        buf.writeInt(input.size());
        for (List<EntryStack> stacks : input) {
            buf.writeInt(stacks.size());
            for (EntryStack stack : stacks) {
                if (stack.getItemStack() != null)
                    buf.writeItemStack(stack.getItemStack());
                else
                    buf.writeItemStack(ItemStack.EMPTY);
            }
        }
        ClientSidePacketRegistry.INSTANCE.sendToServer(RoughlyEnoughItemsNetwork.MOVE_ITEMS_PACKET, buf);
        return Result.createSuccessful();
    }

    @Override
    public double getPriority() {
        return -9;
    }

    public IntList hasItems(List<List<EntryStack>> inputs) {
        // Create a clone of player's inventory, and count
        DefaultedList<ItemStack> copyMain = DefaultedList.of();
        for (ItemStack stack : MinecraftClient.getInstance().player.inventory.main) {
            copyMain.add(stack.copy());
        }
        IntList intList = new IntArrayList();
        for (int i = 0; i < inputs.size(); i++) {
            List<EntryStack> possibleStacks = inputs.get(i);
            boolean done = possibleStacks.isEmpty();
            for (EntryStack possibleStack : possibleStacks) {
                if (!done) {
                    int invRequiredCount = possibleStack.getAmount();
                    for (ItemStack stack : copyMain) {
                        EntryStack entryStack = EntryStack.create(stack);
                        if (entryStack.equals(possibleStack)) {
                            while (invRequiredCount > 0 && !stack.isEmpty()) {
                                invRequiredCount--;
                                stack.decrement(1);
                            }
                        }
                    }
                    if (invRequiredCount <= 0) {
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                intList.add(i);
            }
        }
        return intList;
    }

    public IntList hasItems(List<List<EntryStack>> inputs, EntryStack catalyst) {
        // Create a clone of player's inventory, and count
        DefaultedList<ItemStack> copyMain = DefaultedList.of();
        for (ItemStack stack : MinecraftClient.getInstance().player.inventory.main) {
            copyMain.add(stack.copy());
        }
        IntList intList = new IntArrayList();
        for (int i = 0; i < inputs.size(); i++) {
            List<EntryStack> possibleStacks = inputs.get(i);
            boolean done = possibleStacks.isEmpty();
            for (EntryStack possibleStack : possibleStacks) {
                if (!done) {
                    int invRequiredCount = possibleStack.getAmount();
                    for (ItemStack stack : copyMain) {
                        EntryStack entryStack = EntryStack.create(stack);
                        if (entryStack.equals(possibleStack)) {
                            while (invRequiredCount > 0 && !stack.isEmpty()) {
                                invRequiredCount--;
                                stack.decrement(1);
                            }
                        }
                        if (entryStack.equals(catalyst)) {
                            while (invRequiredCount > 0 && !stack.isEmpty()) {
                                invRequiredCount--;
                                stack.decrement(1);
                            }
                        }
                    }
                    if (invRequiredCount <= 0) {
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                intList.add(i);
            }
        }
        return intList;
    }
}
*/