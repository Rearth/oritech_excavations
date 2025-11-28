package rearth.excavations.client.ui;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import rearth.excavations.Excavation;
import rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity;
import rearth.oritech.api.networking.NetworkManager;
import rearth.oritech.client.ui.BasicMachineScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity.SCAN_END_Y;
import static rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity.SCAN_START_Y;

public class DigControllerScreen extends BaseOwoHandledScreen<FlowLayout, DigControllerScreenHandler> {
    
    private static final Identifier BIG_BUTTON_TEXTURE = Excavation.id("textures/gui/big_button.png");
    private static final Identifier BIG_BUTTON_TEXTURE_DISABLED = Excavation.id("textures/gui/big_button_disabled.png");
    private static final Identifier BIG_BUTTON_HOVER_TEXTURE = Excavation.id("textures/gui/big_button_hover.png");
    private static final Identifier BIG_BUTTON_PRESSED_TEXTURE = Excavation.id("textures/gui/big_button_pressed.png");
    
    private final long openedAt;
    
    public DigControllerScreen(DigControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.openedAt = inventory.player.getWorld().getTime();
    }
    
    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }
    
    @Override
    protected void build(FlowLayout rootComponent) {
        
        var textColor = 13685204;
        
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.CENTER);
        var mainPanel = Containers.horizontalFlow(Sizing.content(0), Sizing.content(0));
        mainPanel.padding(Insets.of(6));
        mainPanel.surface(BasicMachineScreen.ORITECH_PANEL);
        
        var diagramPanel = Containers.verticalFlow(Sizing.content(0), Sizing.content(0));
        diagramPanel.surface(Surface.PANEL_INSET);
        diagramPanel.padding(Insets.of(2, 2, 2, 3));
        
        createDiagram(diagramPanel);
        
        mainPanel.child(diagramPanel);
        
        // right panel
        var rightPanel = Containers.verticalFlow(Sizing.content(0), Sizing.content());
        rightPanel.surface(Surface.PANEL_INSET);
        rightPanel.padding(Insets.of(2, 2, 2, 3));
        rightPanel.margins(Insets.of(0, 0, 5, 0));
        
        var titleLabel = Components.label(Text.literal("Project Scanner").formatted(Formatting.BOLD));
        titleLabel.color(Color.WHITE);
        titleLabel.margins(Insets.of(4, 4, 1, 0));
        
        var reloadButton = Components.button(Text.translatable(" \uD83D\uDD04 ".formatted(Formatting.BLACK, Formatting.BOLD)), elem -> onReloadHoleClicked());
        reloadButton.renderer(BasicMachineScreen.ORITECH_BUTTON_DARK);
        reloadButton.sizing(Sizing.fixed(20), Sizing.fixed(20));
        reloadButton.margins(Insets.of(5, 2, 0, 0));
        reloadButton.tooltip(List.of(Text.translatable("tooltip.excavations.reload_scan"), Text.literal("Center: ").append(Text.literal(this.handler.controllerEntity.getHoleCenter().toShortString()))));
        
        var progressContainer = Containers.verticalFlow(Sizing.content(), Sizing.content(0));
        var progressLabel = Components.label(Text.literal("Progress:"));
        progressLabel.color(Color.ofRgb(textColor));
        progressLabel.margins(Insets.of(4, 1, 1, 0));
        
        var progressBar = new ProgressBar(Sizing.fixed(130), Sizing.fixed(7));
        progressBar.progress = 0.1f;
        progressBar.margins(Insets.of(3));
        
        progressContainer.child(progressLabel);
        progressContainer.child(progressBar);
        
        
        var bigButton = new BigAssembleButton(0, 0, 0, 0, Text.literal("BUILD!").formatted(Formatting.BOLD), btn -> {}, btn -> {
            System.out.println("big button pressed");
        });
        bigButton.active = false;
        bigButton.positioning(Positioning.layout());
        bigButton.sizing(Sizing.fixed(138), Sizing.fixed(59));
        
        
        rightPanel.child(titleLabel);
        rightPanel.child(progressContainer);
        rightPanel.child(reloadButton);
        
        mainPanel.child(rightPanel);
        mainPanel.child(bigButton.margins(Insets.of(3, 3, 0, 2)).positioning(Positioning.relative(100, 100)));
        
        rootComponent.child(mainPanel);
        
    }
    
    
    private void createDiagram(FlowLayout container) {
        
        var progressData = this.handler.controllerEntity.getScanResults();
        
        var bottom = SCAN_START_Y;
        var top = SCAN_END_Y;
        var height = top - bottom;
        var segments = height / 16;
        
        var startWidth = 0;
        
        for (int i = segments; i >= 0; i--) {
            
            final var segmentY = bottom + i * 16;
            
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
            
            var added = new HoleSegmentComponent(Sizing.fixed(width), Sizing.fixed(5),
              () -> this.handler.controllerEntity.getScanResults().getOrDefault(segmentY, DigControllerBlockEntity.SegmentScan.NOT_SCANNED),
              () -> this.handler.controllerEntity.lastScanY == segmentY && this.handler.world.getTime() - this.handler.controllerEntity.lastScanTime < 30);
            added.color(new Color(0.2f, 0.3f, 0.7f, 0.4f));
            added.fill(true);
            added.margins(Insets.of(0, 0, leftOffset + 5, 5));
            
            List<Text> tooltip = List.of(
              Text.literal("Y: " + segmentY + " to " + (segmentY + 16)),
              Text.literal("Target Radius: " + desiredRadius)
            );
            
            added.tooltip(tooltip);
            
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
    
    private static class HoleSegmentComponent extends BoxComponent {
        
        public final Supplier<DigControllerBlockEntity.SegmentScan> dataSupplier;
        public final Supplier<Boolean> freshlyScannedSupplier;
        
        public HoleSegmentComponent(Sizing horizontalSizing, Sizing verticalSizing, Supplier<DigControllerBlockEntity.SegmentScan> dataSupplier, Supplier<Boolean> freshlyScannedSupplier) {
            super(horizontalSizing, verticalSizing);
            this.dataSupplier = dataSupplier;
            this.freshlyScannedSupplier = freshlyScannedSupplier;
        }
        
        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            final int backColor = new Color(73 / 255f, 107 / 255f, 117 / 255f, 1f).argb();
            final int backSideColor = new Color(73 / 255f, 164 / 255f, 235 / 255f, 1f).argb();
            
            var freshlyScanned = freshlyScannedSupplier.get();
            var alphaFactor = 1f;
            if (freshlyScanned)
                alphaFactor = (float) (Math.sin(System.currentTimeMillis() / 128D + this.y * 20) * 0.3f + 0.5f);
            
            final int initColor = backSideColor;
            final int lowColor = new Color(219 / 255f, 44 / 255f, 9 / 255f, alphaFactor).argb();
            final int mediumColor = new Color(255 / 255f, 128 / 255f, 9 / 255f, alphaFactor).argb();
            final int highColor = new Color(89 / 255f, 255 / 255f, 0 / 255f, alphaFactor).argb();
            final int overflowColor = new Color(89 / 255f, 255 / 255f, 0 / 255f, 0.6f * alphaFactor).argb();
            
            context.drawGradientRect(this.x, this.y, this.width, this.height, backColor, backColor, backColor, backColor);
            context.drawGradientRect(this.x, this.y, 1, this.height, backSideColor, backSideColor, backSideColor, backSideColor);
            context.drawGradientRect(this.x + this.width - 1, this.y, 1, this.height, backSideColor, backSideColor, backSideColor, backSideColor);
            
            var data = this.dataSupplier.get();
            var progress = data.progress();
            progress = Math.min(progress, 1.5);
            
            var frontColor = lowColor;
            if (progress > 0.5f)
                frontColor = mediumColor;
            if (progress > 0.99f)
                frontColor = highColor;
            if (progress > 1.02f)
                frontColor = overflowColor;
            
            var stillScanning = data.foundMinRadius() < 0;
            if (stillScanning || (progress <= 0.1 && freshlyScanned)) {
                frontColor = initColor;
                var bufferSize = 0.05f;
                progress += Math.sin(System.currentTimeMillis() / 200D + this.y) * bufferSize + bufferSize + 0.05;
            }
            
            var progressWidth = (int) (this.width * progress);
            var progressOffset = (this.width - progressWidth) / 2;
            
            context.drawGradientRect(this.x + progressOffset, this.y, progressWidth, this.height, frontColor, frontColor, frontColor, frontColor);
            
            if (stillScanning) {
                var markerColor = Color.WHITE.argb();
                context.drawGradientRect(this.x + progressOffset, this.y, 1, this.height, markerColor, markerColor, markerColor, markerColor);
                context.drawGradientRect(this.x + progressOffset + progressWidth - 1, this.y, 1, this.height, markerColor, markerColor, markerColor, markerColor);
            }
            
            context.drawGradientRect(this.x, this.y, 1, this.height, backSideColor, backSideColor, backSideColor, backSideColor);
            context.drawGradientRect(this.x + this.width - 1, this.y, 1, this.height, backSideColor, backSideColor, backSideColor, backSideColor);
            // super.draw(context, mouseX, mouseY, partialTicks, delta);
        }
        
        @Override
        public List<TooltipComponent> tooltip() {
            var result = new ArrayList<TooltipComponent>();
            result.addAll(super.tooltip());
            result.add(TooltipComponent.of((Text.literal("Progress: " + (int) (this.dataSupplier.get().progress() * 100) + "%").asOrderedText())));
            return result;
        }
    }
    
    private static class ProgressBar extends BoxComponent {
        
        public float progress;
        
        public ProgressBar(Sizing horizontalSizing, Sizing verticalSizing) {
            super(horizontalSizing, verticalSizing);
        }
        
        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            
            var greenColor = -12810969;
            var orangeColor = -1012726;
            var markerColor = -3092012;
            var endColor = -526345;
            
            var mainColor = progress < 1 ? orangeColor : greenColor;
            
            var mainHeight = this.height();
            
            drawBarPart(context, 0, progress, mainColor, mainHeight, 0);    // main bar
            drawBarPart(context, 0.246f, 0.252f, markerColor, mainHeight - 1, 1);    // progress step 1
            drawBarPart(context, 0.498f, 0.502f, markerColor, mainHeight - 1, 1);    // progress step 2
            drawBarPart(context, 0.746f, 0.752f, markerColor, mainHeight - 1, 1);    // progress step 3
            drawBarPart(context, progress - 0.005f, progress + 0.005f, endColor, mainHeight, 0); // end step
            
            context.drawRectOutline(this.x - 1, this.y - 1, this.width + 2, this.height + 2, Color.BLACK.argb());
            
        }
        
        private void drawBarPart(DrawContext context, float fillStart, float fillEnd, int color, int height, int yOffset) {
            
            var barWidth = this.width();
            
            var fromX = this.x() + (int) (barWidth * fillStart);
            var fromY = this.y() + yOffset;
            var toX = this.x + (int) (barWidth * fillEnd);
            var toY = this.y() + height;
            
            context.fill(fromX, fromY, toX, toY, 10, color);
        }
    }
    
    private static class BigAssembleButton extends ButtonWidget {
        
        private boolean isPressed = false;
        protected final PressAction onRelease;
        
        protected BigAssembleButton(int x, int y, int width, int height, Text message, PressAction onPress, PressAction onRelease) {
            super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.onRelease = onRelease;
        }
        
        @Override
        public void onPress() {
            isPressed = true;
            
            super.onPress();
        }
        
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            var valid = super.mouseReleased(mouseX, mouseY, button);
            if (valid && isPressed) {
                isPressed = false;
                playUpSound(MinecraftClient.getInstance().getSoundManager());
                onRelease.onPress(this);
            }
            
            return valid;
        }
        
        @Override
        public void playDownSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, 0.7F));
        }
        
        public void playUpSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.BLOCK_BEACON_DEACTIVATE, 0.7F));
        }
        
        @SuppressWarnings("lossy-conversions")
        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            
            var usedTexture = BIG_BUTTON_TEXTURE;
            
            if (this.isHovered())
                usedTexture = BIG_BUTTON_HOVER_TEXTURE;
            
            if (isPressed)
                usedTexture = BIG_BUTTON_PRESSED_TEXTURE;
            
            if (!this.active) {
                usedTexture = BIG_BUTTON_TEXTURE_DISABLED;
            }
            
            
            context.drawTexture(usedTexture, this.getX(), this.getY(), 0, 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
            
            var scale = 3.4f;
            
            var textX = this.getX() + 11;
            var textY = this.getY() + 13;
            
            var textColor = 13685204;
            
            if (this.isHovered())
                textY += 4;
            
            if (this.isPressed)
                textY += 6;
            
            if (!this.active)
                textY = this.getY() + 24;
            
            
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, scale);
            
            textX /= scale;
            textY /= scale;
            
            context.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(), textX, textY, textColor, false);
            
            context.getMatrices().pop();
        }
    }
}
