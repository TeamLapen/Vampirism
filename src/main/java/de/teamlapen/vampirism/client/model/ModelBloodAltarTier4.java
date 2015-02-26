package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import de.teamlapen.vampirism.util.Logger;

public class ModelBloodAltarTier4 extends ModelBase
{
  //fields
    ModelRenderer Base;
    ModelRenderer Blood_Cube;
    ModelRenderer Magic;
    int bloodLevel;
  
  public ModelBloodAltarTier4()
  {
    textureWidth = 64;
    textureHeight = 64;
    
    
      Base = new ModelRenderer(this, 0, 0);
      Base.addBox(-2F, 0F, -2F, 16, 5, 16);
      Base.setRotationPoint(-6F, 19F, -6F);
      Base.setTextureSize(64, 64);
      Base.mirror = true;
      setRotation(Base, 0F, 0F, 0F);
      Blood_Cube = new ModelRenderer(this, 0, 41);
      Blood_Cube.addBox(-3F, -2F, -3F, 6, 6, 6);
      Blood_Cube.setRotationPoint(0F, 11F, -1F);
      Blood_Cube.setTextureSize(64, 64);
      Blood_Cube.mirror = true;
      setRotation(Blood_Cube, 0.8726646F, 0.8726646F, 0.8726646F);
      Magic = new ModelRenderer(this, 0, 23);
      Magic.addBox(0F, 0F, 0F, 16, 0, 16);
      Magic.setRotationPoint(-8F, 17F, -8F);
      Magic.setTextureSize(64, 64);
      Magic.mirror = true;
      setRotation(Magic, 0F, 0F, 0F);
  }
  
  @Override
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5,entity);
    Base.render(f5);
    Blood_Cube.render(f5);
    Magic.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  @Override
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5,Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5,entity);
  }
  
  public void setBloodLevel(int i){
  	this.bloodLevel=i;
  }  

}
