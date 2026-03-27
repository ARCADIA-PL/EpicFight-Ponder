package org.com.efp.compat;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.asanginxst.epicfightx.gameassets.animations.ExtraAnimations;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import org.com.efp.client.ponder.EFPSceneUtils;
import yesman.epicfight.world.item.EpicFightItems;

public class EFXCompat {

    /**
     * 技能展示：EFX RushingTempo
     */
    public static void showcaseRushingTempo_EFX(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "tachi_rushing_tempo", "epic_fight_ponder.ponder.tachi_rushing_tempo.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_TACHI.get()));

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, 5.5, 1, 4.0, 0, new ItemStack(Items.DIAMOND_SWORD));
        builder.idle(20);

        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_1", 40, 5, 1, 5);
        builder.idle(50);

        // 普攻起手
        world.playAnimation(attacker, AnimationsX.TACHI_AUTO1, 0.0F);

        // 第一个派生慢动作提示
        world.waitForCanUseSkill(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.05F);
        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_2", 40, 5, 2, 5);
        builder.idle(50);

        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.playAnimation(attacker, AnimationsX.RUSHING_TEMPO1, 0.0F);

        //微时缓增加打击感
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.25F);
        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_3", 100, 5, 2, 5);
        builder.idle(20);

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
        world.simulateSpring(attacker, 2, 10);
        builder.idle(10);
        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_4", 80, 5, 2, 5);

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

        builder.idle(30);
        builder.markAsFinished();
    }

}
