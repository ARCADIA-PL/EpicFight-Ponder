package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.gameasset.EFPAnimations;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.List;
import java.util.function.Consumer;

public class EFPWeaponScenes {
    public static void showcaseTachiBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "tachi_basic_attack_combo",
                EpicFightItems.DIAMOND_TACHI.get().getDefaultInstance());
    }

    public static void showcaseUchigatanaBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "uchigatana_basic_attack_combo",
                EpicFightItems.UCHIGATANA.get().getDefaultInstance());
    }

    public static void showcaseUchigatanaBasicAttackCombo_Sheath(SceneBuilder baseScene, SceneBuildingUtil util) {
        ItemStack sheathStack = EpicFightItems.UCHIGATANA.get().getDefaultInstance();
        EFPSceneUtils.showcaseUchigatanaStandardWeaponCombo(baseScene, util, 11, "uchigatana_basic_attack_combo_sheath",
                sheathStack, CapabilityItem.Styles.SHEATH);
    }

    public static void showcaseLongSwordBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "longsword_basic_attack_combo",
                EpicFightItems.DIAMOND_LONGSWORD.get().getDefaultInstance(), CapabilityItem.Styles.TWO_HAND);
    }

    public static void showcaseLongSwordBasicAttackCombo_OneHand(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "longsword_basic_attack_combo_onehand",
                EpicFightItems.DIAMOND_LONGSWORD.get().getDefaultInstance(), Items.SHIELD.getDefaultInstance(), CapabilityItem.Styles.ONE_HAND);
    }

    public static void showcaseLongSwordBasicAttackCombo_Ochs(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "longsword_ochs", "epic_fight_ponder.ponder.longsword_ochs.title");

        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 6.5, 180,
                new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()), ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1, 4.5, 0,
                new ItemStack(EpicFightItems.IRON_GREATSWORD.get()), ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);

        world.modifyEntityNoStun(victim, true);
        world.modifyEntityNoStun(attacker, true);

        builder.idle(20);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.longsword_ochs.text_1", 20, 4, 2, 5);
        world.playAnimation(attacker, Animations.BIPED_LIECHTENAUER_READY, 0);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, SoundEvents.ARMOR_EQUIP_CHAIN);
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.OCHS);
        builder.idle(25);

        List<ClashFrame> clashSequence = List.of(
                new ClashFrame(Animations.GREATSWORD_AUTO1, 2, Animations.LONGSWORD_LIECHTENAUER_AUTO1, 5, false),
                new ClashFrame(Animations.GREATSWORD_AUTO2, 6, Animations.LONGSWORD_LIECHTENAUER_AUTO2, 6, false),
                new ClashFrame(Animations.GREATSWORD_AUTO1, 3, Animations.LONGSWORD_LIECHTENAUER_AUTO3, 5, false),
                new ClashFrame(Animations.GREATSWORD_AUTO2, 4, Animations.LONGSWORD_AIR_SLASH, 7, false)
        );

        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.longsword_ochs.text_2", 25, 4, 2, 5);
        builder.idle(20);

        for (int i = 0; i < clashSequence.size(); i++) {
            ClashFrame frame = clashSequence.get(i);

            if (frame.isJump) {
                world.setPosition(attacker, 5.5, 1, 6.5);
                builder.idle(5);
                world.simulateJump(attacker);
            }

            world.playAnimation(victim, frame.victimAnim, 0.0F, hitEvent -> {
                if (hitEvent.isCancelable()) hitEvent.setCanceled(true);
            }, beHitEvent -> {
            });

            builder.idle(frame.attackerDelay);

            world.playAnimation(attacker, frame.attackerAnim, 0.0F, hitEvent -> {
                EFPSceneUtils.playSoundClientSide(EpicFightSounds.BLADE_RUSH_FINISHER.get(), 1.0F, 1.0F);
            }, beHitEvent -> {
            });

            world.waitForPhaseLevel(attacker, 1);

            builder.idle(frame.clashDelay);

            world.modifyEntityPlaySpeed(attacker, 0.05F);
            world.modifyEntityPlaySpeed(victim, 0.05F);

            baseScene.addInstruction(scene -> {
                Entity attackerEntity = EFPSceneUtils.resolveEntity(scene.builder(), attacker);
                Entity victimEntity = EFPSceneUtils.resolveEntity(scene.builder(), victim);
                if (attackerEntity != null && victimEntity != null) {
                    EFPSceneUtils.playSoundClientSide(EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
                    EFPSceneUtils.spawnEfmHitParticleClientSide(
                            attackerEntity.level(), EpicFightParticles.HIT_BLUNT.get(),
                            attackerEntity, victimEntity, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO
                    );
                }
            });

            if (i == 0) {
                builder.idle(4);
                EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.longsword_ochs.text_3", 70, 4, 2, 5);
            }

            builder.idle(15);

            world.modifyEntityPlaySpeed(attacker, 1.0F);
            world.modifyEntityPlaySpeed(victim, 1.0F);

            if (!frame.isJump && i < clashSequence.size() - 1) {
                world.waitForCanBasicAttack(attacker);
            } else {
                world.waitForInaction(attacker);
                if (frame.isJump) {
                    world.resetJump(attacker);
                }
            }
        }

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseSpearBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "spear_basic_attack_combo",
                EpicFightItems.DIAMOND_SPEAR.get().getDefaultInstance());
    }

    public static void showcaseSpearBasicAttackCombo_OneHand(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "spear_basic_attack_combo_onehand",
                EpicFightItems.DIAMOND_SPEAR.get().getDefaultInstance(), Items.SHIELD.getDefaultInstance(), CapabilityItem.Styles.ONE_HAND);
    }

    public static void showcaseGreatSwordBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "greatsword_basic_attack_combo",
                EpicFightItems.DIAMOND_GREATSWORD.get().getDefaultInstance());
    }

    public static void showcaseSwordBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "sword_basic_attack_combo",
                Items.DIAMOND_SWORD.getDefaultInstance(), CapabilityItem.Styles.ONE_HAND);
    }

    public static void showcaseSwordBasicAttackCombo_Dual(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "sword_basic_attack_combo_dual",
                Items.DIAMOND_SWORD.getDefaultInstance(), Items.DIAMOND_SWORD.getDefaultInstance(), CapabilityItem.Styles.TWO_HAND);
    }

    public static void showcaseDaggerBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "dagger_basic_attack_combo",
                EpicFightItems.DIAMOND_DAGGER.get().getDefaultInstance(), CapabilityItem.Styles.ONE_HAND);
    }

    public static void showcaseDaggerBasicAttackCombo_Dual(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "dagger_basic_attack_combo_dual",
                EpicFightItems.DIAMOND_DAGGER.get().getDefaultInstance(), EpicFightItems.DIAMOND_DAGGER.get().getDefaultInstance(), CapabilityItem.Styles.TWO_HAND);
    }

    public static void showcaseAxeBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "axe_basic_attack_combo",
                Items.DIAMOND_AXE.getDefaultInstance());
    }

    public static void showcaseHeartPiercer(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "heart_piercer", "epic_fight_ponder.ponder.heart_piercer.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_SPEAR.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.heart_piercer.text_1", 30, 5, 1, 5);
        builder.idle(30);

        world.playAnimation(attacker, Animations.HEARTPIERCER, 0.0F);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseSharpStab(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "sharp_stab", "epic_fight_ponder.ponder.sharp_stab.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.IRON_LONGSWORD.get()), new ItemStack(Items.SHIELD));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.sharp_stab.text_1", 30, 5, 1, 5);
        builder.idle(30);

        world.playAnimation(attacker, Animations.SHARP_STAB, 0.0F);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseSteelWhirlWind(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "steel_whirlwind", "epic_fight_ponder.ponder.steel_whirlwind.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.NETHERITE_GREATSWORD.get()));
        world.modifyEntityMovement(attacker, true);
        builder.idle(20);

        world.playAnimation(attacker, Animations.STEEL_WHIRLWIND_CHARGING, 0.0F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.steel_whirlwind.text_1", 30, 5, 1, 5);
        builder.idle(20);
        world.playAnimation(attacker, Animations.STEEL_WHIRLWIND, 0.0F);


        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseBladeRush(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "blade_rush", "epic_fight_ponder.ponder.blade_rush.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()), new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 2.5, 7, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_1", 20, 3, 1, 12);
        builder.idle(10);

        world.lookAtEntity(attacker, victim);
        world.playAnimation(attacker, EFPAnimations.BLADE_RUSH_COMBO1_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker, 3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_2", 30, 5, 0, 5);
        builder.idle(20);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForCanUseSkill(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, EFPAnimations.BLADE_RUSH_COMBO2_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker, 3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForCanUseSkill(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, EFPAnimations.BLADE_RUSH_COMBO3_PONDER, 0.0F);
        world.waitForPhaseLevel(attacker, 3);
        world.modifyEntityPlaySpeed(attacker, 0.1F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_3", 30, 5, 0, 7);
        builder.idle(25);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);
        world.lookAtEntity(attacker, victim);

        world.playAnimation(attacker, EFPAnimations.BLADE_RUSH_TRY_PONDER, 0.0F);
        world.waitForAnimationProgress(attacker, 0.15F);
        world.setPosition(attacker, 3.5, 2.5, 6.5);
        world.waitForAnimationProgress(attacker, 0.2F);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.blade_rush.text_4", 50, 3, 2, 6);
        builder.idle(15);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();
        EFPSceneUtils.playInteractiveStrike(builder, attacker, EFPAnimations.BLADE_RUSH_EXECUTE_BIPED_PONDER, 1.0F, normalKillCallBack);
        world.waitForCanBasicAttack(attacker);
        world.setYRot(attacker, 180);

        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseEviscerate(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "eviscerate", "epic_fight_ponder.ponder.eviscerate.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.NETHERITE_DAGGER.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.eviscerate.text_1", 20, 5, 1, 5);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardExHitCallback(Animations.EVISCERATE_FIRST.get(), Animations.EVISCERATE_SECOND.get(), 6, 1.5F, 0.7F);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.EVISCERATE_FIRST, 1.0F, normalKillCallBack);
        builder.idle(2);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.eviscerate.text_2", 40, 5, 1, 4);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        world.modifyEntityPlaySpeed(attacker, 1);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseGraspingSpire(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "grasping_spire", "epic_fight_ponder.ponder.grasping_spire.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_SPEAR.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 1.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_1", 20, 5, 1, 6);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardExHitCallback(Animations.GRASPING_SPIRAL_FIRST.get(), Animations.GRASPING_SPIRAL_SECOND.get(), 15, 1.5F, 0.7F);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.GRASPING_SPIRAL_FIRST, 1.0F, normalKillCallBack);
        builder.idle(5);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_2", 20, 5, 1, 4);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(8);
        world.modifyEntityPlaySpeed(attacker, 1.15F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.grasping_spire.text_3", 40, 5, 1, 3);
        world.modifyEntityPlaySpeed(attacker, 1);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseGuillotine(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "guillotine", "epic_fight_ponder.ponder.guillotine.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.GOLDEN_AXE));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 1.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.guillotine.text_1", 20, 5, 1, 6);
        builder.idle(10);
        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();
        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.THE_GUILLOTINE, 1.0F, normalKillCallBack);
        builder.idle(7);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.guillotine.text_2", 30, 5, 2, 4);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(19);
        world.modifyEntityPlaySpeed(attacker, 1.15F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.guillotine.text_3", 40, 5, 1, 3);
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(15);
        world.modifyEntityPlaySpeed(attacker, 1);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseSweepingEdge(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "sweeping_edge", "epic_fight_ponder.ponder.sweeping_edge.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.DIAMOND_SWORD));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 2.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        world.modifyEntityMovement(attacker, true);
        builder.idle(10);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.sweeping_edge.text_1", 80, 5, 1, 6);
        builder.idle(10);
        world.playAnimation(attacker, Animations.SWEEPING_EDGE, 0.0F);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseDancingEdge(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "dancing_edge", "epic_fight_ponder.ponder.dancing_edge.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1.0, 3.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        builder.idle(10);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.dancing_edge.text_1", 80, 5, 1, 6);
        builder.idle(10);
        world.playAnimation(attacker, Animations.DANCING_EDGE, 0.0F);
        world.waitForInaction(attacker);
        builder.idle(20);
        builder.markAsFinished();
    }

    /**
     * 技能展示：RushingTempo
     */
    public static void showcaseRushingTempo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "tachi_rushing_tempo", "epic_fight_ponder.ponder.tachi_rushing_tempo.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_TACHI.get()));
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1, 3.5, 0, null);
        builder.idle(20);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_1", 40, 5, 1, 5);
        builder.idle(50);

        // 普攻起手
        world.playAnimation(attacker, Animations.TACHI_AUTO1, 0.0F);

        // 第一个派生慢动作提示
        world.waitForCanUseSkill(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.05F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_2", 40, 5, 2, 5);
        builder.idle(50);

        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.playAnimation(attacker, Animations.RUSHING_TEMPO1, 0.0F);

        //微时缓增加打击感
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.25F);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_3", 100, 5, 2, 5);
        builder.idle(20);

        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, Animations.TACHI_AUTO2, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, Animations.RUSHING_TEMPO2, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, Animations.TACHI_AUTO3, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, Animations.RUSHING_TEMPO3, 0.2F, 5);

        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaPassive(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "uchigatana_passive", "epic_fight_ponder.ponder.uchigatana_passive.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.TWO_HAND);
        EFPSceneUtils.updateSheathState(builder, attacker, 0);
        builder.idle(20);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_passive.text_1", 80, 5, 1, 5);
        builder.idle(20);
        world.playAnimation(attacker, Animations.BIPED_UCHIGATANA_SCRAP, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.SHEATH);
        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaBattojutsu_UnSheath(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "uchigatana_battojutsu_unsheath", "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.TWO_HAND);
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 10.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim1 = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        EFPSceneUtils.updateSheathState(builder, attacker, 0);
        world.modifyEntityMovement(attacker, true);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();

        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.BATTOJUTSU, 1.0F, 0.0F, normalKillCallBack);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_1", 30, 3, 1, 12);
        builder.idle(1);
        world.modifyEntityPlaySpeed(attacker, 0.2F);
        builder.idle(20);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForAttacking(attacker);
        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_2", 20, 3, 1, 11);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(10);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);
        world.setPosition(attacker, 3.5, 1, 12);
        builder.idle(10);
        world.simulateSpring(attacker, 1, 5);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_unsheath.text_3", 40, 3, 1, 10);
        builder.idle(5);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.BATTOJUTSU_DASH, 1.0F, 0.0F, normalKillCallBack);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.SWORD_IN.get());
        world.waitForAttacking(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.3F);
        builder.idle(5);
        world.modifyEntityPlaySpeed(attacker, 1F);
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaBattojutsu_Sheath(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardLongScene(builder, "uchigatana_battojutsu_sheath", "epic_fight_ponder.ponder.uchigatana_battojutsu_sheath.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 3.5, 1, 12.5, 180, new ItemStack(EpicFightItems.UCHIGATANA.get()), null, CapabilityItem.Styles.SHEATH);
        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 10.5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        ElementLink<EntityElement> victim1 = EFPSceneUtils.spawnDummyVictim(builder, 3.5, 1.0, 5, 0, ItemStack.EMPTY, ItemStack.EMPTY);
        EFPSceneUtils.updateSheathState(builder, attacker, 1);
        world.modifyEntityMovement(attacker, true);
        builder.idle(20);
        EFPSceneUtils.showTextWithKeyFrame(builder, util, "epic_fight_ponder.ponder.uchigatana_battojutsu_sheath.text_1", 40, 3, 1, 12);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> normalKillCallBack = EFPSceneUtils.createStandardKillCallback();

        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.BATTOJUTSU, 1.0F, -0.45F, normalKillCallBack);
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
        builder.idle(1);
        EFPSceneUtils.playInteractiveStrike(builder, attacker, Animations.BATTOJUTSU_DASH, 1.0F, -0.45F, normalKillCallBack);
        world.waitForInaction(attacker);
        EFPSceneUtils.changeStyleAndRefreshMotions(builder, attacker, CapabilityItem.Styles.TWO_HAND);

        builder.idle(30);
        builder.markAsFinished();
    }

    /**
     * 用于定义每一回合拼刀动作的配置类
     */
    public static class ClashFrame {
        public final AnimationManager.AnimationAccessor<? extends AttackAnimation> victimAnim;
        public final int attackerDelay;
        public final AnimationManager.AnimationAccessor<? extends AttackAnimation> attackerAnim;
        public final int clashDelay;
        public final boolean isJump;

        public ClashFrame(AnimationManager.AnimationAccessor<? extends AttackAnimation> victimAnim, int attackerDelay,
                          AnimationManager.AnimationAccessor<? extends AttackAnimation> attackerAnim, int clashDelay, boolean isJump) {
            this.victimAnim = victimAnim;
            this.attackerDelay = attackerDelay;
            this.attackerAnim = attackerAnim;
            this.clashDelay = clashDelay;
            this.isJump = isJump;
        }
    }
}