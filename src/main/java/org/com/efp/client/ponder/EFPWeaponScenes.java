package org.com.efp.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.com.efp.api.ponder.EpicFightSceneBuilder;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.item.EpicFightItems;

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
        sheathStack.getOrCreateTag().putInt("sheath", 1);
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
        EFPSceneUtils.showcaseStandardWeaponCombo(baseScene, util, 11, "longsword_basic_attack_combo_ochs",
                EpicFightItems.DIAMOND_LONGSWORD.get().getDefaultInstance(), ItemStack.EMPTY, CapabilityItem.Styles.OCHS, false, true);
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

    /**
     * 技能展示：RushingTempo
     */
    public static void showcaseRushingTempo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "tachi_rushing_tempo", "epic_fight_ponder.ponder.tachi_rushing_tempo.title");
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActor(builder, 5.5, 1, 5.5, 180, new ItemStack(EpicFightItems.DIAMOND_TACHI.get()));
        builder.idle(20);

        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_1", 40, 5, 1, 5);
        builder.idle(50);

        // 普攻起手
        world.playAnimation(attacker, Animations.TACHI_AUTO1, 0.0F);

        // 第一个派生慢动作提示
        world.waitForCanUseSkill(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.05F);
        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_2", 40, 5, 2, 5);
        builder.idle(50);

        world.modifyEntityPlaySpeed(attacker, 1.0F);
        world.playAnimation(attacker, Animations.RUSHING_TEMPO1, 0.0F);

        //微时缓增加打击感
        world.waitForCanBasicAttack(attacker);
        world.modifyEntityPlaySpeed(attacker, 0.25F);
        EFPSceneUtils.showTextAtTop(builder, util, "epic_fight_ponder.ponder.tachi_rushing_tempo.text_3", 100, 5, 2, 5);
        builder.idle(20);

        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, Animations.TACHI_AUTO2, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, Animations.RUSHING_TEMPO2, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_USE_SKILL, Animations.TACHI_AUTO3, 0.2F, 5);
        EFPSceneUtils.playDerivationWithSlowMo(builder, attacker, EFPSceneUtils.WaitType.CAN_BASIC_ATTACK, Animations.RUSHING_TEMPO3, 0.2F, 5);

        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }
}