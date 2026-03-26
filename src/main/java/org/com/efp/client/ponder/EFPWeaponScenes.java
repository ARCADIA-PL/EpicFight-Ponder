package org.com.efp.client.ponder;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.asanginxst.epicfightx.gameassets.animations.ExtraAnimations;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.entity.DummyPlayerEntity;
import org.com.efp.entity.DummyPlayerEntityPatch;
import org.com.efp.registry.EFPEntities;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.item.EpicFightItems;

import java.util.List;

public class EFPWeaponScenes {

    public static void showcaseTachiBasicAttackCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder epicFightSceneBuilder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions epicFightWorldInstructions = epicFightSceneBuilder.world();

        epicFightSceneBuilder.title("tachi_basic_attack_combo", "epic_fight_ponder.ponder.tachi_basic_attack_combo.title");
        epicFightSceneBuilder.configureBasePlate(0, 0, 11);
        epicFightSceneBuilder.showBasePlate();
        epicFightSceneBuilder.scaleSceneView(1F);

        epicFightSceneBuilder.idle(5);

        ItemStack displayItem = new ItemStack(EpicFightItems.NETHERITE_TACHI.get());

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = null;
        var itemCap = EpicFightCapabilities.getItemStackCapability(displayItem);
        if (itemCap instanceof WeaponCapability weaponCap) {
            comboMotions = weaponCap.getAutoAttackMotion(null);
        }

        ElementLink<EntityElement> attackerLink = epicFightSceneBuilder.world().createEntity(level -> {
            LivingEntity attackPerformer = EFPEntities.DUMMY_PLAYER.get().create(level);
            if (attackPerformer != null) {
                attackPerformer.setPos(5.5, 1, 5.5);
                attackPerformer.setYRot(180);
                attackPerformer.yBodyRot = 180;
                attackPerformer.yHeadRot = 180;
                attackPerformer.setItemInHand(InteractionHand.MAIN_HAND, displayItem);

                EpicFightCapabilities.getUnparameterizedEntityPatch(attackPerformer, DummyPlayerEntityPatch.class).ifPresent(dummyPlayerEntityPatch -> {
                    dummyPlayerEntityPatch.setYRot(180);
                    dummyPlayerEntityPatch.setYRotO(180);
                    dummyPlayerEntityPatch.updateLivingMotionsForPonder();
                });
            }
            return attackPerformer;
        });

        epicFightSceneBuilder.idle(10);

        epicFightSceneBuilder.overlay().showText(40)
                .text("epic_fight_ponder.ponder.tachi_basic_attack_combo.text_1")
                .pointAt(util.vector().topOf(5, 2, 5))
                .placeNearTarget();

        epicFightSceneBuilder.idle(20);

        if (comboMotions != null) {
            int size = comboMotions.size();
            for (int i = 0; i < size; i++) {

                if (i == size - 2) {
                    epicFightWorldInstructions.simulateSpring(attackerLink, 2, 10);
                    epicFightSceneBuilder.idle(10);
                    epicFightSceneBuilder.overlay().showText(30)
                            .text("epic_fight_ponder.ponder.tachi_basic_attack_combo.text_2")
                            .pointAt(util.vector().centerOf(5, 0, 5))
                            .placeNearTarget();
                } else if (i == size - 1) {
                    epicFightWorldInstructions.setPosition(attackerLink, 5.5, 1, 5.5);
                    epicFightSceneBuilder.idle(5);
                    epicFightWorldInstructions.simulateJump(attackerLink);
                    epicFightSceneBuilder.idle(8);
                    epicFightSceneBuilder.overlay().showText(40)
                            .text("epic_fight_ponder.ponder.tachi_basic_attack_combo.text_3")
                            .pointAt(util.vector().topOf(5, 2, 5))
                            .placeNearTarget();
                }

                AnimationManager.AnimationAccessor<? extends AttackAnimation> currentMotion = comboMotions.get(i);
                epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, currentMotion, 0.0F);

                if (i >= size - 3) {
                    epicFightWorldInstructions.waitForInaction(attackerLink);
                } else {
                    epicFightWorldInstructions.waitForCanBasicAttack(attackerLink);
                }
            }
        }

        epicFightSceneBuilder.idle(20);
        epicFightSceneBuilder.markAsFinished();
    }

    public static void showcaseRushingTempo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder epicFightSceneBuilder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions epicFightWorldInstructions = epicFightSceneBuilder.world();

        epicFightSceneBuilder.title("tachi_rushing_tempo", "epic_fight_ponder.ponder.tachi_rushing_tempo.title");
        epicFightSceneBuilder.configureBasePlate(0, 0, 11);
        epicFightSceneBuilder.showBasePlate();
        epicFightSceneBuilder.scaleSceneView(1F);

        epicFightSceneBuilder.idle(5);

        ItemStack displayItem = new ItemStack(EpicFightItems.NETHERITE_TACHI.get());

        ElementLink<EntityElement> attackerLink = epicFightSceneBuilder.world().createEntity(level -> {
            LivingEntity attackPerformer = EFPEntities.DUMMY_PLAYER.get().create(level);
            if (attackPerformer != null) {
                attackPerformer.setPos(5.5, 1, 5.5);
                attackPerformer.setYRot(180);
                attackPerformer.yBodyRot = 180;
                attackPerformer.yHeadRot = 180;
                attackPerformer.setItemInHand(InteractionHand.MAIN_HAND, displayItem);

                EpicFightCapabilities.getUnparameterizedEntityPatch(attackPerformer, DummyPlayerEntityPatch.class).ifPresent(dummyPlayerEntityPatch -> {
                    dummyPlayerEntityPatch.setYRot(180);
                    dummyPlayerEntityPatch.setYRotO(180);
                    dummyPlayerEntityPatch.updateLivingMotionsForPonder();
                });
            }
            return attackPerformer;
        });

        epicFightSceneBuilder.idle(20);

        epicFightSceneBuilder.overlay().showText(40)
                .text("epic_fight_ponder.ponder.tachi_rushing_tempo.text_1")
                .pointAt(util.vector().topOf(5, 1, 5))
                .placeNearTarget();

        epicFightSceneBuilder.idle(50);

        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.TACHI_AUTO1, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.05F);

        epicFightSceneBuilder.overlay().showText(40)
                .text("epic_fight_ponder.ponder.tachi_rushing_tempo.text_2")
                .pointAt(util.vector().topOf(5, 2, 5))
                .placeNearTarget();

        epicFightSceneBuilder.idle(50);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.RUSHING_TEMPO1, 0.0F);
        epicFightWorldInstructions.waitForCanBasicAttack(attackerLink);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);

        epicFightSceneBuilder.overlay().showText(100)
                .text("epic_fight_ponder.ponder.tachi_rushing_tempo.text_3")
                .pointAt(util.vector().topOf(5, 2, 5))
                .placeNearTarget();

        epicFightSceneBuilder.idle(20);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.TACHI_AUTO2, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.RUSHING_TEMPO2, 0.0F);
        epicFightWorldInstructions.waitForCanBasicAttack(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.TACHI_AUTO3, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.RUSHING_TEMPO3, 0.0F);
        epicFightWorldInstructions.waitForCanBasicAttack(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.TACHI_AUTO4, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.RUSHING_TEMPO4, 0.0F);
        epicFightWorldInstructions.waitForCanBasicAttack(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.TACHI_AUTO5, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.RUSHING_TEMPO5, 0.0F);
        epicFightWorldInstructions.waitForInaction(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightWorldInstructions.simulateSpring(attackerLink, 2, 10);
        epicFightSceneBuilder.idle(10);
        epicFightSceneBuilder.overlay().showText(80)
                .text("epic_fight_ponder.ponder.tachi_rushing_tempo.text_4")
                .pointAt(util.vector().topOf(5, 2, 5))
                .placeNearTarget();

        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, AnimationsX.TACHI_DASH, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.RUSHING_DASH, 0.0F);
        epicFightWorldInstructions.waitForInaction(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightWorldInstructions.setPosition(attackerLink, 5.5, 1, 5.5);
        epicFightSceneBuilder.idle(5);
        epicFightWorldInstructions.simulateJump(attackerLink);
        epicFightSceneBuilder.idle(8);

        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.TACHI_AIR_SLASH, 0.0F);
        epicFightWorldInstructions.waitForCanUseSkill(attackerLink);
        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 0.25F);
        epicFightSceneBuilder.idle(10);

        epicFightWorldInstructions.modifyEntityPlaySpeed(attackerLink, 1.0F);
        epicFightSceneBuilder.world().playEntitiesAnimation(DummyPlayerEntity.class, ExtraAnimations.RUSHING_AIR_SLASH, 0.0F);
        epicFightWorldInstructions.waitForInaction(attackerLink);

        epicFightSceneBuilder.idle(30);
        epicFightSceneBuilder.markAsFinished();
    }
}