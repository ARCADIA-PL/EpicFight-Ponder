package org.com.efp.mixin.epicfight.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.config.ClientConfig;

/**
 * 修复开启compute shader后实体无法在PonderLevel渲染的bug——By P1nero
 */
@Mixin(RenderEngine.Events.class)
public class RenderEngineEventsMixin {

    @WrapMethod(method = "renderLivingEvent", remap = false)
    private static void efp$wrapRenderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event, Operation<Void> original) {
        LivingEntity living = event.getEntity();
        if (living.level() instanceof PonderLevel) {
            boolean computeShaderSetting = ClientConfig.activateComputeShader;
            ClientConfig.activateComputeShader = false;
            original.call(event);
            ClientConfig.activateComputeShader = computeShaderSetting;
            return;
        }
        original.call(event);
    }

}
