package org.com.efp.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.com.efp.EpicFightPonder;
import org.com.efp.entity.DummyPlayerEntity;

public class EFPEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EpicFightPonder.MOD_ID);

    public static final RegistryObject<EntityType<DummyPlayerEntity>> DUMMY_PLAYER = ENTITIES.register("dummy_player",
            () -> EntityType.Builder.of(DummyPlayerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("dummy_player"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}