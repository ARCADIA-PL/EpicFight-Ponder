package org.arc.efp.mixin.epicfight;

import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.LevelUtil;


@Mixin(value = LevelUtil.class, remap = false)
public class MixinLevelUtil {

    @Inject(
            method = "circleSlamFracture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;DZZZ)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void disableFractureInPonder(
            LivingEntity caster, Level level, Vec3 center, double radius,
            boolean noSound, boolean noParticle, boolean hurtEntities,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (level instanceof PonderLevel) {
            cir.setReturnValue(false);
        }
    }
}