package org.infection.juego;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class JugadorBueno extends Jugador implements IOnAreaTouchListener {

	private int g_vida = 100;
	private boolean g_sangrando = false;
  	
	public JugadorBueno(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld pmyPhysicsWorld, Scene pScene) {
        
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		body = PhysicsFactory.createBoxBody(pmyPhysicsWorld, this, BodyType.DynamicBody, objectFixtureDef);
		body.setUserData(this);
		
		pmyPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
		
		this.animate(new long[]{200,200}, 0, 1, true);
		
		this.setUserData(body);
		this.setEtiqueta("BUENO");
		pScene.registerTouchArea(this);
		pScene.attachChild(this);
	}
	
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {	
		super.onManagedUpdate(pSecondsElapsed);
		
		
		if (g_sangrando) {
			this.setG_vida(this.getG_vida()-1);

			if (this.getG_vida() <= 0 ) {

				this.stopAnimation();

			}
		}
				
	};
	

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		
		// TODO Auto-generated method stub
		return false;
	}

	public int getG_vida() {
		return g_vida;
	}

	public void setG_vida(int g_vida) {
		this.g_vida = g_vida;
	}


	public boolean isG_sangrando() {
		return g_sangrando;
	}


	public void setG_sangrando(boolean g_sangrando) {
		this.g_sangrando = g_sangrando;
	}
	
	
}
