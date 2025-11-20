package rearth.excavations.client.ui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity;
import rearth.excavations.client.init.ScreenContent;
import rearth.oritech.api.networking.SyncType;

import java.util.Objects;

public class DigControllerScreenHandler extends ScreenHandler {
    
    public final DigControllerBlockEntity controllerEntity;
    public final World world;
    
    // this calls the second version
    public DigControllerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())));
    }
    
    // on server, also called from client constructor
    public DigControllerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ScreenContent.DIG_CONTROLLER_SCREEN, syncId);
        
        controllerEntity = (DigControllerBlockEntity) blockEntity;
        world = blockEntity.getWorld();
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        controllerEntity.sendUpdate(SyncType.GUI_TICK);
    }
}
