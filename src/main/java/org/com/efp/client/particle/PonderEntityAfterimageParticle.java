package org.com.efp.client.particle;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.joml.Quaternionf;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.particle.EntityAfterimageParticle;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;

public class PonderEntityAfterimageParticle extends EntityAfterimageParticle {

    private static final Cache<Integer, EntitySnapshot<?>> SNAPSHOT_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();
    private static int NEXT_ID = 0;

    public static int cacheSnapshot(EntitySnapshot<?> snapshot) {
        int id = NEXT_ID++;
        SNAPSHOT_CACHE.put(id, snapshot);
        return id;
    }

    public PonderEntityAfterimageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, EntitySnapshot<?> entitySnapshot, Consumer<EntityAfterimageParticle> ticktask) {
        super(level, x, y, z, xd, yd, zd, entitySnapshot, ticktask);
    }

    @Override
    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTick) {
        poseStack.pushPose();

        Vec3 cameraPosition = camera.getPosition();
        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPosition.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPosition.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPosition.z());
        poseStack.translate(x, y, z);

        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F));
        poseStack.mulPose(rotation);
        poseStack.mulPoseMatrix(OpenMatrix4f.exportToMojangMatrix(this.entitySnapshot.getModelMatrix()));

        float scale = Mth.lerp(partialTick, this.scaleO, this.scale);
        poseStack.translate(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
    }

    @Override
    protected void revert(PoseStack poseStack) {
        poseStack.popPose();
    }
    public static class PonderWhite extends PonderEntityAfterimageParticle {
        public PonderWhite(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, EntitySnapshot<?> entitySnapshot) {
            super(level, x, y, z, xd, yd, zd, entitySnapshot, p -> {});
            this.lifetime = 45;
        }

        @Override
        public void tick() {
            super.tick();
            this.alpha = (float) (this.lifetime - this.age) / (float) this.lifetime;
        }

        @Override
        public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
            float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
            int lightColor = this.getLightColor(partialTicks);
            PoseStack poseStack = new PoseStack();
            this.setupPoseStack(poseStack, camera, partialTicks);
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

            this.entitySnapshot.renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, Mesh.DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
            this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), Mesh.DrawingFunction.POSITION_TEX, lightColor, 1.0F);
            buffers.endLastBatch();

            this.entitySnapshot.render(poseStack, buffers, EpicFightRenderTypes.entityAfterimageWhite(), Mesh.DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP, lightColor, this.rCol, this.gCol, this.bCol, alpha);
            this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageWhite(), Mesh.DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP, lightColor, alpha);
            buffers.endLastBatch();

            this.revert(poseStack);
        }
    }

    public static class PonderWhiteProvider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

            int snapshotId = (int) Math.round(xSpeed);

            EntitySnapshot<?> snapshot = SNAPSHOT_CACHE.getIfPresent(snapshotId);
            if (snapshot != null) {
                SNAPSHOT_CACHE.invalidate(snapshotId);
                return new PonderWhite(level, x, y, z, xSpeed, ySpeed, zSpeed, snapshot);
            }

            return null;
        }
    }
}