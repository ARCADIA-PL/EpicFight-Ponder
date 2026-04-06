package org.arc.efp.compat;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.registries.EFNItem;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.arc.efp.api.event.PonderCombatEvent;
import org.arc.efp.api.ponder.EpicFightSceneBuilder;
import org.arc.efp.client.ponder.EFPSceneUtils;

import java.util.function.Consumer;

import static org.arc.efp.compat.EFNCompat.createYamatoSmartParryCallback;

public class EFNxEFXCompat {

    public static void showcaseEFNParrySkill_Third(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder builder = new EpicFightSceneBuilder(baseScene);
        EpicFightSceneBuilder.EpicFightWorldInstructions world = builder.world();

        EFPSceneUtils.setupStandardScene(builder, 11, "skill_parry_efn_third", "epic_fight_ponder.ponder.skill_parry_efn_third.title");

        double centerX = 5.5;
        double centerZ = 5.5;
        double attackerZ = centerZ - 2.0;

        ItemStack victimWeapon = new ItemStack(EFNItem.CO_TACHI.get());

        ElementLink<EntityElement> victim = EFPSceneUtils.spawnDummyVictim(builder, centerX, 1.0, centerZ, 180, victimWeapon, ItemStack.EMPTY);
        ElementLink<EntityElement> attacker = EFPSceneUtils.spawnDummyActorWithItem(builder, centerX, 1.0, attackerZ, 0, new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD));

        world.modifyEntityMovement(victim, false);

        EFPSceneUtils.showText(builder, util, "epic_fight_ponder.ponder.skill_parry_efn_third.text_1", 80, 5, 1, 5);
        builder.idle(20);

        Consumer<PonderCombatEvent.Hit> parryCallback = createYamatoSmartParryCallback(
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1.get(),
                EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2.get()
        );

        world.playAnimation(attacker, AnimationsX.DANCING_EDGE, 0.15F, parryCallback, beHit -> {});

        // 3. 延迟 1 tick 的极限反应时间
        builder.idle(1);

        world.playAnimation(victim, AnimationsX.LONGSWORD_GUARD, 0.0F);

        world.waitForInaction(attacker);

        builder.idle(30);
        builder.markAsFinished();
    }

}
