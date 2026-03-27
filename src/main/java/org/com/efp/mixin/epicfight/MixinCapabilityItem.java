package org.com.efp.mixin.epicfight;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import org.com.efp.entity.DummyEntityPatch;
import yesman.epicfight.world.capabilities.item.Style;

@Mixin(value = {CapabilityItem.class, WeaponCapability.class}, remap = false)
public class MixinCapabilityItem {

    @Inject(method = "getStyle", at = @At("HEAD"), cancellable = true)
    private void efp$forceDummyStyleInPonder(LivingEntityPatch<?> livingEntityPatch, CallbackInfoReturnable<Style> cir) {
        if (livingEntityPatch instanceof DummyEntityPatch<?> dummyEntityPatch) {
            Style forced = dummyEntityPatch.getForcedStyle();
            if (forced != null) {
                cir.setReturnValue(forced);
            }
        }
    }
}