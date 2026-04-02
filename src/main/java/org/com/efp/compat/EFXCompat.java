package org.com.efp.compat;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.asanginxst.epicfightx.gameassets.animations.ExtraAnimations;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.client.ponder.EFPSceneUtils;
import org.com.efp.gameasset.EFPAnimations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.function.Consumer;

public class EFXCompat {

    public static void showcaseStepSkill_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_step", "epic_fight_ponder.ponder.skill_step.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker, true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_step.text_1", 100, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, AnimationsX.BIPED_STEP_BACKWARD_PONDER, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, AnimationsX.BIPED_STEP_FORWARD_PONDER, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, AnimationsX.BIPED_STEP_RIGHT_PONDER, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, AnimationsX.BIPED_STEP_LEFT_PONDER, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseRollSkill_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_roll", "epic_fight_ponder.ponder.skill_roll.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker, true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_roll.text_1", 100, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, AnimationsX.BIPED_ROLL_BACKWARD_PONDER, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, AnimationsX.BIPED_ROLL_FORWARD_PONDER, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }


    public static void showcaseBladeRush_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "blade_rush", "epic_fight_ponder.ponder.blade_rush.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()), new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 2.5, 7, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_1", 20, 3, 1, 12);
        builder.idle(10);

        world.lookAtEntity(attacker, victim);
        world.playAnimation(attacker, AnimationsX.BLADE_RUSH_COMBO1_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker,3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_2", 30, 5, 0, 5);
        builder.idle(20);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForCanUseSkill(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, AnimationsX.BLADE_RUSH_COMBO2_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker,3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForCanUseSkill(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, AnimationsX.BLADE_RUSH_COMBO3_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker,3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_3", 30, 5, 0, 7);
        builder.idle(25);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, AnimationsX.BLADE_RUSH_TRY_PONDER, 0.0F);
        world.waitForAnimationProgress(attacker, 0.15F);
        world.setPosition(attacker, 3.5, 2.5, 6.5);
        world.waitForAnimationProgress(attacker, 0.2F);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_4", 50, 3, 2, 6);
        builder.idle(15);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.BLADE_RUSH_EXECUTE_BIPED_PONDER, 1.0F, normalKillCallBack);
        world.waitForCanBasicAttack(attacker);
        world.setYRot(attacker, 180);

        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseEviscerate_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "eviscerate_efx", "epic_fight_ponder.ponder.eviscerate_efx.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 6.5, 180, new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 2, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.eviscerate_efx.text_1", 20, 5, 1, 7);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalExHitCallBack = EFPSceneUtils.createStandardExHitCallback(AnimationsX.EVISCERATE_FIRST.get(), AnimationsX.EVISCERATE_SECOND.get(), 6, 1.5F, 0.7F);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.EVISCERATE_FIRST, 1.0F, normalExHitCallBack);
        builder.idle(5);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.eviscerate_efx.text_2", 30, 5, 1, 3);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1.15F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        world.modifyEntityPlaySpeed(attacker, 1);
        builder.idle(3);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.eviscerate_efx.text_3", 40, 2, 2, 2);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseGraspingSpire_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "grasping_spire", "epic_fight_ponder.ponder.grasping_spire.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_SPEAR.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 1.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_1", 20, 5, 1, 6);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalExHitCallBack = EFPSceneUtils.createStandardExHitCallback(AnimationsX.GRASPING_SPIRAL_FIRST.get(), AnimationsX.GRASPING_SPIRAL_SECOND.get(), 15, 1.5F, 0.7F);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.GRASPING_SPIRAL_FIRST, 1.0F, normalExHitCallBack);
        builder.idle(3);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_2", 20, 5, 1, 2);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(8);
        world.modifyEntityPlaySpeed(attacker, 1.15F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_3", 40, 5, 1, 2);
        world.modifyEntityPlaySpeed(attacker, 1);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseGuillotine_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "guillotine", "epic_fight_ponder.ponder.guillotine.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.GOLDEN_AXE));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 1.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.guillotine.text_1", 20, 5, 1, 6);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.THE_GUILLOTINE, 1.0F, normalKillCallBack);
        builder.idle(7);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.guillotine.text_2", 30, 5, 1, 4);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(19);
        world.modifyEntityPlaySpeed(attacker, 1.15F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.guillotine.text_3", 40, 5, 1, 2);
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        world.modifyEntityPlaySpeed(attacker, 1);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseSweepingEdge_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "sweeping_edge", "epic_fight_ponder.ponder.sweeping_edge.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.IRON_SWORD));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim2 = EFPSceneUtils.spawnDummyVictim(builder, 7, 1.0, 5.5, 90, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.sweeping_edge.text_1", 80, 5, 1, 6);
        builder.idle(10);
        world.playAnimation(attacker, AnimationsX.SWEEPING_EDGE, 0.0F);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseDancingEdge_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "dancing_edge", "epic_fight_ponder.ponder.dancing_edge.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim2 = EFPSceneUtils.spawnDummyVictim(builder, 6.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim3 = EFPSceneUtils.spawnDummyVictim(builder, 4.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        builder.idle(10);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.dancing_edge.text_1", 80, 5, 1, 6);
        builder.idle(10);
        world.playAnimation(attacker, AnimationsX.DANCING_EDGE, 0.0F);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    /**
     * 技能展示：EFX RushingTempo
     */
    public static void showcaseRushingTempo_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "tachi_rushing_tempo", "epic_fight_ponder.ponder.tachi_rushing_tempo.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_TACHI.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        builder.idle(20);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_1", 40, 5, 1, 5);
        builder.idle(50);

        // 普攻起手
        world.playAnimation(attacker, AnimationsX.TACHI_AUTO1, 0.0F);

        // 第一个派生慢动作提示
        world.waitForCanUseSkill(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.05F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_2", 40, 5, 2, 5);
        builder.idle(50);

        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.playAnimation(attacker, AnimationsX.RUSHING_TEMPO1, 0.0F);

        //微时缓增加打击感
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.25F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_3", 100, 5, 2, 5);
        builder.idle(15);

        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, AnimationsX.TACHI_AUTO2, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, AnimationsX.RUSHING_TEMPO2, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, AnimationsX.TACHI_AUTO3, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, AnimationsX.RUSHING_TEMPO3, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, ExtraAnimations.TACHI_AUTO4, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, ExtraAnimations.RUSHING_TEMPO4, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, ExtraAnimations.TACHI_AUTO5, 0.25F, 10);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, ExtraAnimations.RUSHING_TEMPO5, 0.25F, 10);

        world.waitForInaction(attacker);

        //冲刺派生
        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.simulateSpring(attacker, 0.7F, 4);
        builder.idle(4);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_4", 80, 5, 2, 5);

        world.playAnimation(attacker, AnimationsX.TACHI_DASH, 0.0F);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, ExtraAnimations.RUSHING_DASH, 0.25F, 10);

        //跳劈派生
        world.waitForInaction(attacker);
        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.setPosition(attacker, 5.5, 1, 5.5);
        builder.idle(5);
        world.simulateJump(attacker);
        builder.idle(8);

        world.playAnimation(attacker, ExtraAnimations.TACHI_AIR_SLASH, 0.0F);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, ExtraAnimations.RUSHING_AIR_SLASH, 0.25F, 10);
        world.waitForInaction(attacker);
        world.resetJump(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaPassive_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "uchigatana_passive", "epic_fight_ponder.ponder.uchigatana_passive.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.TWO_HAND);
        EFPSceneUtils.updateSheathState(builder, attacker, 0);
        builder.idle(20);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_passive.text_1", 80, 5, 1, 5);
        builder.idle(20);
        world.playAnimation(attacker, AnimationsX.BIPED_UCHIGATANA_SCRAP, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.SHEATH);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaBattojutsu_UnSheath_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "uchigatana_battojutsu_unsheath", "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 7, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim1 = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 3, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        EFPSceneUtils.updateSheathState(builder, attacker, 0);
        world.modifyEntityMovement(attacker, true);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.BATTOJUTSU, 1.0F, normalKillCallBack);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_1", 30, 3, 1, 12);
        builder.idle(1);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(20);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForAttacking(attacker);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_2", 20, 3, 1, 7);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);
        world.setPosition(attacker, 3.5, 1, 12);
        builder.idle(10);
        world.simulateSpring(attacker, 1, 5);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_3", 40, 3, 1, 10);
        builder.idle(5);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.BATTOJUTSU_DASH, 1.0F, normalKillCallBack);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        world.waitForAttacking(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaBattojutsu_Sheath_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "uchigatana_battojutsu_sheath", "epic_fight_ponder.ponder.uchigatana_battojutsu_sheath.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.SHEATH);
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 7, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim1 = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 3, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        EFPSceneUtils.updateSheathState(builder, attacker, 1);
        world.modifyEntityMovement(attacker, true);
        builder.idle(20);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_sheath.text_1", 40, 3, 1, 12);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();

        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.BATTOJUTSU, 1.0F, -0.6F, normalKillCallBack);
        builder.idle(3);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.TWO_HAND);
        builder.idle(15);
        world.setPosition(attacker, 3.5, 1, 12);
        EFPSceneUtils.updateSheathState(builder, attacker, 1);
        builder.idle(15);
        world.simulateSpring(attacker, 1, 5);
        builder.idle(5);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, AnimationsX.BATTOJUTSU_DASH, 1.0F, -0.6F, normalKillCallBack);
        world.waitForInaction(attacker);
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.TWO_HAND);

        builder.idle(30);
        builder.markAsFinished();
    }

    /**
     * 技能展示：完美闪避 (Technician)
     */
    public static void showcaseTechnicianSkill_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
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

        builder.idle(20);

        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.playCinematicDodgeStrike(
                builder, util, attacker, victim,
                AnimationsX.BATTOJUTSU,
                EFPAnimations.BIPED_STEP_BACKWARD.get(),
                38, 4, 40,
                "epic_fight_ponder.ponder.skill_technician.text_1",
                "epic_fight_ponder.ponder.skill_technician.text_2",
                5.5, 1, 5.5
        );

        builder.idle(40);
        builder.markAsFinished();
    }
}
