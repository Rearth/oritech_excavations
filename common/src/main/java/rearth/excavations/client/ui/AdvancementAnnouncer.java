package rearth.excavations.client.ui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import rearth.excavations.Excavation;
import rearth.excavations.announcement.Announcement;

import java.util.List;

public class AdvancementAnnouncer {
    
    private static Announcement currentAnnouncement = null;
    private static long createdAt = 0;
    private static SoundInstance currentPlayingSound = null;
    
    private static FlowLayout mainContainer;
    private static LabelComponent subtitle;
    private static LabelComponent lastSubtitle;
    
    public static void announce(Announcement announcement) {
        currentAnnouncement = announcement;
        createdAt = System.currentTimeMillis();
        mainContainer.clearChildren();
        
        var soundCandidate = Registries.SOUND_EVENT.getOrEmpty(Identifier.of(announcement.sound()));
        if (soundCandidate.isEmpty()) {
            Excavation.LOGGER.error("Invalid sound event: {}", announcement.sound());
            return;
        }
        
        if (currentPlayingSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(currentPlayingSound);
        }
        
        var soundEvent = PositionedSoundInstance.master(soundCandidate.get(), 1f, 1f);
        currentPlayingSound = soundEvent;
        MinecraftClient.getInstance().getSoundManager().play(soundEvent);
        
        subtitle = Components.label(Text.literal(announcement.subtitles().getFirst().text()));
        subtitle.margins(Insets.of(2, 0, 3, 0));
        lastSubtitle = Components.label(Text.literal(" "));
        lastSubtitle.margins(Insets.of(2, 0, 3, 0));
        
        subtitle.horizontalTextAlignment(HorizontalAlignment.RIGHT);
        lastSubtitle.horizontalTextAlignment(HorizontalAlignment.RIGHT);
        
        var color = new Color(0.2f, 0.2f, 0.2f, 0.8f);
        var colorB = new Color(0.2f, 0.2f, 0.2f, 0.4f);
        
        var subContainer = Containers.horizontalFlow(Sizing.content(1), Sizing.content(1));
        subContainer.surface(Surface.flat(color.argb()));
        
        var lastSubContainer = Containers.horizontalFlow(Sizing.content(1), Sizing.content(1));
        lastSubContainer.surface(Surface.flat(colorB.argb()));
        
        subContainer.child(subtitle);
        lastSubContainer.child(lastSubtitle);
        
        mainContainer.child(lastSubContainer);
        mainContainer.child(subContainer);
    }
    
    public static void init() {
        mainContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
        mainContainer.horizontalAlignment(HorizontalAlignment.RIGHT);
        
        Hud.add(Excavation.id("hud_progress"), () -> mainContainer
                                                       .positioning(Positioning.relative(100, 35)));
        
    }
    
    public static void tick() {
        
        if (currentAnnouncement != null) {
            var currentTime = System.currentTimeMillis();
            var currentOffset = currentTime - createdAt;
            
            var activeSub = currentAnnouncement.subtitles().getFirst().text();
            var lastSub = "";
            
            List<Announcement.Subtitle> subtitles = currentAnnouncement.subtitles();
            for (int i = 0; i < subtitles.size(); i++) {
                var subtitle = subtitles.get(i);
                if (currentOffset < subtitle.endAt()) {
                    activeSub = subtitle.text();
                    if (i > 0)
                        lastSub = subtitles.get(i - 1).text();
                    break;
                }
            }
            
            subtitle.text(Text.literal(activeSub));
            lastSubtitle.text(Text.literal(lastSub));
            
            mainContainer.layout(Size.zero());
            
            if (currentOffset >= currentAnnouncement.subtitles().getLast().endAt()) {
                // end announcement
                mainContainer.clearChildren();
                currentAnnouncement = null;
                currentPlayingSound = null;
            }
            
        }
        
    }
    
}
