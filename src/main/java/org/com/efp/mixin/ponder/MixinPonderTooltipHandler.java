package org.com.efp.mixin.ponder;

import net.createmod.ponder.api.registration.SceneRegistryAccess;
import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.com.efp.client.ponder.EFPPonderPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PonderTooltipHandler.class, remap = false)
public class MixinPonderTooltipHandler {

    @Unique
    private static ItemStack EFP_LATEST_STACK = ItemStack.EMPTY;

    @Inject(method = "addToTooltip", at = @At("HEAD"))
    private static void captureTooltipStack(List<Component> toolTip, ItemStack stack, CallbackInfo ci) {
        if (stack != null) {
            EFP_LATEST_STACK = stack;
        }
    }

    @Inject(method = "updateHovered", at = @At("HEAD"))
    private static void captureHoveredStack(ItemStack stack, CallbackInfo ci) {
        if (stack != null) {
            EFP_LATEST_STACK = stack;
        }
    }

    @Redirect(
            method = {"addToTooltip", "updateHovered"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/ponder/api/registration/SceneRegistryAccess;doScenesExistForId(Lnet/minecraft/resources/ResourceLocation;)Z"
            )
    )
    private static boolean redirectDoScenesExist(SceneRegistryAccess instance, ResourceLocation originalId) {
        if (!EFP_LATEST_STACK.isEmpty()) {
            ResourceLocation customId = EFPPonderPlugin.getCustomPonderId(EFP_LATEST_STACK);
            if (customId != null) {
                return instance.doScenesExistForId(customId);
            }
        }
        return instance.doScenesExistForId(originalId);
    }
}