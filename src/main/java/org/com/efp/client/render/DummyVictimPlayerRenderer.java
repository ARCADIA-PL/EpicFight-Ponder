package org.com.efp.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.com.efp.client.ponder.trail.EFPPonderTrailParticle;
import org.com.efp.entity.DummyEntityPatch;
import org.com.efp.entity.DummyVictimPlayerEntity;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class DummyVictimPlayerRenderer extends HumanoidMobRenderer<DummyVictimPlayerEntity, PlayerModel<DummyVictimPlayerEntity>> {

    public DummyVictimPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DummyVictimPlayerEntity pEntity) {
        if (Minecraft.getInstance().player != null) {
            return Minecraft.getInstance().player.getSkinTextureLocation();
        }
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/steve.png");
    }

    @Override
    public void render(@NotNull DummyVictimPlayerEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        if (entity.level() instanceof PonderLevel) {
            entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(cap -> {
                if (cap instanceof DummyEntityPatch<?> patch && !patch.activeTrails.isEmpty()) {

                    for (EFPPonderTrailParticle trail : patch.activeTrails) {
                        if (trail.trailInfo == null) {
                            continue;
                        }
                        if (trail.trailInfo.texturePath() == null) {
                            continue;
                        }

                        RenderType renderType = RenderType.entityTranslucent(trail.trailInfo.texturePath());
                        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

                        trail.renderForPonder(vertexConsumer, poseStack, partialTicks);
                    }
                }
            });
        }
    }
}