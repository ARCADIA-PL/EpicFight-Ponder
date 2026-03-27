package org.com.efp.client.ponder;

import net.createmod.catnip.platform.ForgeRegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.com.efp.EpicFightPonder;
import org.com.efp.compat.EFPCompatManager;
import org.com.efp.compat.EFXCompat;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.item.*;

public class EFPPonderPlugin implements PonderPlugin {

    public interface PonderSceneMethod {
        void build(SceneBuilder scene, SceneBuildingUtil util);
    }

    @Override
    public @NotNull String getModId() {
        return EpicFightPonder.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ForgeRegisteredObjectsHelper forgeObjectsHelper = new ForgeRegisteredObjectsHelper();

        PonderSceneRegistrationHelper<Item> itemHelper = helper.withKeyFunction(forgeObjectsHelper::getKeyOrThrow);

        PonderSceneRegistrationHelper<String> skillHelper = helper.withKeyFunction(
                skillString -> {
                    ResourceLocation rl = ResourceLocation.parse(skillString);
                    return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "skill_" + rl.getPath());
                }
        );

        if (EFPCompatManager.isEFXLoaded) {
            registerWeaponGroup(itemHelper, TachiItem.class, EFPWeaponScenes::showcaseTachiBasicAttackCombo, EFXCompat::showcaseRushingTempo_EFX);
        } else {
            registerWeaponGroup(itemHelper, TachiItem.class, EFPWeaponScenes::showcaseTachiBasicAttackCombo, EFPWeaponScenes::showcaseRushingTempo);
        }

        registerWeaponGroup(itemHelper, UchigatanaItem.class, EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo, EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
        registerWeaponGroup(itemHelper, GreatswordItem.class, EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo);
        registerWeaponGroup(itemHelper, LongswordItem.class, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_Ochs, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand);
        registerWeaponGroup(itemHelper, DaggerItem.class, EFPWeaponScenes::showcaseDaggerBasicAttackCombo, EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
        registerWeaponGroup(itemHelper, SpearItem.class, EFPWeaponScenes::showcaseSpearBasicAttackCombo, EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand);
        registerWeaponGroup(itemHelper, SwordItem.class, true, EFPWeaponScenes::showcaseSwordBasicAttackCombo, EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual);

        registerSkillBookFallback(itemHelper, EFPSKillScenes::showcaseNoSkill);

        registerSkill(skillHelper, "epicfight:guard", EFPSKillScenes::showcaseGuardSkill);
    }

    private void registerWeaponGroup(PonderSceneRegistrationHelper<Item> helper, Class<? extends Item> weaponClass, PonderSceneMethod... scenes) {
        registerWeaponGroup(helper, weaponClass, false, "epicfight_showcase", scenes);
    }

    private void registerWeaponGroup(PonderSceneRegistrationHelper<Item> helper, Class<? extends Item> weaponClass, boolean strictClassMatch, PonderSceneMethod... scenes) {
        registerWeaponGroup(helper, weaponClass, strictClassMatch, "epicfight_showcase", scenes);
    }

    private void registerWeaponGroup(PonderSceneRegistrationHelper<Item> helper, Class<? extends Item> weaponClass, boolean strictClassMatch, String structureId, PonderSceneMethod... scenes) {
        Item[] items = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> strictClassMatch ? item.getClass().equals(weaponClass) : weaponClass.isInstance(item))
                .toArray(Item[]::new);

        if (items.length > 0) {
            var component = helper.forComponents(items);
            for (PonderSceneMethod scene : scenes) {
                component.addStoryBoard(structureId, scene::build);
            }
        }
    }

    private void registerSkillBookFallback(PonderSceneRegistrationHelper<Item> helper, PonderSceneMethod... fallbackScenes) {
        Item[] skillBooks = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof SkillBookItem)
                .toArray(Item[]::new);

        if (skillBooks.length > 0) {
            var component = helper.forComponents(skillBooks);
            for (PonderSceneMethod scene : fallbackScenes) {
                component.addStoryBoard("epicfight_showcase", scene::build);
            }
        }
    }

    /**
     * 为特定的技能注册专属思索场景
     * @param helper 技能的 RegistrationHelper
     * @param skillId 技能全名，例如 "epicfight:guard"
     * @param scenes 该技能对应的展示场景
     */
    private void registerSkill(PonderSceneRegistrationHelper<String> helper, String skillId, PonderSceneMethod... scenes) {
        registerSkill(helper, skillId, "epicfight_showcase", scenes);
    }

    private void registerSkill(PonderSceneRegistrationHelper<String> helper, String skillId, String structureId, PonderSceneMethod... scenes) {
        var component = helper.forComponents(skillId);
        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard(structureId, scene::build);
        }
    }
}