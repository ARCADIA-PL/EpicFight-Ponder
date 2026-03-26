package org.com.efp.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class DummyPlayerEntity extends PathfinderMob {

    private int dashTicks = 0;
    private Vec3 dashVelocity = Vec3.ZERO;

    public DummyPlayerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .build();
    }

    public void sprintForward(double blocks, int durationTicks) {
        if (durationTicks <= 0) return;

        float f = this.getYRot() * 0.017453292F;
        double totalX = -Mth.sin(f) * blocks;
        double totalZ = Mth.cos(f) * blocks;

        this.dashVelocity = new Vec3(totalX / durationTicks, 0, totalZ / durationTicks);
        this.dashTicks = durationTicks;

        DummyPlayerEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(this, DummyPlayerEntityPatch.class);
        if (patch != null) {
            patch.getClientAnimator().playAnimation(patch.getClientAnimator().getLivingAnimation(LivingMotions.RUN, null), 0);
        }
    }

    public void jumpFromGround(float jumpForce) {
        Vec3 currentMovement = this.getDeltaMovement();

        double newX = currentMovement.x;
        double newZ = currentMovement.z;
        if (this.isSprinting()) {
            float f = this.getYRot() * 0.017453292F;
            newX += -Mth.sin(f) * 0.2F;
            newZ += Mth.cos(f) * 0.2F;
        }

        DummyPlayerEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(this, DummyPlayerEntityPatch.class);
        if (patch != null) {
            patch.getClientAnimator().playAnimation(patch.getClientAnimator().getLivingAnimation(LivingMotions.JUMP, null), 0);
        }

        this.setDeltaMovement(newX, jumpForce, newZ);

        this.setJumping(true);
        this.setOnGround(false);
        this.verticalCollision = false;
        this.hasImpulse = true;

        ForgeHooks.onLivingJump(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.dashTicks > 0) {
            this.move(MoverType.SELF, this.dashVelocity);
            this.setSprinting(true);
            this.dashTicks--;
        } else if (this.isSprinting()) {
            this.setSprinting(false);
        }
    }
}