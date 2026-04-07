package org.arc.epic_ponder.mixin.epicfight;

import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.nbt.CompoundTag;
import org.arc.epic_ponder.api.ponder.EpicFightSceneBuilder;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class, remap = false)
public class AnimationPlayerMixin {

    @ModifyVariable(
            method = "tick",
            at = @At(value = "FIELD", target = "Lyesman/epicfight/api/animation/AnimationPlayer;elapsedTime:F", opcode = Opcodes.GETFIELD, ordinal = 2),
            ordinal = 0
    )
    private float efp$applyAnimationSpeed(float playbackSpeed, LivingEntityPatch<?> entityPatch) {
        if (entityPatch.isLogicalClient()) {
            if (entityPatch.getOriginal().level() instanceof PonderLevel) {
                CompoundTag data = entityPatch.getOriginal().getPersistentData();
                if (data.contains(EpicFightSceneBuilder.PLAY_SPEED)) {
                    return data.getFloat(EpicFightSceneBuilder.PLAY_SPEED);
                }
            }
        }
        return playbackSpeed;
    }
}