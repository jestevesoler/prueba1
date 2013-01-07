package org.infection.juego;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 21:18:08 - 27.06.2010
 */
public class InfectionV1Activity extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 360;
	private static final int CAMERA_HEIGHT = 240;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mMaloTextureRegion;

	private int mFaceCount = 0;

	private PhysicsWorld mPhysicsWorld;

	private float mGravityX;
	private float mGravityY;

	private Scene mScene;

	private ArrayList<JugadorBueno> buenos = new ArrayList<JugadorBueno>();

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mMaloTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"malov1.png", 0, 32, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_EARTH), false);

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2,
				CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2,
				vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT,
				vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2,
				CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
				0.5f, 0.5f);
		((Body)PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
				BodyType.StaticBody, wallFixtureDef)).setUserData("GROUND");
		((Body)PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
				BodyType.StaticBody, wallFixtureDef)).setUserData("ROOF");
		((Body)PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, wallFixtureDef)).setUserData("LEFT");;
		((Body)PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, wallFixtureDef)).setUserData("RIGHT");;

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.setOnAreaTouchListener(this);
	    this.mPhysicsWorld.setContactListener(createContactListener());

		JugadorMalo malo = new JugadorMalo(5, 5, this.mMaloTextureRegion,
				this.getVertexBufferObjectManager(), this.mPhysicsWorld,
				this.mScene);
		anyadeBuenos(0);

		return this.mScene;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
			final ITouchArea pTouchArea, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {

		if (pSceneTouchEvent.isActionMove()) {
			final Jugador face = (Jugador) pTouchArea;

			//Log.d("TOCANDO", (String) ((Body) face.getUserData()).getUserData());

			if (face.getEtiqueta() == "BUENO") {
				this.jumpFace(face);
			} else {

				// TODO: Añadir sprite mordiendo dedo, quitar vida o algo a
				// Jugador. Paralizar Malo
				Vibrator vib = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
				vib.vibrate(500);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		/*
		 * if(this.mPhysicsWorld != null) { if(pSceneTouchEvent.isActionDown())
		 * { this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		 * return true; } }
		 */
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(
			final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		this.mGravityX = pAccelerationData.getX();
		this.mGravityY = pAccelerationData.getY();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX,
				this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;

		final AnimatedSprite face;
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1,
				0.5f, 0.5f);

		face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion,
				this.getVertexBufferObjectManager());
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face,
				BodyType.DynamicBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face,
				body, true, true));

		face.animate(new long[] { 200, 200 }, 0, 1, true);
		face.setUserData(body);

		this.mScene.registerTouchArea(face);
		this.mScene.attachChild(face);
	}

	private void anyadeBuenos(int p_cuantos) {
		int i = 0;

		while (i <= p_cuantos) {

			buenos.add(new JugadorBueno(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2,
						this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager(),
						this.mPhysicsWorld, this.mScene));
			i++;
		}

	}

	private void jumpFace(final AnimatedSprite face) {
		final Body faceBody = (Body) face.getUserData();

		final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50,
				this.mGravityY * -50);
		faceBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}

	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				final Body b1 = x1.getBody();
				final Body b2 = x2.getBody();
				
				final Jugador j1 = (Jugador) b1.getUserData();
				final Jugador j2 = (Jugador) b2.getUserData();
				
				if (b1.getUserData().getClass().getName() == "Jugador") {
				
					if ((j1.getEtiqueta().equals("BUENO")) && 
						(j2.getEtiqueta().equals("MALO"))) {

							// TODO: Combinar Sprite Bueno y malo
							// Quitar vida al Bueno, si la vida del bueno termina
							// convertir en malo
							//((JugadorBueno)j1).setG_sangrando(true);
							

						}
					
				}
								
			}

			@Override
			public void endContact(Contact contact) {
				
			/*	final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				final Body b1 = x1.getBody();
				final Body b2 = x2.getBody();
				
				final Jugador j1 = (Jugador) b1.getUserData();
				final Jugador j2 = (Jugador) b2.getUserData();
				
				if (b1.getUserData().getClass().getName() == "Jugador") {
				
					if ((j1.getEtiqueta().equals("BUENO")) && 
						(j2.getEtiqueta().equals("MALO"))) {

							// TODO: Combinar Sprite Bueno y malo
							// Quitar vida al Bueno, si la vida del bueno termina
							// convertir en malo
							//((JugadorBueno)j1).setG_sangrando(false);
							
						}
					
				}*/

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}

		};
		return contactListener;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
