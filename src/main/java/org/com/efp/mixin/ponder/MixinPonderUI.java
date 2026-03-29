package org.com.efp.mixin.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.com.efp.client.ponder.EFPPonderPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("all")
@Mixin(value = PonderUI.class, remap = false)
public class MixinPonderUI {

    private static final ThreadLocal<ItemStack> EFP_CONTEXT_STACK = new ThreadLocal<>();
    @Shadow
    ItemStack stack;

    @Inject(method = "of(Lnet/minecraft/world/item/ItemStack;)Lnet/createmod/ponder/foundation/ui/PonderUI;", at = @At("HEAD"), cancellable = true)
    private static void routeItemToDataDrivenConcept(ItemStack item, CallbackInfoReturnable<PonderUI> cir) {
        if (item == null || item.isEmpty()) return;

        ResourceLocation targetRl = EFPPonderPlugin.getCustomPonderId(item);

        if (targetRl != null) {
            List<PonderScene> compiledScenes = PonderIndex.getSceneAccess().compile(targetRl);

            if (!compiledScenes.isEmpty()) {
                EFP_CONTEXT_STACK.set(item);
                try {
                    cir.setReturnValue(PonderUI.of(targetRl));
                } finally {
                    EFP_CONTEXT_STACK.remove();
                }
            }
        }
    }

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("TAIL"))
    private void assignCorrectStackToUI(List<PonderScene> scenes, CallbackInfo ci) {
        ItemStack contextItem = EFP_CONTEXT_STACK.get();
        if (contextItem != null && !contextItem.isEmpty()) {
            this.stack = contextItem.copy();
        }
    }
}