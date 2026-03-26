package org.com.efp.client.ponder;

import net.createmod.catnip.platform.ForgeRegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.com.efp.EpicFightPonder;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.item.EpicFightItems;

public class EFPPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return EpicFightPonder.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ForgeRegisteredObjectsHelper forgeRegisteredObjectsHelper = new ForgeRegisteredObjectsHelper();
        PonderSceneRegistrationHelper<Item> itemHelper = helper.withKeyFunction(forgeRegisteredObjectsHelper::getKeyOrThrow);

        itemHelper.forComponents(EpicFightItems.NETHERITE_TACHI.get())
                .addStoryBoard("epicfight_showcase", EFPWeaponScenes::showcaseTachiBasicAttackCombo)
                .addStoryBoard("epicfight_showcase", EFPWeaponScenes::showcaseRushingTempo);
    }
}