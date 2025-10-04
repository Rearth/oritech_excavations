package rearth.excavations.blocks.quarry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import rearth.oritech.block.blocks.interaction.DeepDrillBlock;

import java.util.List;

public class PrimitiveDeepDrillBlock extends DeepDrillBlock {
    
    public PrimitiveDeepDrillBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PrimitiveDeepDrillEntity(pos, state);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        var showExtra = Screen.hasControlDown();
        
        if (showExtra) {
            var rate = 64;
            tooltip.add(Text.translatable("tooltip.oritech.core_desc").formatted(Formatting.GRAY).append(Text.literal("1").formatted(Formatting.GOLD)));
            tooltip.add(Text.translatable("tooltip.oritech.machine_rate_desc").formatted(Formatting.GRAY).append(Text.translatable("tooltip.oritech.energy_transfer_rate", rate).formatted(Formatting.GOLD)));
            
            
            var id = Registries.BLOCK.getId(this);
            if (I18n.hasTranslation("tooltip.oritech." + id.getPath() + ".extra")) {
                tooltip.add(Text.translatable("tooltip.oritech." + id.getPath() + ".extra").formatted(Formatting.GRAY));
            }
            
        } else {
            tooltip.add(Text.translatable("tooltip.oritech.item_extra_info").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
        }
    }
}
