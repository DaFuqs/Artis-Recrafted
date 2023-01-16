package de.dafuqs.artis.block;

import de.dafuqs.artis.*;
import de.dafuqs.artis.inventory.condenser.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.base.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class CondenserBlockEntity extends BlockEntity implements NamedScreenHandlerFactory  {

    public final SingleVariantStorage<ItemVariant> input = new SingleVariantStorage<>() {
        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant) {
            return Integer.MAX_VALUE;
        }
    };
    public final SingleVariantStorage<ItemVariant> fuel = new SingleVariantStorage<>() {
        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant) {
            return 64;
        }

        @Override
        protected void onFinalCommit() {
            // Called after a successful insertion or extraction, markDirty to ensure the new amount and variant will be saved properly.
            markDirty();
            if (!world.isClient) {
                var buf = PacketByteBufs.create();
                PlayerLookup.tracking(CondenserBlockEntity.this).forEach(player -> {
                    ServerPlayNetworking.send(player, new Identifier(Artis.MODID, "fuel_changed"), buf);
                });
            }
        }
    };
    public final SingleVariantStorage<ItemVariant> output = new SingleVariantStorage<>() {
        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant) {
            return 64;
        }

        @Override
        protected void onFinalCommit() {
            // Called after a successful insertion or extraction, markDirty to ensure the new amount and variant will be saved properly.
            markDirty();
            if (!world.isClient) {
                var buf = PacketByteBufs.create();
                PlayerLookup.tracking(CondenserBlockEntity.this).forEach(player -> {
                    ServerPlayNetworking.send(player, new Identifier(Artis.MODID, "output_changed"), buf);
                });
            }
        }
    };

    private int burnTime;
    private int cookTime;
    private int cookTimeTotal;

    protected final PropertyDelegate propertyDelegate;

    public CondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ArtisBlocks.CONDENSER_BLOCK_ENTITY, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> CondenserBlockEntity.this.burnTime;
                    case 1 -> CondenserBlockEntity.this.cookTime;
                    case 2 -> CondenserBlockEntity.this.cookTimeTotal;
                    case 3 -> (int) CondenserBlockEntity.this.input.amount;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CondenserBlockEntity.this.burnTime = value;
                    case 1 -> CondenserBlockEntity.this.cookTime = value;
                    case 2 -> CondenserBlockEntity.this.cookTimeTotal = value;
                    case 3 -> CondenserBlockEntity.this.input.amount = value;
                }

            }

            public int size() {
                return 4;
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("InputVariant", this.input.variant.toNbt());
        nbt.putLong("InputCount", this.input.amount);
        nbt.put("FuelVariant", this.fuel.variant.toNbt());
        nbt.putLong("FuelCount", this.fuel.amount);
        nbt.put("OutputVariant", this.output.variant.toNbt());
        nbt.putLong("OutputCount", this.output.amount);

        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.input.variant = ItemVariant.fromNbt(nbt.getCompound("InputVariant"));
        this.input.amount = nbt.getLong("InputCount");
        this.fuel.variant = ItemVariant.fromNbt(nbt.getCompound("FuelVariant"));
        this.fuel.amount = nbt.getLong("FuelCount");
        this.output.variant = ItemVariant.fromNbt(nbt.getCompound("OutputVariant"));
        this.output.amount = nbt.getLong("OutputCount");

        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CondenserBlockEntity blockEntity) {
        /*boolean bl = blockEntity.isBurning();
        boolean bl2 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
        }

        ItemStack itemStack = (ItemStack)blockEntity.inventory.get(1);
        if (blockEntity.isBurning() || !itemStack.isEmpty() && !((ItemStack)blockEntity.inventory.get(0)).isEmpty()) {
            Recipe<?> recipe = (Recipe)world.getRecipeManager().getFirstMatch(blockEntity.recipeType, blockEntity, world).orElse((Object)null);
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.burnTime = blockEntity.getFuelTime(itemStack);
                blockEntity.fuelTime = blockEntity.burnTime;
                if (blockEntity.isBurning()) {
                    bl2 = true;
                    if (!itemStack.isEmpty()) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            blockEntity.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }

            if (blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.cookTime;
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    blockEntity.cookTimeTotal = getCookTime(world, blockEntity.recipeType, blockEntity);
                    if (craftRecipe(recipe, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipe);
                    }

                    bl2 = true;
                }
            } else {
                blockEntity.cookTime = 0;
            }
        } else if (!blockEntity.isBurning() && blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }

        if (bl != blockEntity.isBurning()) {
            bl2 = true;
            state = (BlockState)state.with(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, 3);
        }

        if (bl2) {
            markDirty(world, pos, state);
        }*/
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.artis.condenser");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CondenserScreenHandler(syncId, inv, this);
    }

    public PropertyDelegate getPropertyDelegate() {
        return this.propertyDelegate;
    }

}
