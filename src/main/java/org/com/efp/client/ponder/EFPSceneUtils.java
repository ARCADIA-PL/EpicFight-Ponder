package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.entity.DummyPlayerEntityPatch;
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
        builder.scaleSceneView(1F);
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

                EpicFightCapabilities.getUnparameterizedEntityPatch(actor, DummyPlayerEntityPatch.class).ifPresent(patch -> {
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
     * 4. 动作派生的时间过渡
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
     * 5. 全自动播放标准武器连段
     */
    public static void playStandardComboWithDashAndJump(
            EpicFightSceneBuilder builder, SceneBuildingUtil util,
            ElementLink<EntityElement> attacker,
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions,
            double centerX, double centerY, double centerZ,
            String dashTextKey, String jumpTextKey) {

        if (comboMotions == null || comboMotions.isEmpty()) return;

        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();
        int size = comboMotions.size();

        for (int i = 0; i < size; i++) {
            if (i == size - 2) {
                world.simulateSpring(attacker, 1.5F, 10);
                builder.idle(10);
                if (dashTextKey != null && !dashTextKey.isEmpty()) {
                    showTextAtTop(builder, util, dashTextKey, 30, (int)centerX, (int)centerY - 1, (int)centerZ);
                }
            }
            else if (i == size - 1) {
                world.setPosition(attacker, centerX, centerY, centerZ);
                builder.idle(5);
                world.simulateJump(attacker);
                builder.idle(8);
                if (jumpTextKey != null && !jumpTextKey.isEmpty()) {
                    showTextAtTop(builder, util, jumpTextKey, 40, (int)centerX, (int)centerY + 1, (int)centerZ);
                }
            }

            world.playAnimation(attacker, comboMotions.get(i), 0.0F);

            if (i >= size - 3) {
                world.waitForInaction(attacker);
            } else {
                world.waitForCanBasicAttack(attacker);
            }
        }
    }

    /**
     * 标准武器基础连段展示 (完整重载：带副手和指定姿势)
     */
    public static void showcaseStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack mainHandItem, ItemStack offHandItem, Style showcaseStyle) {

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
            Map<Style, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> motionsMap =
                    ((WeaponCapabilityAccessor) weaponCap).getAutoAttackMotions();

            comboMotions = motionsMap.getOrDefault(showcaseStyle, motionsMap.get(CapabilityItem.Styles.COMMON));
        } else {
            comboMotions = CapabilityItem.getBasicAutoAttackMotion();
        }

        playStandardComboWithDashAndJump(
                builder, util, attacker, comboMotions,
                center, centerY, center,
                "epic_fight_ponder.ponder." + sceneId + ".text_2",
                "epic_fight_ponder.ponder." + sceneId + ".text_3"
        );

        builder.idle(20);
        builder.markAsFinished();
    }

    public static void showcaseStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack weapon, Style showcaseStyle) {
        showcaseStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, showcaseStyle);
    }

    public static void showcaseStandardWeaponCombo(
            SceneBuilder baseScene, SceneBuildingUtil util,
            int size, String sceneId, ItemStack weapon) {
        showcaseStandardWeaponCombo(baseScene, util, size, sceneId, weapon, ItemStack.EMPTY, CapabilityItem.Styles.TWO_HAND);
    }
}