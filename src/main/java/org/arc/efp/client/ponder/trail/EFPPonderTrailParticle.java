package org.arc.efp.client.ponder.trail;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.physics.bezier.CubicBezierCurve;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public class EFPPonderTrailParticle extends AnimationTrailParticle {

    private final LivingEntityPatch<?> entityPatch;
    public TrailInfo trailInfo;

    @SuppressWarnings("deprecation")
    public EFPPonderTrailParticle(LivingEntityPatch<?> entityPatch, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo) {
        super(entityPatch.getArmature(), entityPatch, joint, animation, trailInfo);
        this.entityPatch = entityPatch;
        this.trailInfo = trailInfo;
    }

    @Override
    public void tick() {
        AnimationPlayer animPlayer = this.entityPatch.getAnimator().getPlayerFor(null);

        this.trailEdges.removeIf(v -> !v.isAlive());

        if (this.shouldRemove) {
            this.lifetime--;
        } else {
            if (animPlayer == null || this.animation != animPlayer.getRealAnimation()) {
                this.shouldRemove = true;
                this.lifetime = this.trailInfo.trailLifetime();
            } else if (animPlayer.getElapsedTime() > this.trailInfo.endTime()) {
                this.shouldRemove = true;
                this.lifetime = this.trailInfo.trailLifetime();
            }
        }

        if (animPlayer == null) return;

        if (this.trailInfo.fadeTime() > 0.0F && this.trailInfo.endTime() < animPlayer.getElapsedTime()) {
            return;
        }

        boolean isTrailInvisible = animPlayer.getAnimation().get().isLinkAnimation() || animPlayer.getElapsedTime() <= this.trailInfo.startTime();
        boolean isFirstTrail = this.trailEdges.isEmpty();
        boolean needCorrection = (!isTrailInvisible && isFirstTrail);

        if (needCorrection) {
            float startCorrection = Math.max((this.trailInfo.startTime() - animPlayer.getPrevElapsedTime()) / (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
            this.startEdgeCorrection = this.trailInfo.interpolateCount() * 2 * startCorrection;
        }

        TrailInfo trailInfo = this.trailInfo;
        Pose prevPose = this.entityPatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entityPatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entityPatch.getAnimator().getPose(1.0F);

        OpenMatrix4f prevJointTf = this.entityPatch.getArmature().getBoundTransformFor(prevPose, this.joint);
        OpenMatrix4f middleJointTf = this.entityPatch.getArmature().getBoundTransformFor(middlePose, this.joint);
        OpenMatrix4f currentJointTf = this.entityPatch.getArmature().getBoundTransformFor(currentPose, this.joint);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start());
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end());
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start());
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end());
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start());
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end());

        List<Vec3> finalStartPositions;
        List<Vec3> finalEndPositions;
        boolean visibleTrail;

        if (isTrailInvisible) {
            finalStartPositions = Lists.newArrayList();
            finalEndPositions = Lists.newArrayList();
            finalStartPositions.add(prevStartPos);
            finalStartPositions.add(middleStartPos);
            finalEndPositions.add(prevEndPos);
            finalEndPositions.add(middleEndPos);

            this.invisibleTrailEdges.clear();
            visibleTrail = false;
        } else {
            List<Vec3> startPosList = Lists.newArrayList();
            List<Vec3> endPosList = Lists.newArrayList();
            TrailEdge edge1;
            TrailEdge edge2;

            if (isFirstTrail) {
                int lastIdx = this.invisibleTrailEdges.size() - 1;
                if (lastIdx < 0) {
                    this.invisibleTrailEdges.add(new TrailEdge(prevStartPos, prevEndPos, -1));
                    lastIdx = 0;
                }
                edge1 = this.invisibleTrailEdges.get(lastIdx);
                edge2 = new TrailEdge(prevStartPos, prevEndPos, -1);
            } else {
                int idx1 = this.trailEdges.size() - (this.trailInfo.interpolateCount() / 2 + 1);
                int idx2 = this.trailEdges.size() - 1;
                if (idx1 < 0) idx1 = 0;
                if (idx2 < 0) idx2 = 0;
                edge1 = this.trailEdges.get(idx1);
                edge2 = this.trailEdges.get(idx2);
                edge2.lifetime++;
            }

            startPosList.add(edge1.start);
            endPosList.add(edge1.end);
            startPosList.add(edge2.start);
            endPosList.add(edge2.end);
            startPosList.add(middleStartPos);
            endPosList.add(middleEndPos);
            startPosList.add(currentStartPos);
            endPosList.add(currentEndPos);

            finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(startPosList, 1, 3, this.trailInfo.interpolateCount());
            finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(endPosList, 1, 3, this.trailInfo.interpolateCount());

            if (!isFirstTrail) {
                finalStartPositions.remove(0);
                finalEndPositions.remove(0);
            }

            visibleTrail = true;
        }

        this.makeTrailEdges(finalStartPositions, finalEndPositions, visibleTrail ? this.trailEdges : this.invisibleTrailEdges);
    }

    public void renderForPonder(VertexConsumer vertexConsumer, PoseStack poseStack, float partialTick) {
        if (this.trailEdges.isEmpty()) return;

        poseStack.pushPose();
        float yaw = Mth.lerp(partialTick, this.entityPatch.getOriginal().yRotO, this.entityPatch.getOriginal().getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        int edges = this.trailEdges.size() - 1;
        boolean startFade = this.trailEdges.get(0).lifetime == 1;
        boolean endFade = this.trailEdges.get(edges).lifetime == this.trailInfo.trailLifetime();
        float startEdge = (startFade ? this.trailInfo.interpolateCount() * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
        float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount() * 2) * (1.0F - partialTick), edges - 1) : edges - 1;
        float interval = 1.0F / (endEdge - startEdge);
        float fading = 1.0F;

        if (this.shouldRemove) {
            if (TrailInfo.isValidTime(this.trailInfo.fadeTime())) {
                fading = ((float) this.lifetime / (float) this.trailInfo.trailLifetime());
            } else {
                fading = Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime(), 0.0F, 1.0F);
            }
        }

        float partialStartEdge = interval * (startEdge % 1.0F);
        float from = -partialStartEdge;
        float to = -partialStartEdge + interval;

        int r = (int) (this.rCol * 255.0F);
        int g = (int) (this.gCol * 255.0F);
        int b = (int) (this.bCol * 255.0F);
        int overlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
        int light = 15728880;

        for (int i = (int) (startEdge); i < (int) endEdge + 1; i++) {
            if (i < 0 || i + 1 >= this.trailEdges.size()) continue;

            TrailEdge e1 = this.trailEdges.get(i);
            TrailEdge e2 = this.trailEdges.get(i + 1);

            float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
            float alphaTo = Mth.clamp(to, 0.0F, 1.0F);

            int a1 = (int) (this.alpha * alphaFrom * fading * 255.0F);
            int a2 = (int) (this.alpha * alphaTo * fading * 255.0F);

            vertexConsumer.vertex(matrix4f, (float) e1.start.x, (float) e1.start.y, (float) e1.start.z)
                    .color(r, g, b, a1).uv(from, 1.0F).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();

            vertexConsumer.vertex(matrix4f, (float) e1.end.x, (float) e1.end.y, (float) e1.end.z)
                    .color(r, g, b, a1).uv(from, 0.0F).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();

            vertexConsumer.vertex(matrix4f, (float) e2.end.x, (float) e2.end.y, (float) e2.end.z)
                    .color(r, g, b, a2).uv(to, 0.0F).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();

            vertexConsumer.vertex(matrix4f, (float) e2.start.x, (float) e2.start.y, (float) e2.start.z)
                    .color(r, g, b, a2).uv(to, 1.0F).overlayCoords(overlay).uv2(light).normal(matrix3f, 0, 1, 0).endVertex();

            from += interval;
            to += interval;
        }

        poseStack.popPose();
    }

    public boolean isAlive() {
        return !this.shouldRemove || this.lifetime > 0;
    }
}