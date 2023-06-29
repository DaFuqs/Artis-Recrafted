package de.dafuqs.artis.block;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.*;
import de.dafuqs.artis.inventory.crafting.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import net.minecraft.screen.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

public class ArtisTableBlockEntity extends BlockEntity implements DefaultInventory, ExtendedScreenHandlerFactory, RecipeInputProvider {
	
	private ArtisCraftingRecipeType tableType;
	private DefaultedList<ItemStack> stacks;
	
	public ArtisTableBlockEntity(BlockPos pos, BlockState state) {
		super(ArtisBlocks.ARTIS_BLOCK_ENTITY, pos, state);
	}
	
	public ArtisTableBlockEntity(@NotNull ArtisCraftingRecipeType tableType, BlockPos pos, BlockState state) {
		super(ArtisBlocks.ARTIS_BLOCK_ENTITY, pos, state);
		
		this.tableType = tableType;
		this.stacks = DefaultedList.ofSize((tableType.getWidth() * tableType.getHeight()) + 1, ItemStack.EMPTY);
	}
	
	@Override
	public Text getDisplayName() {
		return Text.empty();
	}
	
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
		return new ArtisRecipeProvider(Registries.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(world, getPos()));
	}
	
	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, @NotNull PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
	
	@Override
	public DefaultedList<ItemStack> getItems() {
		return stacks;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		tableType = ArtisBlocks.ARTIS_TABLE_TYPES.get(new Identifier(nbt.getString("tableType")));
		stacks = DefaultedList.ofSize((tableType.getWidth() * tableType.getHeight()) + 1, ItemStack.EMPTY);
		Inventories.readNbt(nbt, stacks);
	}
	
	@Override
	public void writeNbt(NbtCompound nbt) {
		if (tableType != null)
			nbt.putString("tableType", tableType.getId().toString());
		
		if (stacks != null)
			Inventories.writeNbt(nbt, stacks);
	}
	
	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
	
	}
	
}