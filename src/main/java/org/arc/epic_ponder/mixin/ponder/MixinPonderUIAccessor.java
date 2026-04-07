package org.arc.epic_ponder.mixin.ponder;

import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PonderUI.class, remap = false)
public interface MixinPonderUIAccessor {

    @Accessor("stack")
    ItemStack efp$getStack();
}