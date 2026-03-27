package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.entity.DummyEntityPatch;
import org.com.efp.mixin.epicfight.WeaponCapabilityAccessor;
import org.com.efp.registry.EFPEntities;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.List;
import java.util.Map;

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

    /**
     * 3. 显示浮空文本
     */
    public static void showTextAtTop(EpicFightSceneBuilder builder, SceneBuildingUtil util, String textKey, int duration, int x, int y, int z) {
        builder.overlay().showText(duration)
                .text(textKey)
                .pointAt(util.vector().topOf(x, y, z))
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

    /**
     * 4. 动作派生的时间过渡 (时间缓动系统)
     */
    public enum WaitType { CAN_BASIC_ATTACK, CAN_USE_SKILL, INACTION }

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
     * 5. 全自动播放标准武器连段 (分离 Dash 和 Jump 开关)
     */
    public static void playStandardCombo(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            double centerX, double centerY, double centerZ,
            String dashTextKey, String jumpTextKey, boolean enableDash, boolean enableJump) {

        if (comboMotions == null || comboMotions.isEmpty()) return;

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
                    showTextAtTop(builder, util, dashTextKey, 30, (int)centerX, (int)centerY - 1, (int)centerZ);
                }
            } else if (i == jumpIndex) {
                world.setPosition(attacker, centerX, centerY, centerZ);
                builder.idle(5);
                world.simulateJump(attacker);
                builder.idle(8);
                if (jumpTextKey != null && !jumpTextKey.isEmpty()) {
                    showTextAtTop(builder, util, jumpTextKey, 40, (int)centerX, (int)centerY + 1, (int)centerZ);
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

        if (comboMotions == null || comboMotions.isEmpty()) return;

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
                    showTextAtTop(builder, util, dashTextKey, 30, (int)centerX, (int)centerY - 1, (int)centerZ);
                }
            } else if (i == jumpIndex) {
                updateSheathState(builder, attacker, 1);
                world.setPosition(attacker, centerX, centerY, centerZ);
                builder.idle(5);
                world.simulateJump(attacker);
                builder.idle(8);
                if (jumpTextKey != null && !jumpTextKey.isEmpty()) {
                    showTextAtTop(builder, util, jumpTextKey, 40, (int)centerX, (int)centerY + 1, (int)centerZ);
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

    /**
     * 标准武器基础连段展示
     */
    public static void showcaseStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle, boolean enableDash, boolean enableJump) {

        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        double center = size / 2.0D;
        double centerY = 1.0;

        setupStandardScene(builder, size, sceneId, "epic_fight_ponder.ponder." + sceneId + ".title");

        ElementLink<EntityElement> attacker = spawnDummyActor(builder, center, centerY, center, 180, mainHandItem, offHandItem, showcaseStyle);
        builder.idle(10);

        showTextAtTop(builder, util, "epic_fight_ponder.ponder." + sceneId + ".text_1", 40, (int) center, (int)centerY + 1, (int) center);
        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = null;
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(mainHandItem);

        if (cap instanceof WeaponCapability weaponCap) {
            Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> motionsMap = ((WeaponCapabilityAccessor) weaponCap).getAutoAttackMotions();
            comboMotions = motionsMap.getOrDefault(showcaseStyle, motionsMap.get(CapabilityItem.Styles.COMMON));
        } else {
            comboMotions = CapabilityItem.getBasicAutoAttackMotion();
        }

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
        builder.idle(10);

        showTextAtTop(builder, util, "epic_fight_ponder.ponder." + sceneId + ".text_1", 40, (int) center, (int)centerY + 1, (int) center);
        builder.idle(20);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = null;
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(mainHandItem);

        if (cap instanceof WeaponCapability weaponCap) {
            Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> motionsMap = ((WeaponCapabilityAccessor) weaponCap).getAutoAttackMotions();
            comboMotions = motionsMap.getOrDefault(showcaseStyle, motionsMap.get(CapabilityItem.Styles.COMMON));
        } else {
            comboMotions = CapabilityItem.getBasicAutoAttackMotion();
        }

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
}