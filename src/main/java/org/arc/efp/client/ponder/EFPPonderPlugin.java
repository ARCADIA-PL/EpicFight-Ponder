package org.arc.efp.client.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.arc.efp.EpicFightPonder;
import org.arc.efp.compat.EFNCompat;
import org.arc.efp.compat.EFNxEFXCompat;
import org.arc.efp.compat.EFPCompatManager;
import org.arc.efp.compat.EFXCompat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.world.capabilities.item.ItemKeywordReloadListener;
import yesman.epicfight.world.item.SkillBookItem;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EFPPonderPlugin implements PonderPlugin {

    public static final Set<String> REGISTERED_SKILLS = ConcurrentHashMap.newKeySet();

    private static final Map<Item, ResourceLocation> CACHED_WEAPON_TYPES = new ConcurrentHashMap<>();
    private static boolean isCacheBuilt = false;

    public EFPPonderPlugin() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    public static ResourceLocation getCustomPonderId(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        if (item.getItem() instanceof SkillBookItem) {
            if (item.getTag() != null && item.hasTag() && item.getTag().contains("skill")) {
                String skillId = item.getTag().getString("skill");
                ResourceLocation rawRl = ResourceLocation.parse(skillId);
                return ResourceLocation.fromNamespaceAndPath(rawRl.getNamespace(), "skill_" + rawRl.getPath());
            }
        }

        if (!isCacheBuilt) {
            rebuildWeaponTypeCache();
        }

        ResourceLocation presetId = CACHED_WEAPON_TYPES.get(item.getItem());
        if (presetId != null) {
            return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + presetId.getPath());
        }

        return null;
    }

    private static synchronized void rebuildWeaponTypeCache() {
        CACHED_WEAPON_TYPES.clear();

        ItemCapabilityReloadListener.getWeaponDataStream().forEach(tag -> {
            if (tag.contains("type")) {
                Item item = Item.byId(tag.getInt("id"));
                ResourceLocation presetId = ResourceLocation.parse(tag.getString("type"));
                CACHED_WEAPON_TYPES.put(item, presetId);
            }
        });

        Map<ResourceLocation, ItemKeywordReloadListener.ItemRegex> regexes = ItemKeywordReloadListener.getRegexes();

        for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            Item item = entry.getValue();

            if (CACHED_WEAPON_TYPES.containsKey(item) || item instanceof BlockItem) {
                continue;
            }

            ResourceLocation registryName = entry.getKey().location();
            boolean matched = false;

            for (Map.Entry<ResourceLocation, ItemKeywordReloadListener.ItemRegex> regexEntry : regexes.entrySet()) {
                if (regexEntry.getValue().matchesAny(registryName.toString())) {
                    CACHED_WEAPON_TYPES.put(item, regexEntry.getKey());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                Class<?> clazz = item.getClass();
                while (clazz != null) {
                    if (SwordItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:sword"));
                        break;
                    } else if (AxeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:axe"));
                        break;
                    } else if (PickaxeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:pickaxe"));
                        break;
                    } else if (ShovelItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:shovel"));
                        break;
                    } else if (HoeItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:hoe"));
                        break;
                    } else if (BowItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:bow"));
                        break;
                    } else if (CrossbowItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:crossbow"));
                        break;
                    } else if (ShieldItem.class.equals(clazz)) {
                        CACHED_WEAPON_TYPES.put(item, ResourceLocation.parse("epicfight:shield"));
                        break;
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        }

        isCacheBuilt = true;
    }

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        isCacheBuilt = false;
    }

    @Override
    public @NotNull String getModId() {
        return EpicFightPonder.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {

        PonderSceneRegistrationHelper<String> skillHelper = helper.withKeyFunction(
                skillString -> {
                    ResourceLocation rl = ResourceLocation.parse(skillString);
                    return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "skill_" + rl.getPath());
                }
        );

        PonderSceneRegistrationHelper<String> weaponHelper = helper.withKeyFunction(
                presetId -> {
                    ResourceLocation rl = ResourceLocation.parse(presetId);
                    return ResourceLocation.fromNamespaceAndPath(EpicFightPonder.MOD_ID, "weapon_" + rl.getPath());
                }
        );

        // ==== 注册技能 ====
        registerSkill(skillHelper, "epic_fight_ponder:fallback",
                EFPSKillScenes::showcaseNoSkill);
        registerSkill(skillHelper, "epicfight:guard",
                EFPSKillScenes::showcaseGuardSkill,
                EFPSKillScenes::showcaseGuardSkillBreak);
        registerSkill(skillHelper, "epicfight:parrying",
                EFPSKillScenes::showcaseParrySkill);

        if (EFPCompatManager.isEFXLoaded) {
            registerSkill(skillHelper, "epicfight:technician",
                    EFXCompat::showcaseTechnicianSkill_EFX);
            registerSkill(skillHelper, "epicfight:step",
                    EFXCompat::showcaseStepSkill_EFX);
            registerSkill(skillHelper, "epicfight:roll",
                    EFXCompat::showcaseRollSkill_EFX);
        } else {
            registerSkill(skillHelper, "epicfight:technician",
                    EFPSKillScenes::showcaseTechnicianSkill);
            registerSkill(skillHelper, "epicfight:step",
                    EFPSKillScenes::showcaseStepSkill);
            registerSkill(skillHelper, "epicfight:roll",
                    EFPSKillScenes::showcaseRollSkill);
        }

        if (EFPCompatManager.isEFNLoaded) {
            registerSkill(skillHelper, "efn:efn_parry",
                    EFNCompat::showcaseEFNParrySkill_First,
                    EFNCompat::showcaseEFNParrySkill_Second);
            registerSkill(skillHelper, "efn:efn_dodge",
                    EFNCompat::showcaseRollSkill_EFN);
            registerSkill(skillHelper, "efn:efn_step",
                    EFNCompat::showcaseStepSkill_EFN);

        }

        if (EFPCompatManager.isEFNLoaded && EFPCompatManager.isEFXLoaded) {
            registerSkill(skillHelper, "efn:efn_parry",
                    EFNxEFXCompat::showcaseEFNParrySkill_Third);
        }

        // ==== 注册武器====
        if (EFPCompatManager.isEFXLoaded) {
            registerPreset(weaponHelper, "epicfight:sword",
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo,
                    EFXCompat::showcaseSweepingEdge_EFX,
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual,
                    EFXCompat::showcaseDancingEdge_EFX);
            registerPreset(weaponHelper, "epicfight:dagger",
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo,
                    EFXCompat::showcaseEviscerate_EFX,
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
            registerPreset(weaponHelper, "epicfight:dagger", "epicfight_showcase_long",
                    EFXCompat::showcaseBladeRush_EFX);
            registerPreset(weaponHelper, "epicfight:spear",
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo,
                    EFXCompat::showcaseGraspingSpire_EFX,
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand,
                    EFXCompat::showcaseHeartPiercer_EFX);
            registerPreset(weaponHelper, "epicfight:axe",
                    EFPWeaponScenes::showcaseAxeBasicAttackCombo,
                    EFXCompat::showcaseGuillotine_EFX);
            registerPreset(weaponHelper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFXCompat::showcaseRushingTempo_EFX);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_UnSheath_EFX);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFXCompat::showcaseUchigatanaPassive_EFX,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFXCompat::showcaseUchigatanaBattojutsu_Sheath_EFX);
            registerPreset(weaponHelper, "epicfight:longsword",
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo,
                    EFXCompat::showcaseLongSwordBasicAttackCombo_Ochs_EFX,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand,
                    EFXCompat::showcaseSharpStab_EFX);
            registerPreset(weaponHelper, "epicfight:greatsword",
                    EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo,
                    EFXCompat::showcaseSteelWhirlWind_EFX);
        } else {
            registerPreset(weaponHelper, "epicfight:sword",
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseSweepingEdge,
                    EFPWeaponScenes::showcaseSwordBasicAttackCombo_Dual,
                    EFPWeaponScenes::showcaseDancingEdge);
            registerPreset(weaponHelper, "epicfight:dagger",
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo,
                    EFPWeaponScenes::showcaseEviscerate,
                    EFPWeaponScenes::showcaseDaggerBasicAttackCombo_Dual);
            registerPreset(weaponHelper, "epicfight:dagger", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseBladeRush);
            registerPreset(weaponHelper, "epicfight:spear",
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo,
                    EFPWeaponScenes::showcaseGraspingSpire,
                    EFPWeaponScenes::showcaseSpearBasicAttackCombo_OneHand,
                    EFPWeaponScenes::showcaseHeartPiercer);
            registerPreset(weaponHelper, "epicfight:axe",
                    EFPWeaponScenes::showcaseAxeBasicAttackCombo,
                    EFPWeaponScenes::showcaseGuillotine);
            registerPreset(weaponHelper, "epicfight:tachi",
                    EFPWeaponScenes::showcaseTachiBasicAttackCombo,
                    EFPWeaponScenes::showcaseRushingTempo);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_UnSheath);
            registerPreset(weaponHelper, "epicfight:uchigatana",
                    EFPWeaponScenes::showcaseUchigatanaPassive,
                    EFPWeaponScenes::showcaseUchigatanaBasicAttackCombo_Sheath);
            registerPreset(weaponHelper, "epicfight:uchigatana", "epicfight_showcase_long",
                    EFPWeaponScenes::showcaseUchigatanaBattojutsu_Sheath);
            registerPreset(weaponHelper, "epicfight:longsword",
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_Ochs,
                    EFPWeaponScenes::showcaseLongSwordBasicAttackCombo_OneHand,
                    EFPWeaponScenes::showcaseSharpStab);
            registerPreset(weaponHelper, "epicfight:greatsword",
                    EFPWeaponScenes::showcaseGreatSwordBasicAttackCombo,
                    EFPWeaponScenes::showcaseSteelWhirlWind);
        }
    }

    private void registerSkill(PonderSceneRegistrationHelper<String> helper, String skillId, PonderSceneMethod... scenes) {
        REGISTERED_SKILLS.add(skillId);
        registerPreset(helper, skillId, scenes);
    }

    private void registerPreset(PonderSceneRegistrationHelper<String> helper, String presetOrSkillId, PonderSceneMethod... scenes) {
        var component = helper.forComponents(presetOrSkillId);
        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard("epicfight_showcase", scene::build);
        }
    }

    private void registerPreset(PonderSceneRegistrationHelper<String> helper, String presetOrSkillId, String structure, PonderSceneMethod... scenes) {
        var component = helper.forComponents(presetOrSkillId);
        for (PonderSceneMethod scene : scenes) {
            component.addStoryBoard(structure, scene::build);
        }
    }

    public interface PonderSceneMethod {
        void build(SceneBuilder scene, SceneBuildingUtil util);
    }
}