package org.arc.epic_ponder.mixin.epicskills.client;

import com.yesman.epicskills.client.gui.screen.SkillInfoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.arc.epic_ponder.compat.EFMSkillsPonderManager;
import org.arc.epic_ponder.mixin.epicskills.AccessorSkillBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.Skill;

@Mixin(value = SkillInfoScreen.class)
public class MixinSkillInfoScreen {

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void onRenderInfoScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, boolean asBackground, CallbackInfo ci) {
        if (!asBackground) {
            Skill skill = ((AccessorSkillBookScreen) this).getSkill();

            if (EFMSkillsPonderManager.hasPonderScene(skill)) {
                EFMSkillsPonderManager.setHoveredSkill(skill);

                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        EFMSkillsPonderManager.getPonderTooltip(),
                        20, 20, 0xFFFFFF
                );
            }
        }
    }
}