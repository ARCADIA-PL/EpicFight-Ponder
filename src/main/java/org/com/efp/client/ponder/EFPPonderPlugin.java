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

        registerWeaponGroup(itemHelper, TachiItem.class, EFPWeaponScenes::showcaseTachiBasicAttackCombo, EFPWeaponScenes::showcaseRushingTempo);
        registerWeaponGroup(itemHelper, UchigatanaItem.class, EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo, EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
        registerWeaponGroup(itemHelper, GreatswordItem.class, EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo);
        registerWeaponGroup(itemHelper, LongswordItem.class, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_Ochs, EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand);
        registerWeaponGroup(itemHelper, DaggerItem.class, EFPWeaponScenes::showcaseDaggerBasicAttackCombo, EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
        registerWeaponGroup(itemHelper, SpearItem.class, EFPWeaponScenes::showcaseSpearBasicAttackCombo, EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand);

        registerWeaponGroup(itemHelper, SwordItem.class, true, EFPWeaponScenes::showcaseSwordBasicAttackCombo, EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual);
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
}