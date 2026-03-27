package org.com.efp.entity;

import com.mojang.datafixers.util.Pair;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.com.efp.client.ponder.trail.EFPPonderTrailParticle;
import org.joml.Vector3d;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;

import java.util.*;
import java.util.function.Supplier;

public class DummyEntityPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {

    private final Map<AttackAnimation.Phase, Set<Entity>> phaseHitMemory = new HashMap<>();
    private final Set<AttackAnimation.Phase> playedSwingPhases = new HashSet<>();

    public final List<EFPPonderTrailParticle> activeTrails = new ArrayList<>();
    private AssetAccessor<? extends StaticAnimation> lastPlayedAnimation = null;
    private DynamicAnimation lastCheckedAnim = null;
    private Style forcedStyle = null;

    public DummyEntityPatch() {
        super(Factions.NEUTRAL);
    }

    @Override
    public HumanoidArmature getArmature() {
        return Armatures.BIPED.get();
    }

    @Override
    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    public void setForcedStyle(Style style) { this.forcedStyle = style; }
    public Style getForcedStyle() { return this.forcedStyle; }

    @Override
    public void updateMotion(boolean considerInaction) { }

    public void updateLivingMotionsForPonder() {
        CapabilityItem mainHandCap = this.getHoldingItemCapability(InteractionHand.MAIN_HAND);
        CapabilityItem offHandCap = this.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND);

        Map<LivingMotion, AssetAccessor<? extends StaticAnimation>> newLivingAnimations = new HashMap<>(mainHandCap.getLivingMotionModifier(this, InteractionHand.MAIN_HAND));
        newLivingAnimations.putAll(offHandCap.getLivingMotionModifier(this, InteractionHand.OFF_HAND));

        if (this.weaponLivingMotions != null && this.weaponLivingMotions.containsKey(mainHandCap.getWeaponCategory())) {
            Map<Style, Set<Pair<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>>>> byStyle = this.weaponLivingMotions.get(mainHandCap.getWeaponCategory());
            Style style = mainHandCap.getStyle(this);

            if (byStyle.containsKey(style) || byStyle.containsKey(CapabilityItem.Styles.COMMON)) {
                Set<Pair<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>>> animModifierSet = byStyle.getOrDefault(style, byStyle.get(CapabilityItem.Styles.COMMON));

                for (Pair<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>> pair : animModifierSet) {
                    newLivingAnimations.put(pair.getFirst(), pair.getSecond());
                }
            }
        }
        this.getAnimator().resetLivingAnimations();
        newLivingAnimations.forEach(this.getAnimator()::addLivingAnimation);
    }

    @Override
    public void modifyLivingMotionByCurrentItem(boolean onStartTracking) {
        this.updateLivingMotionsForPonder();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (this.original.level().isClientSide() && this.original.level() instanceof PonderLevel) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 1.0F));
        } else {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        LivingEntity original = this.getOriginal();
        if (original.level() instanceof PonderLevel) {
            original.yBodyRot = original.getYRot();
            original.yBodyRotO = original.yRotO;
            original.yHeadRot = original.getYRot();
            original.yHeadRotO = original.yRotO;
        }

        super.tick(event);

        if (this.original.level() instanceof PonderLevel) {
            this.activeTrails.forEach(EFPPonderTrailParticle::tick);
            this.activeTrails.removeIf(trail -> !trail.isAlive());

            AnimationPlayer animPlayer = this.getClientAnimator() != null ? this.getClientAnimator().getPlayerFor(null) : null;
            if (animPlayer != null) {
                AssetAccessor<? extends StaticAnimation> currentAnim = animPlayer.getAnimation().get().getRealAnimation();
                if (currentAnim != null && currentAnim != lastPlayedAnimation) {
                    lastPlayedAnimation = currentAnim;
                    currentAnim.get().getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent(trailInfos -> {
                        for (TrailInfo info : trailInfos) {
                            TrailInfo processedInfo = info;
                            if (processedInfo.hand() != null) {
                                ItemStack stack = this.getOriginal().getItemInHand(processedInfo.hand());
                                RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
                                if (renderItemBase != null && renderItemBase.trailInfo() != null) {
                                    processedInfo = renderItemBase.trailInfo().overwrite(processedInfo);
                                }
                            }
                            processedInfo = this.getEntityDecorations().getModifiedTrailInfo(
                                    processedInfo,
                                    processedInfo.hand() == null ? CapabilityItem.EMPTY : this.getAdvancedHoldingItemCapability(processedInfo.hand())
                            );
                            if (processedInfo.start() == null || processedInfo.end() == null) continue;
                            if (processedInfo.rCol() < 0.0F || processedInfo.gCol() < 0.0F || processedInfo.bCol() < 0.0F) {
                                processedInfo = processedInfo.unpackAsBuilder().r(1.0F).g(1.0F).b(1.0F).create();
                            }
                            Joint joint = this.getArmature().searchJointByName(processedInfo.joint());
                            if (joint != null) {
                                EFPPonderTrailParticle trail = new EFPPonderTrailParticle(this, joint, currentAnim, processedInfo);
                                this.activeTrails.add(trail);
                            }
                        }
                    });
                }
            } else {
                lastPlayedAnimation = null;
            }

            this.simulatePonderHitbox();
        }
    }

    private void simulatePonderHitbox() {
        AnimationPlayer player = this.getClientAnimator().baseLayer.animationPlayer;
        if (player == null) {
            this.clearPonderMemory();
            return;
        }

        DynamicAnimation playingAnim = player.getAnimation().get();
        if (playingAnim.isLinkAnimation()) return;

        if (!(playingAnim instanceof AttackAnimation attackAnim)) {
            this.clearPonderMemory();
            this.lastCheckedAnim = playingAnim;
            return;
        }

        float prevTime = player.getPrevElapsedTime();
        float time = player.getElapsedTime();

        if (time < prevTime || playingAnim != this.lastCheckedAnim) {
            this.clearPonderMemory();
        }
        this.lastCheckedAnim = playingAnim;

        EntityState prevState = playingAnim.getState(this, prevTime);
        EntityState state = playingAnim.getState(this, time);

        if (state.attacking() || (prevState.getLevel() <= 2 && state.getLevel() > 2)) {
            List<AttackAnimation.Phase> activePhases = new ArrayList<>();

            if (attackAnim.getClass().getSimpleName().equals("MultiPhaseAttackAnimation") ||
                    attackAnim.getClass().getName().contains("MultiPhase")) {
                for (AttackAnimation.Phase phase : attackAnim.phases) {
                    if (time >= phase.antic && time <= phase.contact) activePhases.add(phase);
                }
            } else {
                AttackAnimation.Phase currentPhase = attackAnim.getPhaseByTime(time);
                if (currentPhase != null) activePhases.add(currentPhase);
            }

            for (AttackAnimation.Phase phase : activePhases) {
                if (!this.playedSwingPhases.contains(phase)) {
                    this.playedSwingPhases.add(phase);
                    SoundEvent swingSound = phase.getProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND).orElse(this.getSwingSound(phase.hand));
                    if (swingSound != null) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(swingSound, 1, 1.0F));
                }

                float prevPoseTime = prevState.attacking() ? prevTime : phase.preDelay;
                float poseTime = state.attacking() ? time : phase.contact;

                if (poseTime <= prevPoseTime) poseTime = prevPoseTime + 0.01f;

                float playSpeed = attackAnim.getPlaySpeed(this, attackAnim);
                List<Entity> hits = new ArrayList<>();

                for (AttackAnimation.JointColliderPair colliderInfo : phase.colliders) {
                    Collider collider = colliderInfo.getSecond() != null ? colliderInfo.getSecond() : this.getColliderMatching(phase.hand);
                    if (collider != null) {
                        hits.addAll(collider.updateAndSelectCollideEntity(this, attackAnim, prevPoseTime, poseTime, colliderInfo.getFirst(), playSpeed));
                    }
                }

                Set<Entity> currentPhaseHitMemory = this.phaseHitMemory.computeIfAbsent(phase, k -> new HashSet<>());
                for (Entity hit : hits) {
                    if (!(hit instanceof LivingEntity target)) continue;
                    if (currentPhaseHitMemory.contains(target)) continue;

                    currentPhaseHitMemory.add(target);

                    SoundEvent hitSound = phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND).orElse(this.getWeaponHitSound(phase.hand));
                    if (hitSound != null) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(hitSound, 1, 1.0F));

                    HitParticleType particle = phase.getProperty(AnimationProperty.AttackPhaseProperty.PARTICLE)
                            .map(Supplier::get).orElse(this.getWeaponHitParticle(phase.hand));

                    if (particle != null) {
                        Vector3d pos = particle.positionProvider.apply(target, this.getOriginal());
                        Vector3d args = particle.argumentProvider.apply(target, this.getOriginal());
                        this.getOriginal().level().addParticle(particle, pos.x, pos.y, pos.z, args.x, args.y, args.z);
                    }
                }
            }
        }
    }

    private void clearPonderMemory() {
        this.phaseHitMemory.clear();
        this.playedSwingPhases.clear();
    }
}