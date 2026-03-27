package org.com.efp.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.com.efp.EpicFightPonder;
import org.com.efp.client.render.DummyPlayerRenderer;
import org.com.efp.client.render.DummyVictimPlayerRenderer;
import org.com.efp.client.render.patched.PDummyPlayerRenderer;
import org.com.efp.client.render.patched.PDummyVictimPlayerRenderer;
import org.com.efp.entity.DummyEntityPatch;
import org.com.efp.entity.DummyPlayerEntity;
import org.com.efp.registry.EFPEntities;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(modid = EpicFightPonder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EFPEntities.DUMMY_PLAYER.get(), DummyPlayerEntity.createAttributes());
        event.put(EFPEntities.DUMMY_VICTIM_PLAYER.get(), DummyPlayerEntity.createAttributes());
    }

    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(EFPEntities.DUMMY_PLAYER.get(), (entity) -> DummyEntityPatch::new);
        event.getTypeEntry().put(EFPEntities.DUMMY_VICTIM_PLAYER.get(), (entity) -> DummyEntityPatch::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EFPEntities.DUMMY_PLAYER.get(), DummyPlayerRenderer::new);
        event.registerEntityRenderer(EFPEntities.DUMMY_VICTIM_PLAYER.get(), DummyVictimPlayerRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPatchedRenderer(PatchedRenderersEvent.Add event) {
        event.addPatchedEntityRenderer(EFPEntities.DUMMY_PLAYER.get(),
                entityType -> new PDummyPlayerRenderer(event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(EFPEntities.DUMMY_VICTIM_PLAYER.get(),
                entityType -> new PDummyVictimPlayerRenderer(event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
    }

    public static void registerArmatures() {
        Armatures.registerEntityTypeArmature(EFPEntities.DUMMY_PLAYER.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(EFPEntities.DUMMY_VICTIM_PLAYER.get(), Armatures.BIPED);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModEvents::registerArmatures);
    }
}
