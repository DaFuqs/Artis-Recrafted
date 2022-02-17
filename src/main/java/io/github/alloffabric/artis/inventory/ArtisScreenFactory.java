package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;

public record ArtisScreenFactory(ArtisTableType tableType, Block block, BlockHitResult blockHitResult) implements ExtendedScreenHandlerFactory {

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if(this.block == null || this.blockHitResult == null) {
            return new ArtisRecipeProvider(Registry.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(player.world, player.getBlockPos()));
        } else {
            return new ArtisRecipeProvider(Registry.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(player.world, blockHitResult.getBlockPos()));
        }
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText(tableType.getName());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        if(blockHitResult == null) {
            packetByteBuf.writeBlockPos(serverPlayerEntity.getBlockPos());
        } else {
            packetByteBuf.writeBlockPos(blockHitResult.getBlockPos());
        }
    }
    
}
