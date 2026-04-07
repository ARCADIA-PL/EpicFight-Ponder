package org.arc.epic_ponder.mixin.epicskills;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.skill.Skill;

@Mixin(value = SkillBookScreen.class, remap = false)
public interface AccessorSkillBookScreen {
    @Accessor("skill")
    Skill getSkill();
}