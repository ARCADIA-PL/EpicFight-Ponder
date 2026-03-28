package org.com.efp.gameasset;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.com.efp.EpicFightPonder;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(modid = EpicFightPonder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFPAnimations {
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_FORWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_BACKWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_LEFT;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_STEP_RIGHT;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_ROLL_FORWARD;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_ROLL_BACKWARD;

    public static void buildAnimations(AnimationManager.AnimationBuilder builder) {
        BIPED_ROLL_FORWARD = builder.nextAccessor("biped/skill/roll_forward", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
        BIPED_ROLL_BACKWARD = builder.nextAccessor("biped/skill/roll_backward", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
        BIPED_STEP_FORWARD = builder.nextAccessor("biped/skill/step_forward", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
        BIPED_STEP_BACKWARD = builder.nextAccessor("biped/skill/step_backward", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
        BIPED_STEP_LEFT = builder.nextAccessor("biped/skill/step_left", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
        BIPED_STEP_RIGHT = builder.nextAccessor("biped/skill/step_right", (accessor) ->
                new ActionAnimation(0.1F,accessor, Armatures.BIPED));
    }

    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(EpicFightPonder.MOD_ID, EFPAnimations::buildAnimations);
    }

}
