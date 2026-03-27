package org.com.efp.mixin.compat.epicfightx;

import com.asanginxst.epicfightx.client.renderer.RenderUchigatanaX;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.com.efp.entity.DummyEntityPatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = RenderUchigatanaX.class, remap = false)
public abstract class MixinRenderUchigatanaX {

    @Final @Shadow private ItemStack sheathStack;
    @Final @Shadow private ItemStack alterSheathStack;

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void renderForPonderDummy(ItemStack stack, LivingEntityPatch<?> entityPatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks, CallbackInfo ci) {

        if (entityPatch instanceof DummyEntityPatch<?> dummyPatch) {
            RenderUchigatanaX self = (RenderUchigatanaX) (Object) this;

            boolean useAlterModel = false;

            if (stack.getTag() != null && stack.hasTag() && stack.getTag().contains("sheath")) {
                useAlterModel = stack.getTag().getInt("sheath") == 1;
            }

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            if (!useAlterModel) {
                OpenMatrix4f modelMatrix = self.getCorrectionMatrix(entityPatch, InteractionHand.MAIN_HAND, poses);
                poseStack.pushPose();
                MathUtils.mulStack(poseStack, modelMatrix);
                itemRenderer.renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
                poseStack.popPose();
            }

            OpenMatrix4f sheathMatrix = self.getCorrectionMatrix(entityPatch, InteractionHand.OFF_HAND, poses);
            poseStack.pushPose();
            MathUtils.mulStack(poseStack, sheathMatrix);

            ItemStack sheathToRender = useAlterModel ? this.alterSheathStack : this.sheathStack;
            itemRenderer.renderStatic(sheathToRender, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);

            poseStack.popPose();
            ci.cancel();
        }
    }
}