package org.arc.epic_ponder.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arc.epic_ponder.EpicFightPonder;
import org.arc.epic_ponder.entity.DummyPlayerEntity;
import org.arc.epic_ponder.entity.DummyVictimPlayerEntity;

public class EFPEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EpicFightPonder.MOD_ID);

    public static final RegistryObject<EntityType<DummyPlayerEntity>> DUMMY_PLAYER = ENTITIES.register("dummy_player",
            () -> EntityType.Builder.of(DummyPlayerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("dummy_player"));

    public static final RegistryObject<EntityType<DummyVictimPlayerEntity>> DUMMY_VICTIM_PLAYER = ENTITIES.register("dummy_victim_player",
            () -> EntityType.Builder.of(DummyVictimPlayerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("dummy_victim_player"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}