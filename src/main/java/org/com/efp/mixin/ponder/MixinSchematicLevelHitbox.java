package org.com.efp.mixin.ponder;

import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(value = SchematicLevel.class, remap = false)
public abstract class MixinSchematicLevelHitbox {

    @Shadow public abstract List<Entity> getEntityList();

    @Inject(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void efp$allowPonderHitbox(Entity entity, AABB boundingBox, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        List<Entity> hits = new ArrayList<>();

        for (Entity e : this.getEntityList()) {
            if (e != entity && e.getBoundingBox().intersects(boundingBox)) {

                if (predicate == null || predicate.test(e)) {
                    hits.add(e);
                }
            }
        }

        cir.setReturnValue(hits);
    }
}