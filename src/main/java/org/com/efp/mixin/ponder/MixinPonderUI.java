package org.com.efp.mixin.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

import java.util.List;

@Mixin(value = PonderUI.class, remap = false)
public class MixinPonderUI {

    @Shadow
    ItemStack stack;

    @Inject(method = "of(Lnet/minecraft/world/item/ItemStack;)Lnet/createmod/ponder/foundation/ui/PonderUI;", at = @At("HEAD"), cancellable = true)
    private static void routeSkillBookNBTToConcept(ItemStack item, CallbackInfoReturnable<PonderUI> cir) {
        if (item.getTag() != null && item.getItem() instanceof SkillBookItem && item.hasTag() && item.getTag().contains("skill")) {
            String skillId = item.getTag().getString("skill");
            ResourceLocation rawRl = ResourceLocation.parse(skillId);

            ResourceLocation targetRl = ResourceLocation.fromNamespaceAndPath(rawRl.getNamespace(), "skill_" + rawRl.getPath());

            List<PonderScene> compiledScenes = PonderIndex.getSceneAccess().compile(targetRl);

            if (!compiledScenes.isEmpty()) {
                cir.setReturnValue(PonderUI.of(targetRl));
            }
        }
    }

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("TAIL"))
    private void fixSkillBookIconAndStack(List<PonderScene> scenes, CallbackInfo ci) {
        if (scenes == null || scenes.isEmpty()) return;

        ResourceLocation location = scenes.get(0).getLocation();

        if (location.getPath().startsWith("skill_")) {
            String originalSkillId = location.getNamespace() + ":" + location.getPath().substring(6);

            ItemStack skillBook = new ItemStack(EpicFightItems.SKILLBOOK.get());
            skillBook.getOrCreateTag().putString("skill", originalSkillId);

            this.stack = skillBook;
        }
    }

    @Inject(method = "getBreadcrumbTitle", at = @At("HEAD"), cancellable = true)
    private void fixBreadcrumbTitleForSkill(CallbackInfoReturnable<String> cir) {
        if (this.stack != null && !this.stack.isEmpty() && this.stack.getItem() instanceof SkillBookItem) {
            cir.setReturnValue(this.stack.getHoverName().getString());
        }
    }
}