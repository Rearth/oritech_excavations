package rearth.excavations.entities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rearth.excavations.init.TagContent;

import java.util.*;

public class DigController {
    
    private static Map<BlockPos, DigController> activeControllers = new HashMap<>();
    private static final int MAX_RANGE = 64;
    
    private final BlockPos center;
    private Iterator<BlockPos> iterator;
    private final World world;
    
    public DigController(BlockPos origin, World world) {
        this.center = origin;
        
        iterator = BlockPos.iterateOutwards(origin, MAX_RANGE, MAX_RANGE + 32, MAX_RANGE).iterator();
        this.world = world;
    }
    
    public BlockPos getNext() {
        
        while (iterator.hasNext()) {
            var candidate = iterator.next();
            var candidateState = world.getBlockState(candidate);
            if (candidateState.isAir() || !candidateState.isIn(TagContent.ALLAY_MINEABLE)) continue;
            return candidate;
        }
        
        // nothing left, remove instance
        activeControllers.remove(center);
        return null;
    }
    
    public static BlockPos getNextPosition(BlockPos from, World world) {
        var controller = getControllerForPos(from, world);
        
        return controller.getNext();
    }
    
    public static List<BlockPos> getNextPositions(BlockPos from, World world, int count) {
        var controller = getControllerForPos(from, world);
        
        var result = new ArrayList<BlockPos>();
        for (int i = 0; i < count; i++) {
            var candidate = controller.getNext();
            if (candidate != null) result.add(candidate);
        }
        
        return result;
    }
    
    private static DigController getControllerForPos(BlockPos from, World world) {
        
        var nearest = findNearestController(from, 20 * 20);
        if (nearest == null) {
            var created = createForPos(from, world);
            activeControllers.put(from, created);
            return created;
        } else {
            return nearest;
        }
        
    }
    
    private static DigController findNearestController(BlockPos from, float maxDist) {
        
        var minDist = Double.MAX_VALUE;
        DigController result = null;
        
        for (var pair : activeControllers.entrySet()) {
            var dist = pair.getKey().getSquaredDistance(from);
            if (dist < minDist && dist < maxDist) {
                minDist = dist;
                result = pair.getValue();
            }
        }
        
        return result;
        
    }
    
    private static DigController createForPos(BlockPos pos, World world) {
        System.out.println("Creating new controller");
        return new DigController(pos, world);
    }
    
    
}
