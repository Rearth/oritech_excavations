package rearth.excavations.announcement;

import net.minecraft.util.Identifier;

import java.util.List;

public record Announcement(String title, String sound, List<Subtitle> subtitles) {
    
    public record Subtitle(String text, int endAt) {}
    
}
