package org.infection.juego;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;

import android.util.Log;


public class JugadorMalo extends Jugador implements IOnAreaTouchListener {
	
	
	public JugadorMalo(float pX, float pY,
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
		this.setEtiqueta("MALO");
		pScene.registerTouchArea(this);
		pScene.attachChild(this);
		
		
				
		// TODO Auto-generated constructor stub
	}

	/*@Override
	protected void onManagedUpdate(float pSecondsElapsed) {


		
		
		super.onManagedUpdate(pSecondsElapsed);
				
	};*/
	
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		
		// TODO Auto-generated method stub
		return false;
	}

	
	
	/*@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		
		/*switch (pSceneTouchEvent.getAction()) {
		
		case TouchEvent.ACTION_DOWN: {
			
			
			
			
		}

		case TouchEvent.ACTION_UP: {

		}
		
		case TouchEvent.ACTION_MOVE: {
			this.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			
		}				

	}
		
	return false;
		
	}*/
	
	

}
