package org.com.efp.entity;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.damagesource.StunType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DummyPlayerEntityPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {

    private Style forcedStyle = null;

    public DummyPlayerEntityPatch() {
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
        animator.addLivingAnimation(LivingMotions.JUMP, AnimationsX.BIPED_JUMP);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.CHASE, AnimationsX.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.RUN, AnimationsX.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    public void setForcedStyle(Style style) {
        this.forcedStyle = style;
    }

    public Style getForcedStyle() {
        return this.forcedStyle;
    }

    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return switch (stunType) {
            case LONG -> Animations.BIPED_HIT_LONG;
            case SHORT, HOLD -> Animations.BIPED_HIT_SHORT;
            case KNOCKDOWN -> Animations.BIPED_KNOCKDOWN;
            case NEUTRALIZE -> Animations.BIPED_COMMON_NEUTRALIZED;
            case FALL -> Animations.BIPED_LANDING;
            default -> null;
        };
    }

    @Override
    public void updateMotion(boolean considerInaction) {
    }

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
}