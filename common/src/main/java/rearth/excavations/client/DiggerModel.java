package rearth.excavations.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;
import org.joml.Vector2f;
import rearth.excavations.blocks.digger.DiggerBlockEntity;
import rearth.oritech.client.renderers.MachineModel;
import software.bernie.geckolib.animation.AnimationState;

import java.util.HashMap;
import java.util.Map;

public class DiggerModel extends MachineModel<DiggerBlockEntity> {
    
    public DiggerModel(String subpath) {
        super(subpath);
    }
    
    private final Map<Long, Vec3d> headPosCaches = new HashMap<>();
    
    @Override
    public void setCustomAnimations(DiggerBlockEntity animatable, long instanceId, AnimationState<DiggerBlockEntity> animationState) {
        
        if (animatable.currentTarget == null || animatable.currentTarget.equals(BlockPos.ORIGIN)
              || animatable.currentTarget.equals(animatable.getPos().up())
              || animatable.currentTarget.equals(animatable.getPos())
        ) return;
        
        var targetPos = Vec3d.of(animatable.currentTarget.up());
        var lastPos = headPosCaches.getOrDefault(instanceId, targetPos);
        var currentPos = lerp(lastPos, targetPos, 0.008f);
        headPosCaches.put(instanceId, currentPos);
        
        var targetOffset = currentPos.subtract(Vec3d.of(animatable.getPos())).multiply(-1);
        var horizontalOffset = new Vec3d(targetOffset.x, 0, targetOffset.z);
        
        var facingOffset = 0;
        var facing = animatable.getFacingForMultiblock();
        switch (facing) {
            case EAST -> facingOffset = 90;
            case SOUTH -> facingOffset = 180;
            case WEST -> facingOffset = -90;
        }
        
        var angles = getAzimuthElevation(targetOffset);
        
        var azimuthBone = getAnimationProcessor().getBone("rotato");
        azimuthBone.setRotY((float) (angles.x + Math.toRadians(facingOffset)));
        
        // default head length is 8.5
        var headOffsetZ = horizontalOffset.length() - 9.5f;
        var headOffsetY = targetOffset.y + 1;
        
        var headBone = getAnimationProcessor().getBone("head");
        headBone.setPosZ((float) -headOffsetZ * 16f);
        headBone.setPosY((float) -headOffsetY * 16f);
        
        // first number is distance from origin, second number is length of head
        var headOffset = new Vector2d(horizontalOffset.length() - 1.8f - 4f, -headOffsetY);
        var headDirection = new Vector2d(headOffset).normalize();
        var beamScale = headOffset.length() / 4f * 1.05f;
        
        var beamAngle = Math.atan2(headDirection.y, headDirection.x);
        var beamBone = getAnimationProcessor().getBone("beam");
        beamBone.setRotX((float) beamAngle);
        beamBone.setScaleZ((float) beamScale);
        
        
        
        // first number is distance from origin, second number is length of head
        var headPos = new Vector2d(horizontalOffset.length() - 3.8, -headOffsetY);
        var pivotPos = new Vector2d(31/16f, 60/16f);
        headDirection = new Vector2d(headOffset).normalize();
        beamScale = pivotPos.sub(headPos).length() / 4.95f ;
        beamAngle = Math.atan2(headDirection.y, headDirection.x);
        
        if (headOffsetY < -1)
            beamScale *= 1.2f;
        
        var ropeBone = getAnimationProcessor().getBone("rope");
        ropeBone.setRotX((float) beamAngle);
        ropeBone.setScaleZ((float) beamScale * 1.25f);
        
    }
    
    // returns a vector containing azimuth (x) and elevation (y)
    public static Vector2f getAzimuthElevation(Vec3d vec) {
        
        var normalized = vec.normalize();
        
        var yaw = Math.atan2(normalized.x, normalized.z);
        var horizontalDist = Math.sqrt(normalized.x * normalized.x + normalized.z * normalized.z);
        var pitch = Math.atan2(normalized.y, horizontalDist);
        
        return new Vector2f((float) yaw, (float) pitch);
        
    }
    
    public static Vec3d lerp(Vec3d from, Vec3d to, float t) {
        return new Vec3d(
          MathHelper.lerp(t, from.x, to.x),
          MathHelper.lerp(t, from.y, to.y),
          MathHelper.lerp(t, from.z, to.z)
        );
    }
}
