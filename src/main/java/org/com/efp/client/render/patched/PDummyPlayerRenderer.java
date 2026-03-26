package org.com.efp.client.render.patched;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import org.com.efp.client.render.DummyPlayerRenderer;
import org.com.efp.entity.DummyPlayerEntity;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class PDummyPlayerRenderer extends PHumanoidRenderer<DummyPlayerEntity, HumanoidMobPatch<DummyPlayerEntity>, PlayerModel<DummyPlayerEntity>, DummyPlayerRenderer, HumanoidMesh> {

    public PDummyPlayerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(Meshes.BIPED, context, entityType);

        this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
    }

    @Override
    protected void prepareModel(HumanoidMesh mesh, DummyPlayerEntity entity, HumanoidMobPatch<DummyPlayerEntity> dummyPlayerEntityHumanoidMobPatch, DummyPlayerRenderer renderer) {
        super.prepareModel(mesh, entity, dummyPlayerEntityHumanoidMobPatch, renderer);

        PlayerModel<DummyPlayerEntity> model = renderer.getModel();

        mesh.head.setHidden(!model.head.visible);
        mesh.hat.setHidden(!model.hat.visible);
        mesh.jacket.setHidden(!model.jacket.visible);
        mesh.torso.setHidden(!model.body.visible);
        mesh.leftArm.setHidden(!model.leftArm.visible);
        mesh.leftLeg.setHidden(!model.leftLeg.visible);
        mesh.leftPants.setHidden(!model.leftPants.visible);
        mesh.leftSleeve.setHidden(!model.leftSleeve.visible);
        mesh.rightArm.setHidden(!model.rightArm.visible);
        mesh.rightLeg.setHidden(!model.rightLeg.visible);
        mesh.rightPants.setHidden(!model.rightPants.visible);
        mesh.rightSleeve.setHidden(!model.rightSleeve.visible);
    }

    @Override
    public AssetAccessor<HumanoidMesh> getMeshProvider(HumanoidMobPatch<DummyPlayerEntity> dummyPlayerEntityHumanoidMobPatch) {
        if (Minecraft.getInstance().player != null && "slim".equals(Minecraft.getInstance().player.getModelName())) {
            return Meshes.ALEX;
        }
        return Meshes.BIPED;
    }

    @Override
    public AssetAccessor<HumanoidMesh> getDefaultMesh() {
        return Meshes.BIPED;
    }
}