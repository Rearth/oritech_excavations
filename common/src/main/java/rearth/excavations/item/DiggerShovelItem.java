package rearth.excavations.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class DiggerShovelItem extends Item {
    
    public final int hardness;
    public final float speed;
    
    public DiggerShovelItem(Settings settings, int hardness, float speed) {
        super(settings);
        this.hardness = hardness;
        this.speed = speed;
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.oritech_excavations.shovel_hardness", hardness));
        tooltip.add(Text.translatable("tooltip.oritech_excavations.shovel_speed", speed));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
