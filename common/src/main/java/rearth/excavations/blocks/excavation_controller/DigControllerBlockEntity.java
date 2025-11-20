package rearth.excavations.blocks.excavation_controller;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.client.ui.DigControllerScreenHandler;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.entity.MachineBlockEntity;
import rearth.oritech.block.blocks.storage.UnstableContainerBlock;
import rearth.oritech.util.AutoPlayingSoundKeyframeHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static rearth.oritech.block.base.entity.MachineBlockEntity.IDLE;

public class DigControllerBlockEntity extends NetworkedBlockEntity implements GeoBlockEntity, ExtendedMenuProvider {
    
    protected final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    
    private long age = 0;
    
    public DigControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.DIG_CONTROLLER_ENTITY, pos, state);
    }
    
    @Override
    public void serverTick(World world, BlockPos pos, BlockState state, NetworkedBlockEntity blockEntity) {
        
        age++;
        if (age > 10 && !state.get(UnstableContainerBlock.SETUP_DONE)) {
            world.setBlockState(pos, state.with(UnstableContainerBlock.SETUP_DONE, true));
        }
        
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> {
            if (state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                if (this.getCachedState().get(UnstableContainerBlock.SETUP_DONE)) {
                    return state.setAndContinue(IDLE);
                } else {
                    return state.setAndContinue(MachineBlockEntity.SETUP);
                }
            }
            return PlayState.CONTINUE;
        }).setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>()));
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
    
    @Override
    public void saveExtraData(PacketByteBuf buf) {
        sendUpdate(SyncType.GUI_OPEN);
        buf.writeBlockPos(pos);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.of("");
    }
    
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DigControllerScreenHandler(syncId, playerInventory, this);
    }
}
