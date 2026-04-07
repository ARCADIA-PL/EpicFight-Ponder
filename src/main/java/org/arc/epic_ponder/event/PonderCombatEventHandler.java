package org.arc.epic_ponder.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.arc.epic_ponder.EpicFightPonder;
import org.arc.epic_ponder.api.event.PonderCombatEvent;
import org.arc.epic_ponder.api.ponder.EpicFightSceneBuilder;
import org.arc.epic_ponder.entity.DummyEntityPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod.EventBusSubscriber(modid = EpicFightPonder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PonderCombatEventHandler {

    /**
     * 当 Ponder 里的假人被命中时触发
     */
    @SubscribeEvent
    public static void onBeHitDefaultReaction(PonderCombatEvent.BeHit event) {
        if (event.isCanceled()) return;
        DummyEntityPatch<?> victimEntityPatch = EpicFightCapabilities.getEntityPatch(event.getTarget(), DummyEntityPatch.class);
        if (victimEntityPatch != null) {
            CompoundTag data = victimEntityPatch.getOriginal().getPersistentData();
            if (data.contains(EpicFightSceneBuilder.NO_STUN) && !data.getBoolean(EpicFightSceneBuilder.NO_STUN)) {
                victimEntityPatch.playAnimation(Animations.BIPED_HIT_SHORT, 0F);
            } else if (!data.contains(EpicFightSceneBuilder.NO_STUN)) {
                victimEntityPatch.playAnimation(Animations.BIPED_HIT_SHORT, 0F);
            }
        }
    }
}