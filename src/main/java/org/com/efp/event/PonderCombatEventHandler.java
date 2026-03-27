package org.com.efp.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.com.efp.EpicFightPonder;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.entity.DummyEntityPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod.EventBusSubscriber(modid = EpicFightPonder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PonderCombatEventHandler {

    /**
     * 当 Ponder 里的受害者被命中时，自动为其指派受击反馈（Stun Reaction）。
     */
    @SubscribeEvent
    public static void onBeHitDefaultReaction(PonderCombatEvent.BeHit event) {
        if (event.isCanceled()) return;
        System.out.println("Animation: " + event.getAnimation() + " Phase: " + event.getPhaseOrder());
        DummyEntityPatch<?> victimEntityPatch = EpicFightCapabilities.getEntityPatch(event.getTarget(), DummyEntityPatch.class);
        if (victimEntityPatch != null) {
            victimEntityPatch.playAnimation(Animations.BIPED_HIT_SHORT, 0F);
        }
    }
}