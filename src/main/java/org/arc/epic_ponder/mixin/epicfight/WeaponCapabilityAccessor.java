package org.arc.epic_ponder.mixin.epicfight;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.List;
import java.util.Map;

@Mixin(value = WeaponCapability.class, remap = false)
public interface WeaponCapabilityAccessor {

    /**
     * 强行暴露出底层的 autoAttackMotions Map
     */
    @Accessor("autoAttackMotions")
    Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> getAutoAttackMotions();
}