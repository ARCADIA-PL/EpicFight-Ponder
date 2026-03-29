package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.PonderScene;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.com.efp.api.event.PonderCombatEvent;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.client.particle.PonderEntityAfterimageParticle;
import org.com.efp.entity.DummyEntityPatch;
import org.com.efp.mixin.epicfight.WeaponCapabilityAccessor;
import org.com.efp.particle.EFPParticles;
import org.com.efp.registry.EFPEntities;
import org.joml.Vector3d;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class EFPSceneUtils {

    /**
     * 1. 快速搭建正方形标准场地
     */
    public static void setupStandardScene(EpicFightSceneBuilder builder, int size, String sceneId, String title) {
        builder.title(sceneId, title);
        builder.configureBasePlate(0, 0, size);
        builder.showBasePlate();
        builder.scaleSceneView(1.0F);
        builder.idle(5);
    }

    public static void setupStandardLongScene(EpicFightSceneBuilder builder, String sceneId, String title) {
        builder.title(sceneId, title);
        builder.configureBasePlate(-5, 0, 15);
        builder.showBasePlate();
        builder.scaleSceneView(0.7F);
        builder.idle(5);
    }

    /**
     * 2. 快速生成一个假人演员
     */
    public static ElementLink<EntityElement> spawnDummyActor(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem, ItemStack offHandItem, Style forcedStyle) {
        return builder.world().createEntity(level -> {
            LivingEntity actor = EFPEntities.DUMMY_PLAYER.get().create(level);
            if (actor != null) {
                actor.setPos(x, y, z);
                actor.setYRot(yRot);
                actor.yBodyRot = yRot;
                actor.yHeadRot = yRot;

                if (mainHandItem != null && !mainHandItem.isEmpty()) {
                    actor.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
                }
                if (offHandItem != null && !offHandItem.isEmpty()) {
                    actor.setItemInHand(InteractionHand.OFF_HAND, offHandItem);
                }

                EpicFightCapabilities.getUnparameterizedEntityPatch(actor, DummyEntityPatch.class).ifPresent(patch -> {
                    patch.setYRot(yRot);
                    patch.setYRotO(yRot);
                    if (forcedStyle != null) {
                        patch.setForcedStyle(forcedStyle);
                    }
                    patch.updateLivingMotionsForPonder();
                });
            }
            return actor;
        });
    }

    public static ElementLink<EntityElement> spawnDummyActor(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem, ItemStack offHandItem) {
        return spawnDummyActor(builder, x, y, z, yRot, mainHandItem, offHandItem, null);
    }

    public static ElementLink<EntityElement> spawnDummyActor(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem) {
        return spawnDummyActor(builder, x, y, z, yRot, mainHandItem, ItemStack.EMPTY, null);
    }

    public static ElementLink<EntityElement> spawnDummyVictim(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem, ItemStack offHandItem, Style forcedStyle) {
        return builder.world().createEntity(level -> {
            LivingEntity victim = EFPEntities.DUMMY_VICTIM_PLAYER.get().create(level);
            if (victim != null) {
                victim.setPos(x, y, z);
                victim.setYRot(yRot);
                victim.yBodyRot = yRot;
                victim.yHeadRot = yRot;

                if (mainHandItem != null && !mainHandItem.isEmpty()) {
                    victim.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
                }
                if (offHandItem != null && !offHandItem.isEmpty()) {
                    victim.setItemInHand(InteractionHand.OFF_HAND, offHandItem);
                }

                EpicFightCapabilities.getUnparameterizedEntityPatch(victim, DummyEntityPatch.class).ifPresent(patch -> {
                    patch.setYRot(yRot);
                    patch.setYRotO(yRot);
                    if (forcedStyle != null) {
                        patch.setForcedStyle(forcedStyle);
                    }
                    patch.updateLivingMotionsForPonder();
                });
            }
            return victim;
        });
    }

    public static ElementLink<EntityElement> spawnDummyVictim(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem, ItemStack offHandItem) {
        return spawnDummyVictim(builder, x, y, z, yRot, mainHandItem, offHandItem, null);
    }

    public static ElementLink<EntityElement> spawnDummyVictim(EpicFightSceneBuilder builder, double x, double y, double z, float yRot, ItemStack mainHandItem) {
        return spawnDummyVictim(builder, x, y, z, yRot, mainHandItem, ItemStack.EMPTY, null);
    }

    /**
     * [基础] 在目标上方显示普通文本
     * @param duration 持续时间(tick)
     */
    public static void showText(EpicFightSceneBuilder builder, SceneBuildingUtil util, String textKey, int duration, int x, int y, int z) {
        builder.overlay().showText(duration)
                .text(textKey)
                .pointAt(util.vector().centerOf(x, y, z))
                .placeNearTarget();
    }

    public static void showTextWithKeyFrame(EpicFightSceneBuilder builder, SceneBuildingUtil util, String textKey, int duration, int x, int y, int z) {
        builder.overlay().showText(duration)
                .text(textKey)
                .pointAt(util.vector().centerOf(x, y, z))
                .attachKeyFrame()
                .placeNearTarget();
    }

    /**
     * 专用于更新武器 NBT (如打刀的收鞘状态)
     */
    public static void updateSheathState(EpicFightSceneBuilder builder, ElementLink<EntityElement> attacker, int state) {
        builder.world().modifyEntity(attacker, entity -> {
            if (entity instanceof LivingEntity living) {
                ItemStack stack = living.getMainHandItem();
                if (!stack.isEmpty()) {
                    stack.getOrCreateTag().putInt("sheath", state);
                }
            }
        });
    }

    public static <A extends StaticAnimation> void playDerivationWithSlowMo(
            EpicFightSceneBuilder builder, ElementLink<EntityElement> actor,
            WaitType waitType, AnimationManager.AnimationAccessor<A> nextAnim,
            float slowMoSpeed, int slowMoTicks) {

        switch (waitType) {
            case CAN_BASIC_ATTACK -> builder.world().waitForCanBasicAttack(actor);
            case CAN_USE_SKILL -> builder.world().waitForCanUseSkill(actor);
            case INACTION -> builder.world().waitForInaction(actor);
        }

        builder.world().modifyEntityPlaySpeed(actor, slowMoSpeed);
        builder.idle(slowMoTicks);

        builder.world().modifyEntityPlaySpeed(actor, 1.0F);
        builder.world().playAnimation(actor, nextAnim, 0.0F);
    }

    /**
     * 安全获取武器连段数据
     */
    public static List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> getSafeComboMotions(ItemStack weapon, Style style) {
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(weapon);
        if (cap instanceof WeaponCapability weaponCap) {
            Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> motionsMap = ((WeaponCapabilityAccessor) weaponCap).getAutoAttackMotions();
            if (motionsMap.containsKey(style) && !motionsMap.get(style).isEmpty()) return motionsMap.get(style);
            if (motionsMap.containsKey(CapabilityItem.Styles.TWO_HAND))
                return motionsMap.get(CapabilityItem.Styles.TWO_HAND);
            if (motionsMap.containsKey(CapabilityItem.Styles.ONE_HAND))
                return motionsMap.get(CapabilityItem.Styles.ONE_HAND);
            return motionsMap.getOrDefault(CapabilityItem.Styles.COMMON, CapabilityItem.getBasicAutoAttackMotion());
        }
        return CapabilityItem.getBasicAutoAttackMotion() != null ? CapabilityItem.getBasicAutoAttackMotion() : new ArrayList<>();
    }

    /**
     * 通用双人交互攻击连段展示
     */
    public static void playInteractiveCombo(
            EpicFightSceneBuilder builder,
            ElementLink<EntityElement> attacker,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            int attacksToPlay,
            Consumer<PonderCombatEvent.Hit> normalCallback,
            Consumer<PonderCombatEvent.Hit> lastCallback) {

        if (comboMotions == null || comboMotions.isEmpty()) return;
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        int max = Math.min(attacksToPlay, comboMotions.size());

        for (int i = 0; i < max; i++) {
            AnimationManager.AnimationAccessor<? extends AttackAnimation> currentAttack = comboMotions.get(i);
            boolean isLastAttack = (i == max - 1);

            Consumer<PonderCombatEvent.Hit> activeCallback = isLastAttack ? lastCallback : normalCallback;

            world.playAnimation(attacker, currentAttack, 0.0F, activeCallback, beHitEvent -> {
            });

            if (!isLastAttack) {
                world.waitForCanBasicAttack(attacker);
            } else {
                world.waitForInaction(attacker);
            }
        }
    }

    /**
     * 通用双人交互单次攻击展示 (带时缓与回调)
     */
    public static void playInteractiveStrike(
            EpicFightSceneBuilder builder,
            ElementLink<EntityElement> attacker,
            AnimationManager.AnimationAccessor<? extends AttackAnimation> strikeMotion,
            float playSpeed,
            Consumer<PonderCombatEvent.Hit> onHitCallback) {

        builder.world().modifyEntityPlaySpeed(attacker, playSpeed);
        builder.world().playAnimation(attacker, strikeMotion, 0.0F, onHitCallback, beHitEvent -> {
        });
    }

    /**
     * 电影级闪避展示：支持“慢-快-极慢”的非线性时间曲线（顿帧与子弹时间）
     */
    public static void playCinematicDodgeStrike(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker, ElementLink<EntityElement> victim,
            AnimationManager.AnimationAccessor<? extends AttackAnimation> strikeMotion,
            StaticAnimation dodgeAnim,
            int startupSlowTicks, int fastSnapTicks, int bulletTimeTicks,
            @Nullable String textKey1, @Nullable String textKey2,
            double textX, double textY, double textZ) {

        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        Consumer<PonderCombatEvent.Hit> dodgeCallback = createStandardDodgeCallback(dodgeAnim);

        world.playAnimation(attacker, strikeMotion, 0.0F, dodgeCallback, beHit -> {});

        world.modifyEntityPlaySpeed(attacker, 0.35F);
        world.modifyEntityPlaySpeed(victim, 0.35F);

        if (textKey1 != null && !textKey1.isEmpty()) {
            showText(builder, util, textKey1, (int)(startupSlowTicks * 0.8), (int)textX, (int)textY, (int)textZ);
        }

        builder.idle(startupSlowTicks);

        world.modifyEntityPlaySpeed(attacker, 1.2F);
        world.modifyEntityPlaySpeed(victim, 1.2F);

        builder.idle(fastSnapTicks);

        world.modifyEntityPlaySpeed(attacker, 0.1F);
        world.modifyEntityPlaySpeed(victim, 0.1F);

        if (textKey2 != null && !textKey2.isEmpty()) {
            showText(builder, util, textKey2, bulletTimeTicks, (int)textX, (int)textY, (int)textZ);
        }

        builder.idle(bulletTimeTicks);

        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.modifyEntityPlaySpeed(victim, 1.0F);

        world.waitForInaction(attacker);
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
                world.modifyEntityPlaySpeed(attacker, 0.3F);
                world.modifyEntityPlaySpeed(victim, 0.3F);

                world.playAnimation(attacker, currentAttack, 0.0F, activeCallback, beHit -> {
                });

                builder.idle(15);

                if (defenseSetupAnim != null) {
                    world.playAnimation(victim, defenseSetupAnim.getRealAnimation(), 0.1F);
                    if (prepareHitTextKey != null && !prepareHitTextKey.isEmpty()) {
                        showText(builder, util, prepareHitTextKey, 60, (int) prepareHitTextX, (int) prepareHitTextY, (int) prepareHitTextZ);
                    }
                }

                builder.idle(8);

                world.modifyEntityPlaySpeed(attacker, 0.05F);
                world.modifyEntityPlaySpeed(victim, 0.05F);

                if (firstHitTextKey != null && !firstHitTextKey.isEmpty()) {
                    showText(builder, util, firstHitTextKey, 100, (int) firstHitTextX, (int) firstHitTextY, (int) firstHitTextZ);
                }

                builder.idle(20);

                world.modifyEntityPlaySpeed(attacker, 1.0F);
                world.modifyEntityPlaySpeed(victim, 1.0F);

                world.waitForCanBasicAttack(attacker);
            } else {
                world.playAnimation(attacker, currentAttack, 0.0F, activeCallback, beHit -> {
                });

                builder.idle(10);

                if (defenseSetupAnim != null) {
                    world.playAnimation(victim, defenseSetupAnim.getRealAnimation(), 0.1F);
                }

                if (!isLast) world.waitForCanBasicAttack(attacker);
                else world.waitForInaction(attacker);
            }
        }
    }

    /**
     * 创建“成功闪避”事件
     */
    public static Consumer<PonderCombatEvent.Hit> createStandardDodgeCallback(StaticAnimation dodgeAnim) {
        return hitEvent -> {
            hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_DODGED);

            playSoundClientSide(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);

            LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(hitEvent.getTarget(), LivingEntityPatch.class);
            if (rawPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {

                victimPatch.getClientAnimator().playAnimation(dodgeAnim.getRealAnimation(), 0.0F);

                spawnAfterImage(hitEvent.getTarget());
            }
        };
    }

    /**
     * 创建“成功格挡”事件 (支持 returnAnim 传 null，代表不再回正)
     */
    public static Consumer<PonderCombatEvent.Hit> createStandardGuardCallback(StaticAnimation hitAnim, @Nullable StaticAnimation returnAnim) {
        return hitEvent -> {
            hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_BLOCKED);
            playSoundClientSide(EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
            spawnEfmHitParticleClientSide(
                    hitEvent.getTarget().level(), EpicFightParticles.HIT_BLUNT.get(),
                    hitEvent.getAttacker(), hitEvent.getTarget(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO
            );

            LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(hitEvent.getTarget(), LivingEntityPatch.class);
            if (rawPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {
                victimPatch.getClientAnimator().playAnimation(hitAnim.getRealAnimation(), 0.0F);

                if (returnAnim != null) {
                    int delay = (int) (Objects.requireNonNull(victimPatch.getClientAnimator().getPlayerFor(hitAnim.getRealAnimation())).getAnimation().get().getTotalTime() / 20);
                    victimPatch.scheduleDelayedTask(delay, () -> {
                        victimPatch.getClientAnimator().playAnimation(returnAnim.getRealAnimation(), 0.3F);
                    });
                }
            }
        };
    }

    /**
     * 创建“格挡破防”事件
     */
    public static Consumer<PonderCombatEvent.Hit> createGuardBreakCallback(StaticAnimation breakAnim) {
        return hitEvent -> {
            hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_BLOCKED);
            playSoundClientSide(EpicFightSounds.NEUTRALIZE_MOBS.get(), 1.0F, 1F);
            spawnEfmHitParticleClientSide(
                    hitEvent.getTarget().level(), EpicFightParticles.HIT_BLUNT.get(),
                    hitEvent.getTarget(), hitEvent.getAttacker(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO
            );

            LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(hitEvent.getTarget(), LivingEntityPatch.class);
            if (rawPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {
                victimPatch.getClientAnimator().playAnimation(breakAnim.getRealAnimation(), 0.0F);
            }
            hitEvent.getAttacker().getPersistentData().putFloat(EpicFightSceneBuilder.PLAY_SPEED, 1.0F);
        };
    }

    /**
     * 创建“成功招架 (Parry)”事件
     */
    public static Consumer<PonderCombatEvent.Hit> createCustomParryCallback(Function<PonderCombatEvent.Hit, StaticAnimation> animSelector) {
        return hitEvent -> {
            hitEvent.setResult(PonderCombatEvent.AttackResult.FAIL_PARRIED);
            playSoundClientSide(EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
            spawnEfmHitParticleClientSide(
                    hitEvent.getTarget().level(), EpicFightParticles.HIT_BLUNT.get(),
                    hitEvent.getAttacker(), hitEvent.getTarget(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO
            );

            LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(hitEvent.getTarget(), LivingEntityPatch.class);
            if (rawPatch instanceof DummyEntityPatch<?> victimPatch && victimPatch.getClientAnimator() != null) {
                StaticAnimation chosenAnim = animSelector.apply(hitEvent);
                if (chosenAnim != null) {
                    victimPatch.getClientAnimator().playAnimation(chosenAnim.getRealAnimation(), 0.0F);
                }
            }
        };
    }

    /**
     * 创建“成功招架 (Parry)”事件 - [随机选择动画]
     */
    public static Consumer<PonderCombatEvent.Hit> createParryCallback(StaticAnimation... parryAnims) {
        if (parryAnims == null || parryAnims.length == 0) {
            throw new IllegalArgumentException("Parry animations list cannot be empty!");
        }
        final Random random = new Random();
        return createCustomParryCallback(hitEvent -> parryAnims[random.nextInt(parryAnims.length)]);
    }

    /**
     * 创建“成功招架 (Parry)”事件 - [循环交替]
     */
    public static Consumer<PonderCombatEvent.Hit> createCyclicParryCallback(StaticAnimation... parryAnims) {
        if (parryAnims == null || parryAnims.length == 0) {
            throw new IllegalArgumentException("Parry animations list cannot be empty!");
        }

        final int[] currentIndex = {new Random().nextInt(parryAnims.length)};

        return createCustomParryCallback(hitEvent -> {
            StaticAnimation chosenAnim = parryAnims[currentIndex[0]];

            currentIndex[0] = (currentIndex[0] + 1) % parryAnims.length;

            return chosenAnim;
        });
    }

    /**
     * 5. 全自动播放标准武器连段
     */
    public static void playStandardCombo(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            double centerX, double centerY, double centerZ,
            String dashTextKey, String jumpTextKey, boolean enableDash, boolean enableJump) {

        if (comboMotions == null || comboMotions.isEmpty()) {
            builder.idle(20);
            return;
        }

        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        int originalSize = comboMotions.size();
        int dashIndex = originalSize - 2;
        int jumpIndex = originalSize - 1;

        for (int i = 0; i < originalSize; i++) {
            if (i == dashIndex && !enableDash) continue;
            if (i == jumpIndex && !enableJump) continue;

            if (i == dashIndex) {
                world.simulateSpring(attacker, 1.5F, 10);
                builder.idle(10);
                if (dashTextKey != null && !dashTextKey.isEmpty()) {
                    showText(builder, util, dashTextKey, 30, (int) centerX, (int) centerY - 1, (int) centerZ);
                }
            } else if (i == jumpIndex) {
                world.setPosition(attacker, centerX, centerY, centerZ);
                builder.idle(5);
                world.simulateJump(attacker);
                builder.idle(8);
                if (jumpTextKey != null && !jumpTextKey.isEmpty()) {
                    showText(builder, util, jumpTextKey, 40, (int) centerX, (int) centerY + 1, (int) centerZ);
                }
            }

            world.playAnimation(attacker, comboMotions.get(i), 0.0F);

            boolean isBasicAttack = i < dashIndex;
            boolean isLastBasicAttack = i == (dashIndex - 1);

            if (isBasicAttack && !isLastBasicAttack) {
                world.waitForCanBasicAttack(attacker);
            } else {
                world.waitForInaction(attacker);
            }
        }
    }

    /**
     * EFX打刀专用
     */
    public static void playUchigatanaStandardCombo(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            double centerX, double centerY, double centerZ,
            String dashTextKey, String jumpTextKey, boolean enableDash, boolean enableJump) {

        if (comboMotions == null || comboMotions.isEmpty()) {
            builder.idle(20);
            return;
        }

        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        int originalSize = comboMotions.size();
        int dashIndex = originalSize - 2;
        int jumpIndex = originalSize - 1;

        updateSheathState(builder, attacker, 0);

        for (int i = 0; i < originalSize; i++) {
            if (i == dashIndex && !enableDash) continue;
            if (i == jumpIndex && !enableJump) continue;

            if (i == dashIndex) {
                updateSheathState(builder, attacker, 1);
                world.simulateSpring(attacker, 1.5F, 10);
                builder.idle(10);
                if (dashTextKey != null && !dashTextKey.isEmpty()) {
                    showText(builder, util, dashTextKey, 30, (int) centerX, (int) centerY - 1, (int) centerZ);
                }
            } else if (i == jumpIndex) {
                updateSheathState(builder, attacker, 1);
                world.setPosition(attacker, centerX, centerY, centerZ);
                builder.idle(5);
                world.simulateJump(attacker);
                builder.idle(8);
                if (jumpTextKey != null && !jumpTextKey.isEmpty()) {
                    showText(builder, util, jumpTextKey, 40, (int) centerX, (int) centerY + 1, (int) centerZ);
                }
            }

            updateSheathState(builder, attacker, 0);
            world.playAnimation(attacker, comboMotions.get(i), 0.0F);

            boolean isBasicAttack = i < dashIndex;
            boolean isLastBasicAttack = i == (dashIndex - 1);

            if (isBasicAttack && !isLastBasicAttack) {
                world.waitForCanBasicAttack(attacker);
            } else {
                world.waitForInaction(attacker);
            }
        }

        updateSheathState(builder, attacker, 1);
    }

    public static void showcaseStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle, boolean enableDash, boolean enableJump) {

        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        double center = size / 2.0D;
        double centerY = 1.0;

        setupStandardScene(builder, size, sceneId, "epic_fight_ponder.ponder." + sceneId + ".title");

        ElementLink<EntityElement> attacker = spawnDummyActor(builder, center, centerY, center, 180, mainHandItem, offHandItem, showcaseStyle);
        builder.idle(10);

        showText(builder, util, "epic_fight_ponder.ponder." + sceneId + ".text_1", 40, (int) center, (int) centerY + 1, (int) center);
        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = getSafeComboMotions(mainHandItem, showcaseStyle);

        String dashText = "epic_fight_ponder.ponder." + sceneId + ".text_2";
        String jumpText = "epic_fight_ponder.ponder." + sceneId + ".text_3";

        playStandardCombo(builder, util, attacker, comboMotions, center, centerY, center, dashText, jumpText, enableDash, enableJump);

        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle) {
        showcaseStandardWeaponCombo(baseScene, util, size, sceneId, mainHandItem, offHandItem, showcaseStyle, true, true);
    }

    public static void showcaseStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack weapon, Style showcaseStyle) {
        showcaseStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, showcaseStyle, true, true);
    }

    public static void showcaseStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack weapon) {
        showcaseStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND, true, true);
    }

    public static void showcaseUchigatanaStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle, boolean enableDash, boolean enableJump) {

        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        double center = size / 2.0D;
        double centerY = 1.0;

        setupStandardScene(builder, size, sceneId, "epic_fight_ponder.ponder." + sceneId + ".title");

        ElementLink<EntityElement> attacker = spawnDummyActor(builder, center, centerY, center, 180, mainHandItem, offHandItem, showcaseStyle);
        updateSheathState(builder, attacker, 1);
        builder.idle(10);

        showText(builder, util, "epic_fight_ponder.ponder." + sceneId + ".text_1", 40, (int) center, (int) centerY + 1, (int) center);
        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = getSafeComboMotions(mainHandItem, showcaseStyle);

        String dashText = "epic_fight_ponder.ponder." + sceneId + ".text_2";
        String jumpText = "epic_fight_ponder.ponder." + sceneId + ".text_3";

        playUchigatanaStandardCombo(builder, util, attacker, comboMotions, center, centerY, center, dashText, jumpText, enableDash, enableJump);

        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseUchigatanaStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle) {
        showcaseUchigatanaStandardWeaponCombo(baseScene, util, size, sceneId, mainHandItem, offHandItem, showcaseStyle, true, true);
    }

    public static void showcaseUchigatanaStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack weapon, Style showcaseStyle) {
        showcaseUchigatanaStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, showcaseStyle, true, true);
    }

    public static void showcaseUchigatanaStandardWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util, int size, String sceneId, ItemStack weapon) {
        showcaseUchigatanaStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND, true, true);
    }

    public static void changeStyleAndRefreshMotions(
            EpicFightSceneBuilder builder,
            ElementLink<EntityElement> entityLink,
            Style newStyle) {

        builder.world().modifyEntity(entityLink, entity -> {
            if (entity instanceof LivingEntity living) {
                EpicFightCapabilities.getUnparameterizedEntityPatch(living, DummyEntityPatch.class).ifPresent(patch -> {

                    patch.setForcedStyle(newStyle);

                    patch.updateLivingMotionsForPonder();

                    if (patch.getClientAnimator() != null) {
                        AssetAccessor<? extends StaticAnimation> newLiving = patch.getClientAnimator().getLivingAnimation(patch.getCurrentLivingMotion(), null);
                        if (newLiving != null) {
                            patch.getClientAnimator().playAnimation(newLiving, 0.2F);
                        }
                    }
                });
            }
        });
    }

    @Nullable
    public static Entity resolveEntity(SceneBuilder sceneBuilder, ElementLink<EntityElement> link) {
        PonderScene scene = sceneBuilder.getScene();
        EntityElement element = scene.resolve(link);
        if (element != null) {
            final Entity[] extractedEntity = new Entity[1];
            element.ifPresent(entity -> {
                extractedEntity[0] = entity;
            });
            return extractedEntity[0];
        }
        return null;
    }

    public static void playSoundClientSide(SoundEvent sound, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    public static void spawnAfterImage(Entity entity) {
        if (entity == null) {
            return;
        } else {
            entity.level();
        }

        LivingEntityPatch<?> rawPatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
        if (rawPatch instanceof DummyEntityPatch<?> dummyPatch && dummyPatch.getClientAnimator() != null) {

            EntitySnapshot<?> snapshot = new EntitySnapshot<>(dummyPatch);

            int cacheId = PonderEntityAfterimageParticle.cacheSnapshot(snapshot);

            entity.level().addParticle(
                    EFPParticles.PONDER_AFTERIMAGE.get(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    cacheId, 0.0, 0.0
            );
        }
    }

    public static void playStepSoundOnTimeline(SceneBuilder baseScene, ElementLink<EntityElement> entityLink) {
        baseScene.addInstruction(scene -> {
            Entity entity = resolveEntity(scene.builder(), entityLink);
            BlockState state;
            if (entity != null) {
                state = scene.getWorld().getBlockState(entity.getOnPos());
                playSoundClientSide(state.getSoundType().getHitSound(), 1.0F, 0.8F);
            }
        });
    }

    public static void playSoundOnTimeline(SceneBuilder baseScene, ElementLink<EntityElement> entityLink, SoundEvent soundEvent) {
        baseScene.addInstruction(scene -> {
            Entity entity = resolveEntity(scene.builder(), entityLink);
            if (entity != null) {
                playSoundClientSide(soundEvent, 1.0F, 0.8F);
            }
        });
    }

    public static void addAfterImageOnTimeline(SceneBuilder baseScene, ElementLink<EntityElement> entityLink) {
        baseScene.addInstruction(scene -> {
            Entity entity = resolveEntity(scene.builder(), entityLink);
            if (entity != null) {
                spawnAfterImage(entity);
            }
        });
    }

    public static void spawnEfmHitParticleClientSide(
            Level level, HitParticleType particle, Entity target, Entity attacker,
            BiFunction<Entity, Entity, Vector3d> posFunc, BiFunction<Entity, Entity, Vector3d> argFunc) {

        Vector3d pos = posFunc.apply(target, attacker);
        Vector3d args = argFunc.apply(target, attacker);
        level.addParticle(particle, pos.x, pos.y, pos.z, args.x, args.y, args.z);
    }

    public enum WaitType {CAN_BASIC_ATTACK, CAN_USE_SKILL, INACTION}
}