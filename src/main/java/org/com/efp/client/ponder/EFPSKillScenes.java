package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.gameasset.EFPAnimations;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.List;
import java.util.function.Consumer;

public class EFPSKillScenes {

    public static void showcaseNoSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        showcaseNoSkill(baseScene, util, 11, "nothing");
    }

    public static void showcaseNoSkill(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        double center = size / 2.0D;

        EFPSceneUtils.setupStandardScene(builder, size, sceneId, "epic_fight_ponder.ponder." + sceneId + ".title");
        ElementLink<EntityElement> dummy = EFPSceneUtils.spawnDummyActor(builder, center, 0.5, center, 180, null, null);

        world.playAnimation(dummy, Animations.BIPED_SIT, 0.0F);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseStepSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_step", "epic_fight_ponder.ponder.skill_step.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker,true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_step.text_1", 80, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, EFPAnimations.BIPED_STEP_BACKWARD, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.BIPED_STEP_FORWARD, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.BIPED_STEP_LEFT, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.BIPED_STEP_RIGHT, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseRollSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_roll", "epic_fight_ponder.ponder.skill_roll.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker,true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_roll.text_1", 80, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, EFPAnimations.BIPED_ROLL_BACKWARD, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.BIPED_ROLL_FORWARD, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    /**
     * 技能展示：完美闪避 (Technician)
     */
    public static void showcaseTechnicianSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_technician", "epic_fight_ponder.ponder.skill_technician.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 1.9;

        ItemStack victimWeapon = new ItemStack(EpicFightItems.IRON_LONGSWORD.get());
        ItemStack attackerWeapon = new ItemStack(EpicFightItems.UCHIGATANA.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        world.modifyEntityMovement(victim, true);

        builder.idle(10);

        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.playCinematicDodgeStrike(
                builder, util, attacker, victim,
                Animations.BATTOJUTSU,
                EFPAnimations.BIPED_STEP_BACKWARD.get(),
                39, 4, 40,
                "epic_fight_ponder.ponder.skill_technician.text_1",
                "epic_fight_ponder.ponder.skill_technician.text_2",
                5.5, 1, 5.5
        );

        builder.idle(40);
        builder.markAsFinished();
    }

    /**
     * 技能展示：防御 (Guard)
     */
    public static void showcaseGuardSkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_guard", "epic_fight_ponder.ponder.skill_guard.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 1.9;

        ItemStack victimWeapon = new ItemStack(EpicFightItems.UCHIGATANA.get());
        ItemStack attackerWeapon = new ItemStack(EpicFightItems.DIAMOND_TACHI.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);

        builder.idle(20);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_guard.text_1", 80, (int) centerX, 2, (int) centerZ);
        world.playAnimation(victim, Animations.UCHIGATANA_GUARD, 0.0F);
        builder.idle(10);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = EFPSceneUtils.getSafeComboMotions(attackerWeapon, CapabilityItem.Styles.TWO_HAND);
        int attackCount = comboMotions.size() - 2;

        Consumer<PonderCombatEvent.Hit> normalGuardCallback = EFPSceneUtils.createStandardGuardCallback(Animations.UCHIGATANA_GUARD_HIT.get(), Animations.UCHIGATANA_GUARD.get());
        Consumer<PonderCombatEvent.Hit> lastGuardCallback = EFPSceneUtils.createStandardGuardCallback(Animations.UCHIGATANA_GUARD_HIT.get(), null);

        EFPSceneUtils.playInteractiveCombo(builder, attacker, comboMotions, attackCount, normalGuardCallback, lastGuardCallback);

        builder.idle(30);
        builder.markAsFinished();
    }

    /**
     * 技能展示：防御击破 (Guard Break) - 抵挡跑攻，被跳劈破防
     */
    public static void showcaseGuardSkillBreak(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_guard_break", "epic_fight_ponder.ponder.skill_guard_break.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 2;

        ItemStack victimWeapon = new ItemStack(EpicFightItems.UCHIGATANA.get());
        ItemStack attackerWeapon = new ItemStack(EpicFightItems.DIAMOND_TACHI.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);

        builder.idle(20);

        world.playAnimation(victim, Animations.UCHIGATANA_GUARD, 0.0F);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = EFPSceneUtils.getSafeComboMotions(attackerWeapon, CapabilityItem.Styles.TWO_HAND);
        int totalMotions = comboMotions.size();

        AnimationManager.AnimationAccessor<? extends AttackAnimation> dashAttack = comboMotions.get(totalMotions - 2);
        AnimationManager.AnimationAccessor<? extends AttackAnimation> jumpAttack = comboMotions.get(totalMotions - 1);

        Consumer<PonderCombatEvent.Hit> guardCallback = EFPSceneUtils.createStandardGuardCallback(Animations.UCHIGATANA_GUARD_HIT.get(), Animations.UCHIGATANA_GUARD.get());
        Consumer<PonderCombatEvent.Hit> breakCallback = EFPSceneUtils.createGuardBreakCallback(Animations.BIPED_COMMON_NEUTRALIZED.get());

        // 第一击：跑攻 -> 触发标准格挡
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_guard_break.text_1", 40, (int) centerX, 2, (int) centerZ);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, dashAttack, 1.0F, guardCallback);

        world.waitForInaction(attacker);
        builder.idle(10);

        // 第二击：跳劈破防
        world.simulateJump(attacker);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_guard_break.text_2", 80, (int) centerX, 0, (int) centerZ);
        builder.idle(5);

        // 跳劈命中 -> 触发破防回调
        EFPSceneUtils.playInteractiveStrike(builder, attacker, jumpAttack, 0.45F, breakCallback);

        world.waitForInaction(attacker);

        builder.idle(40);
        builder.markAsFinished();
    }

    /**
     * 技能展示：完美招架 (Parry)
     */
    public static void showcaseParrySkill(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_parry", "epic_fight_ponder.ponder.skill_parry.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 1.9;

        ItemStack victimWeapon = new ItemStack(EpicFightItems.UCHIGATANA.get());
        ItemStack attackerWeapon = new ItemStack(EpicFightItems.NETHERITE_GREATSWORD.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);

        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = EFPSceneUtils.getSafeComboMotions(attackerWeapon, CapabilityItem.Styles.TWO_HAND);
        int attackCount = comboMotions.size() - 2;

        Consumer<PonderCombatEvent.Hit> parryCallback = EFPSceneUtils.createCyclicParryCallback(
                Animations.SWORD_GUARD_ACTIVE_HIT1.get(),
                Animations.SWORD_GUARD_ACTIVE_HIT2.get()
        );

        EFPSceneUtils.playInteractiveComboWithDefensiveSetup(
                builder, util, attacker, victim,
                comboMotions, attackCount,
                Animations.UCHIGATANA_GUARD.get(),
                parryCallback, parryCallback,
                "epic_fight_ponder.ponder.skill_parry.text_1", centerX, 2, centerZ,
                "epic_fight_ponder.ponder.skill_parry.text_2", centerX, 0.5, centerZ
        );

        builder.idle(30);
        builder.markAsFinished();
    }
}