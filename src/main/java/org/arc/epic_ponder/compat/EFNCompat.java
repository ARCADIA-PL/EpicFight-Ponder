package org.arc.epic_ponder.compat;

import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNItem;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.arc.epic_ponder.api.event.PonderCombatEvent;
import org.arc.epic_ponder.api.ponder.EpicFightSceneBuilder;
import org.arc.epic_ponder.client.ponder.EFPSceneUtils;
import org.arc.epic_ponder.entity.DummyEntityPatch;
import org.arc.epic_ponder.gameasset.EFPAnimations;
import org.joml.Vector3d;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.List;
import java.util.function.Consumer;

import static org.arc.epic_ponder.api.ponder.EpicFightSceneBuilder.CAN_MOVE;
import static org.arc.epic_ponder.client.ponder.EFPSceneUtils.*;

public class EFNCompat {

    public static void showcaseStepSkill_EFN(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_step", "epic_fight_ponder.ponder.skill_step.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker, true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_step.text_1", 100, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, EFPAnimations.DODGE_STEP_B_EFN, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.DODGE_STEP_F_EFN, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.DODGE_STEP_R_EFN, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.DODGE_STEP_L_EFN, 0.0F);
        EFPSceneUtils.playStepSoundOnTimeline(builder, attacker);
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseRollSkill_EFN(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_roll", "epic_fight_ponder.ponder.skill_roll.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_LONGSWORD.get()));
        world.modifyEntityMovement(attacker, true);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_roll.text_1", 100, 5, 1, 5);
        builder.idle(20);

        world.playAnimation(attacker, EFPAnimations.DODGE_ROLL_B_EFN, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);
        builder.idle(5);
        world.playAnimation(attacker, EFPAnimations.DODGE_ROLL_F_EFN, 0.0F);
        EFPSceneUtils.playSoundOnTimeline(builder, attacker, EpicFightSounds.ROLL.get());
        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseEFNParrySkill_First(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_parry_efn", "epic_fight_ponder.ponder.skill_parry_efn.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 1.9;

        ItemStack victimWeapon = new ItemStack(EFNItem.SWORD_OF_PIONEER.get());
        ItemStack attackerWeapon = new ItemStack(EFNItem.YAMATO_DMC4_IN_SHEATH.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY);

        world.modifyEntityMovement(victim, false);

        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> customCombo = List.of(
                EFNYamatoAnimations.YAMATO_NORMAL_AUTO1,
                EFNYamatoAnimations.YAMATO_NORMAL_AUTO2,
                EFNYamatoAnimations.YAMATO_EXTEND_AUTO3,
                EFNYamatoAnimations.YAMATO_EXTEND_AUTO4,
                EFNYamatoAnimations.YAMATO_EXTEND_AUTO5
        );

        Consumer<PonderCombatEvent.Hit> normalParryCallback = createYamatoSmartParryCallback(
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1.get(),
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2.get()
        );
        Consumer<PonderCombatEvent.Hit> heavyParryCallback = createEFNParryCallback(
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3.get(), true
        );

        playInteractiveComboWithDefensiveSetup(
                builder, util, attacker, victim,
                customCombo, customCombo.size(),
                Animations.UCHIGATANA_GUARD.get(),
                normalParryCallback, heavyParryCallback,
                "epic_fight_ponder.ponder.skill_parry_efn.text_1", centerX, 2, centerZ,
                "epic_fight_ponder.ponder.skill_parry_efn.text_2", centerX, 0.5, centerZ
        );

        builder.idle(30);
        builder.markAsFinished();
    }

    public static void showcaseEFNParrySkill_Second(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_parry_efn_second", "epic_fight_ponder.ponder.skill_parry_efn_second.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 2.0;

        ItemStack victimWeapon = new ItemStack(EFNItem.SWORD_OF_PIONEER.get());
        ItemStack attackerWeapon = new ItemStack(EFNItem.CRIMSON_MOON.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, centerX, 1.0, attackerZ, 0, attackerWeapon, ItemStack.EMPTY);

        world.modifyEntityMovement(victim, false);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_parry_efn_second.text_1", 80, 5, 1, 5);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> parryCallback = createYamatoSmartParryCallback(
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1.get(),
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2.get()
        );

        world.playAnimation(attacker, EFNScytheAnimations.SCYTHE_HARVEST, 0.0F, parryCallback, beHit -> {});

        // 3. 延迟 12 tick 的极限反应时间
        builder.idle(12);

        world.playAnimation(victim, EFNSwordAnimations.NF_SWORD_GUARD, 0.0F);

        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

    public static Consumer<PonderCombatEvent.Hit> createYamatoSmartParryCallback(StaticAnimation anim1, StaticAnimation anim2) {
        return new Consumer<>() {
            private boolean toggle = false;

            @Override
            public void accept(PonderCombatEvent.Hit hit) {
                StaticAnimation currentAnim = toggle ? anim2 : anim1;toggle = !toggle;

                createEFNParryCallback(currentAnim, false).accept(hit);
            }
        };
    }

    public static Consumer<PonderCombatEvent.Hit> createEFNParryCallback(StaticAnimation parryAnim, boolean isHeavyImpact) {
        return hitEvent -> {
            Entity attacker = hitEvent.getAttacker();
            Entity victim = hitEvent.getTarget();

            hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_PARRIED);

            playSoundClientSide(EFNSounds.PARRY.get(), isHeavyImpact ? 0.8F : 1.0F, 1.0F);

            Vec3 lookVec = victim.getLookAngle();
            Vec3 playerPos = victim.position().add(0, victim.getBbHeight() * 0.6, 0);
            Vec3 targetPos = attacker.position().add(0, attacker.getBbHeight() * 0.6, 0);
            Vec3 midPos = playerPos.add(targetPos.subtract(playerPos).scale(0.5));

            Vector3d particlePos = new Vector3d(midPos.x, midPos.y, midPos.z);
            Vector3d particleArgs = new Vector3d(1.0, 0.0, 0.0);

            if (parryAnim == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1.get()) {
                Vec3 leftOffset = new Vec3(lookVec.z, 0, -lookVec.x).scale(0.2);
                particlePos.add(leftOffset.x, 0, leftOffset.z);
                particleArgs = new Vector3d(1.0, -0.6, 0.0);
            } else if (parryAnim == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2.get()) {
                Vec3 rightOffset = new Vec3(-lookVec.z, 0, lookVec.x).scale(0.2);
                particlePos.add(rightOffset.x, 0, rightOffset.z);
                particleArgs = new Vector3d(1.0, 0.6, 0.0);
            } else if (parryAnim == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3.get()) {
                particleArgs = new Vector3d(1.2, 0.7, 0.0);
                playSoundClientSide(EpicFightSounds.CLASH.get(), 0.7F, 1.0F);
            }

            final Vector3d finalParticlePos = particlePos;
            final Vector3d finalParticleArgs = particleArgs;

            spawnEfmHitParticleClientSide(
                    victim.level(),
                    EFNParticles.EFN_PARRY_FLASH_MAIN.get(),
                    attacker, victim,
                    (t, a) -> finalParticlePos,
                    (t, a) -> finalParticleArgs
            );
            spawnEfmHitParticleClientSide(
                    victim.level(),
                    EFNParticles.ALL_SPARK.get(),
                    attacker, victim,
                    (t, a) -> finalParticlePos,
                    (t, a) -> finalParticleArgs
            );

            LivingEntityPatch<?> rawVictimPatch = EpicFightCapabilities.getEntityPatch(victim, LivingEntityPatch.class);
            if (rawVictimPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {
                victimPatch.getClientAnimator().playAnimation(parryAnim.getRealAnimation(), 0.0F);
            }

            if (isHeavyImpact) {
                if (rawVictimPatch != null) {
                    rawVictimPatch.getOriginal().getPersistentData().putBoolean(CAN_MOVE, true);
                }
            }
        };
    }

    public static void playInteractiveComboWithDefensiveSetup(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker, ElementLink<EntityElement> victim,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            int attacksToPlay, StaticAnimation defenseSetupAnim,
            Consumer<PonderCombatEvent.Hit> normalCallback, Consumer<PonderCombatEvent.Hit> lastCallback,
            String prepareHitTextKey, double prepareHitTextX, double prepareHitTextY, double prepareHitTextZ,
            String firstHitTextKey, double firstHitTextX, double firstHitTextY, double firstHitTextZ) {

        if (comboMotions == null || comboMotions.isEmpty()) return;
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        int max = Math.min(attacksToPlay, comboMotions.size());

        for (int i = 0; i < max; i++) {
            AnimationManager.AnimationAccessor<? extends AttackAnimation> currentAttack = comboMotions.get(i);
            boolean isFirst = (i == 0);
            boolean isLast = (i == max - 1);
            Consumer<PonderCombatEvent.Hit> activeCallback = isLast ? lastCallback : normalCallback;

            if (isFirst) {
                world.playAnimation(attacker, currentAttack, 0.0F, activeCallback, beHit -> {});

                builder.idle(1);

                if (defenseSetupAnim != null) {
                    world.playAnimation(victim, defenseSetupAnim.getRealAnimation(), 0.0F);
                    if (prepareHitTextKey != null && !prepareHitTextKey.isEmpty()) {
                        showText(builder, util, prepareHitTextKey, 30, (int) prepareHitTextX, (int) prepareHitTextY, (int) prepareHitTextZ);
                    }
                }

                builder.idle(1);

                // 4. 起手防御后立刻进入全局 0.1 倍时缓
                world.modifyEntityPlaySpeed(attacker, 0.05F);
                world.modifyEntityPlaySpeed(victim, 0.05F);
                builder.idle(25);

                // 5. 恢复正常速度
                world.modifyEntityPlaySpeed(attacker, 1.0F);
                world.modifyEntityPlaySpeed(victim, 1.0F);

                // 6. 延迟 3 tick
                builder.idle(3);

                world.modifyEntityPlaySpeed(attacker, 0.08F);
                world.modifyEntityPlaySpeed(victim, 0.08F);

                // 弹出第二个文本 (招架成功提示)
                if (firstHitTextKey != null && !firstHitTextKey.isEmpty()) {
                    showText(builder, util, firstHitTextKey, 70, (int) firstHitTextX, (int) firstHitTextY, (int) firstHitTextZ);
                }

                builder.idle(30);

                world.modifyEntityPlaySpeed(attacker, 1.0F);
                world.modifyEntityPlaySpeed(victim, 1.0F);

                world.waitForCanBasicAttack(attacker);
            } else {
                // 后续连段的处理保持不变
                if (defenseSetupAnim != null) {
                    world.playAnimation(victim, defenseSetupAnim.getRealAnimation(), 0.0F);
                }

                builder.idle(2);

                world.playAnimation(attacker, currentAttack, 0.0F, activeCallback, beHit -> {});

                if (!isLast) world.waitForCanBasicAttack(attacker);
                else world.waitForInaction(attacker);
            }
        }

        builder.idle(10);
        builder.addInstruction(scene -> {
            EntityElement resolvedVictim = scene.resolve(victim);
            if (resolvedVictim != null) {
                resolvedVictim.ifPresent(v -> {
                    DummyEntityPatch<?> victimPatch = EpicFightCapabilities.getEntityPatch(v, DummyEntityPatch.class);
                    if (victimPatch != null) {
                        victimPatch.updateLivingMotionsForPonder();
                    }
                });
            }
        });
    }
}