package org.com.efp.gameasset;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.com.efp.EpicFightPonder;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;

@Mod.EventBusSubscriber(modid = EpicFightPonder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFPAnimations {

    public static final Collider BIPED_BODY_COLLIDER = new MultiOBBCollider(new OBBCollider(1, 0.7, 1.2, 0.0, 1.0, -0.6));

    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_FORWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_BACKWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_LEFT;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_RIGHT;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_ROLL_FORWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_ROLL_BACKWARD;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BLADE_RUSH_COMBO1_PONDER;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BLADE_RUSH_COMBO2_PONDER;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BLADE_RUSH_COMBO3_PONDER;
    public static AnimationManager.AnimationAccessor<LongHitAnimation> BLADE_RUSH_HIT_PONDER;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BLADE_RUSH_EXECUTE_BIPED_PONDER;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BLADE_RUSH_TRY_PONDER;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BLADE_RUSH_FAILED_PONDER;

    public static void buildAnimations(AnimationManager.AnimationBuilder builder) {
        BIPED_ROLL_FORWARD = builder.nextAccessor("biped/skill/roll_forward", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BIPED_ROLL_BACKWARD = builder.nextAccessor("biped/skill/roll_backward", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BIPED_STEP_FORWARD = builder.nextAccessor("biped/skill/step_forward", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BIPED_STEP_BACKWARD = builder.nextAccessor("biped/skill/step_backward", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BIPED_STEP_LEFT = builder.nextAccessor("biped/skill/step_left", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BIPED_STEP_RIGHT = builder.nextAccessor("biped/skill/step_right", (accessor) ->
                new ActionAnimation(0.1F, accessor, Armatures.BIPED));
        BLADE_RUSH_COMBO1_PONDER = builder.nextAccessor("biped/skill/blade_rush_combo1", (accessor) ->
                new AttackAnimation(0.1F, 0.0F, 0.15F, 0.35F, 0.85F, BIPED_BODY_COLLIDER, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.35F))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE)
                        .newTimePair(0.0F, 0.65F)
                        .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false));
        BLADE_RUSH_COMBO2_PONDER = builder.nextAccessor("biped/skill/blade_rush_combo2", (accessor) ->
                new AttackAnimation(0.1F, 0.0F, 0.15F, 0.35F, 0.85F, BIPED_BODY_COLLIDER, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.35F))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE)
                        .newTimePair(0.0F, 0.65F)
                        .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false));
        BLADE_RUSH_COMBO3_PONDER = builder.nextAccessor("biped/skill/blade_rush_combo3", (accessor) ->
                new AttackAnimation(0.1F, 0.0F, 0.2F, 0.45F, 0.85F, BIPED_BODY_COLLIDER, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.35F))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE)
                        .newTimePair(0.0F, 0.6F)
                        .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false));
        BLADE_RUSH_HIT_PONDER = builder.nextAccessor("biped/interact/blade_rush_hit", (accessor) ->
                new LongHitAnimation(0.1F, accessor, Armatures.BIPED));
        BLADE_RUSH_TRY_PONDER = builder.nextAccessor("biped/skill/blade_rush_try", (accessor) ->
                new AttackAnimation(0.1F, 0.0F, 0.4F, 0.4F, 0.45F, ColliderPreset.BIPED_BODY_COLLIDER, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.15F, 0.35F)));
        BLADE_RUSH_EXECUTE_BIPED_PONDER = builder.nextAccessor("biped/skill/blade_rush_execute", (accessor) ->
                new AttackAnimation(0.1F, 0.1F, 0.1F, 0.5F, 1.5F, BIPED_BODY_COLLIDER, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.25F))
                        .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                        .addEvents(
                                AnimationEvent.InTimeEvent.create(0.1F, (entitypatch, animation, params) -> {
                                    LivingEntity grapplingTarget = entitypatch.getGrapplingTarget();

                                    if (grapplingTarget != null) {
                                        entitypatch.playSound(EpicFightSounds.BLADE_HIT.get(), 0.0F, 0.0F);
                                    }
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.InTimeEvent.create(0.3F, (entitypatch, animation, params) -> {
                                    LivingEntity grapplingTarget = entitypatch.getGrapplingTarget();

                                    if (grapplingTarget != null) {
                                        entitypatch.playSound(EpicFightSounds.BLADE_HIT.get(), 0.0F, 0.0F);
                                    }
                                }, AnimationEvent.Side.CLIENT)
                        ));
        BLADE_RUSH_FAILED_PONDER = builder.nextAccessor("biped/skill/blade_rush_failed", (accessor) ->
                new ActionAnimation(0.0F, 0.85F, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.0F)));
    }

    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(EpicFightPonder.MOD_ID, EFPAnimations::buildAnimations);
    }

}
