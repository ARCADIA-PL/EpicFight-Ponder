package org.arc.efp.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arc.efp.EpicFightPonder;

public class EFPParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EpicFightPonder.MOD_ID);

    public static final RegistryObject<SimpleParticleType> PONDER_AFTERIMAGE = PARTICLES.register("ponder_afterimage", () -> new SimpleParticleType(true));

}
