package rearth.excavations.client.ui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity;
import rearth.oritech.api.networking.NetworkManager;
import rearth.oritech.client.ui.BasicMachineScreen;

public class DigControllerScreen extends BaseOwoHandledScreen<FlowLayout, DigControllerScreenHandler> {
    
    public DigControllerScreen(DigControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    
    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }
    
    @Override
    protected void build(FlowLayout rootComponent) {
        
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.CENTER);
        
        var reloadButton = Components.button(Text.translatable("reload"), elem -> onReloadHoleClicked());
        
        var mainPanel = Containers.horizontalFlow(Sizing.content(4), Sizing.content(4));
        mainPanel.surface(BasicMachineScreen.ORITECH_PANEL);
        
        var diagramPanel = Containers.verticalFlow(Sizing.content(0), Sizing.content(0));
        diagramPanel.surface(Surface.PANEL_INSET);
        diagramPanel.padding(Insets.of(3));
        
        createDiagram(diagramPanel);
        
        mainPanel.child(diagramPanel);
        
        rootComponent.child(mainPanel);
        
        mainPanel.child(reloadButton);
        
    }
    
    private void createDiagram(FlowLayout container) {
        
        var progressData = this.handler.controllerEntity.getScanResults();
        
        // todo make those constants in dig controller entity
        var bottom = -512;
        var top = 208;
        var height = top - bottom;
        var segments = height / 16;
        
        var startWidth = 0;
        
        for (int i = segments; i >= 0; i--) {
            
            var segmentY = bottom + i * 16;
            
            var progress = progressData.getOrDefault(segmentY, DigControllerBlockEntity.SegmentScan.NOT_SCANNED);
            
            var desiredRadius = DigControllerBlockEntity.GetDesiredRadiusAtY(segmentY);
            var width = 3 + desiredRadius * 3;
            var leftOffset = 0;
            
            if (startWidth == 0) {
                startWidth = width;
            } else {
                var sizeDiff = startWidth - width;
                leftOffset = sizeDiff / 2;
            }
            
            var added = Components.box(Sizing.fixed(width), Sizing.fixed(5));
            added.color(new Color(0.2f, 0.3f, 0.7f, 0.4f));
            added.fill(true);
            added.margins(Insets.of(0, 0, leftOffset, 0));
            
            added.tooltip(Text.literal("R: " + desiredRadius + " Y: " + segmentY + " P: " + progress.progress()));
            
            container.child(added);
            if (!progress.equals(DigControllerBlockEntity.SegmentScan.NOT_SCANNED)) {
                
                var progressWidth = (int) (width * progress.progress());
                var progressSizeDiff = startWidth - progressWidth;
                var progressOffset = progressSizeDiff / 2;
                
                var currentY = (segments - i)  * 5;
                
                var overlay = Components.box(Sizing.fixed(progressWidth), Sizing.fixed(5));
                overlay.fill(true);
                overlay.color(Color.RED);
                overlay.margins(Insets.of(0, 0, progressOffset, 0));
                overlay.positioning(Positioning.absolute(0, currentY));
                
                container.child(overlay);
                
            }
            
        }
        
    }
    
    private void onReloadHoleClicked() {
        
        NetworkManager.sendToServer(new DigControllerBlockEntity.DigTriggerReloadPacket(this.handler.controllerEntity.getPos()));
        this.close();
        
        
        this.client.getToastManager().add(  // says "assembled, drone has been added to inv"
          SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("title.excavations.hole_search"), Text.translatable("tooltip.excavations.hole_search"))
        );
        
    }
}
