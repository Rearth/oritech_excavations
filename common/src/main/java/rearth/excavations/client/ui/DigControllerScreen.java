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
        
        var diagramPanel = Containers.verticalFlow(Sizing.content(4), Sizing.content(4));
        diagramPanel.surface(Surface.PANEL_INSET);
        
        createDiagram(diagramPanel);
        
        mainPanel.child(diagramPanel);
        
        rootComponent.child(mainPanel);
        
        mainPanel.child(reloadButton);
        
    }
    
    private void createDiagram(FlowLayout container) {
        
        for (int i = 0; i < 10; i++) {
            
            var added = Components.box(Sizing.fixed(20 + i * 2), Sizing.fixed(4));
            added.color(new Color(50, i * 20, 100));
            
            
            container.child(added);
            
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
