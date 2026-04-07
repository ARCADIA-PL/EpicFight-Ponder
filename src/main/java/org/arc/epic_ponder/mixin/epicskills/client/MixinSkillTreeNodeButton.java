package org.arc.epic_ponder.mixin.epicskills.client;

import com.yesman.epicskills.client.gui.screen.SkillTreeScreen;
import net.minecraft.util.FormattedCharSequence;
import org.arc.epic_ponder.compat.EFMSkillsPonderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.skill.Skill;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SkillTreeScreen.TreePage.NodeButton.class)
public abstract class MixinSkillTreeNodeButton {

    @Shadow
    public abstract Skill getSkill();

    @Redirect(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/yesman/epicskills/client/gui/screen/SkillTreeScreen;setTooltipForNextRenderPass(Ljava/util/List;)V"
            )
    )
    private void redirectSetTooltip(SkillTreeScreen instance, List<FormattedCharSequence> lines) {
        Skill skill = this.getSkill();

        List<FormattedCharSequence> newLines = new ArrayList<>(lines);

        if (EFMSkillsPonderManager.hasPonderScene(skill)) {
            EFMSkillsPonderManager.setHoveredSkill(skill);

            newLines.add(EFMSkillsPonderManager.getPonderTooltip().getVisualOrderText());
        }

        instance.setTooltipForNextRenderPass(newLines);
    }
}