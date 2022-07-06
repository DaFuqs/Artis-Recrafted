package de.dafuqs.artis.block;

import de.dafuqs.artis.*;
import de.dafuqs.artis.api.ArtisTableType;
import de.dafuqs.artis.inventory.crafting.ArtisRecipeProvider;
import de.dafuqs.artis.inventory.crafting.DefaultInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class ArtisTableBlockEntity extends BlockEntity implements DefaultInventory, ExtendedScreenHandlerFactory {
    
    private ArtisTableType tableType;
    private DefaultedList<ItemStack> stacks;

    public ArtisTableBlockEntity(BlockPos pos, BlockState state) {
        super(ArtisBlocks.ARTIS_BLOCK_ENTITY, pos, state);
    }

    public ArtisTableBlockEntity(@NotNull ArtisTableType tableType, BlockPos pos, BlockState state) {
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
        return new ArtisRecipeProvider(Registry.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(world, getPos()));
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

}