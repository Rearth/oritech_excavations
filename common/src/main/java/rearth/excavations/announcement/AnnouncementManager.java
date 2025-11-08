package rearth.excavations.announcement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.ExcavationClient;

import java.util.HashMap;
import java.util.Map;

public class AnnouncementManager extends JsonDataLoader {
    
    private static final Gson gson = new GsonBuilder().create();
    
    private final Map<Identifier, Announcement> announcements = new HashMap<>();
    
    
    public AnnouncementManager() {
        super(gson, "announcements");
    }
    
    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        announcements.clear();
        
        
        prepared.forEach((location, element) -> {
            var announcement = gson.fromJson(element, Announcement.class);
            announcements.put(location, announcement);
        });
        
        System.out.println("Loaded announcements: " + announcements.size());
    }
    
    public Map<Identifier, Announcement> getAnnouncements() {
        return announcements;
    }
    
    public static @Nullable Announcement getAnnouncement(Identifier id) {
        return ExcavationClient.ANNOUNCEMENT_MANAGER.getAnnouncements().get(id);
    }
}
