package com.adamgamesstudio.android.poptheballoon;

import com.adamgamesstudio.android.poptheballoon.utils.ColorUtils;
import com.adamgamesstudio.android.poptheballoon.utils.ImageColorApproximation;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopTheBalloon extends ApplicationAdapter {

	Random r = new Random();
	SpriteBatch batch;

	BitmapFont font;

	public int balloonAmount = 300;

	public Logger logger = Logger.getLogger("Game Level /L");

	Texture background, paper_bg_small, i_life_full, i_life_empty;

	Texture[][] balloon = new Texture[6][6]; // TODO
	Sprite[] balloonSprite = new Sprite[balloonAmount];

	Texture[] string = new Texture[2];
	Sprite[] stringSprite = new Sprite[balloonAmount];

	String backgroundPath;

	public int balloonWidth, balloonHeight;

	public int balloonSpeed = 3, balloonSpeedMultiplier, balloonSpeedTempCounter, currentTarget, currentTargetTimeLeft, lives, points;

	public int[] balloonType = new int[balloonAmount], balloonAnimationState = new int[balloonAmount]; // TODO


	public boolean isPaused = false;

	@Override
	public void create () {
		/* START */
		batch = new SpriteBatch();

		assignAssets();

		/* Setting Up Timer for Moving Objects */
		Timer t = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < balloonAmount; i++) {
					if (balloonAnimationState[i] == 2) {
						stringSprite[i].setAlpha(0);
					}

					if (balloonAnimationState[i] == 6) {
						balloonSprite[i].setPosition(-balloonSprite[i].getWidth(), -balloonSprite[i].getWidth());
						stringSprite[i].setPosition(-balloonSprite[i].getWidth(), -balloonSprite[i].getWidth());
					} else {
						balloonSprite[i].translateY(balloonSpeed); // balloonSpeed / 2
						stringSprite[i].translateY(balloonSpeed); // balloonSpeed / 2
					}
				}
			}
		};
		t.scheduleAtFixedRate(tt,100,15);

		/* Setting Up Balloon Positions */
		for (int i = 0; i < balloonAmount; i++) {

			//balloonSprite[i].setTexture(balloon[r.nextInt(balloon.length)][0]);
			//balloonSprite[i].setFlip(false, r.nextBoolean());

			stringSprite[i].setTexture(string[r.nextInt(1)]);

			balloonSprite[i].setX((float) (r.nextInt(Gdx.graphics.getWidth() - (int) (balloonWidth / 2)) - (int) (balloonWidth / 4)));
			stringSprite[i].setX((float) (balloonSprite[i].getX() + (balloonSprite[i].getWidth() / 2)) - 8);

			//balloonSprite[i].setRotation(r.nextInt(20) - 10);

			if (i != 0) {
				balloonSprite[i].setY((float) 0 - ((r.nextInt(150) + 550) + -balloonSprite[i - 1].getY())); // (r.nextInt(150)
			} else {
				balloonSprite[i].setY((float) 0 - (r.nextInt(150) + 150 +  (balloonSprite[i].getHeight())));
			}

			stringSprite[i].setY(balloonSprite[i].getY() - (balloonSprite[i].getHeight() / 2));

			//balloonSprite[i].setColor(Color.BLUE);
		}


		lives = 10;

		/* END */
	}


	@Override
	public void render () {
		/* START */
		batch.begin();

		/* Drawing Background */
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//logger.log(Level.INFO, "b a " + balloonSprite[0].getX() + " " + balloonSprite[0].getY() + "b a " + balloonSprite[1].getX() + " " + balloonSprite[1].getY());

		/* Drawing Text Tip */
		font.getData().setScale(3f);
		//font.setColor(ColorUtils.getContrastColor(ImageColorApproximation.getColor(Gdx.files.internal(backgroundPath))));
		//font.draw(batch, "GET READY!", (int) (Gdx.graphics.getWidth() / 2) - (int) (new GlyphLayout(font, "GET READY!").width / 2), (int) (Gdx.graphics.getHeight() / 2) + (int) (new GlyphLayout(font, "GET READY!").height / 2));


		/* Drawing Balloons & Checking If Touched */
		for (int i = 0; i < balloonAmount; i++) {
			// Draw the balloon
			stringSprite[i].draw(batch);
			balloonSprite[i].draw(batch);

			if (balloonSprite[i].getY() > Gdx.graphics.getHeight()) {
				balloonAnimationState[i] = 6;

				if (balloonAnimationState[i] == 0) {
					lives--;
				}
			}

			// Check if the balloon is touched
			if (Gdx.input.justTouched()) {
				int x = Gdx.input.getX();
				int y = Gdx.graphics.getHeight() - Gdx.input.getY();

				if (x > balloonSprite[i].getX() && x < balloonSprite[i].getX() + balloonWidth &&
						y > balloonSprite[i].getY() && y < balloonSprite[i].getY() + balloonHeight) {
					if (balloonAnimationState[i] == 0) {
						// Balloon is touched, perform the explosion animation
						poppedBalloon = i;
						animateBalloon();

						// Log a message to verify that the balloon was touched
						logger.log(Level.INFO, "Balloon " + i + " touched");
						logger.log(Level.INFO, "Speed: " + balloonSpeed);

						points++;
						if (points % 2 == 0 && points < 125) { // if is even
							if (points > 50) {
								balloonSpeedMultiplier++;
								if (balloonSpeedMultiplier % 2 == 0) {
									balloonSpeed++;
								}
							} else {
								balloonSpeed++;
							}
						}
					} else {
						logger.log(Level.INFO, "Wrong Request.");
					}
				}
			}
		}
		batch.flush();

		/* Drawing Top UI */
		batch.draw(paper_bg_small, -25, Gdx.graphics.getHeight() - 40 - paper_bg_small.getHeight(), Gdx.graphics.getWidth() + 50, paper_bg_small.getHeight() + 30);

		//font.setColor(Color.BLACK); // Set the font color back to black
		font.getData().setScale(2.5f);
		font.draw(batch, "" + lives, 155, Gdx.graphics.getHeight() - 40 - (int) (paper_bg_small.getHeight() / 2) + 35);

		font.draw(batch, "" + currentTargetTimeLeft, Gdx.graphics.getWidth() - 355 + new GlyphLayout(font, "0" + currentTargetTimeLeft).width, Gdx.graphics.getHeight() - 40 - (int) (paper_bg_small.getHeight() / 2) + 35);

		font.getData().setScale(1.8f);
		font.draw(batch, "" + points, (int) (Gdx.graphics.getWidth() / 2) - (int) (new GlyphLayout(font, "" + points).width / 2), Gdx.graphics.getHeight() - 40 - (int) (paper_bg_small.getHeight() / 2) + 10);
		font.getData().setScale(1f);
		font.draw(batch, "points", (int) (Gdx.graphics.getWidth() / 2) - (int) (new GlyphLayout(font, "points").width / 2), Gdx.graphics.getHeight() - 48 - (int) (paper_bg_small.getHeight() / 2) - 25);

		batch.draw(i_life_full, 60, Gdx.graphics.getHeight() - 40 - (int) (paper_bg_small.getHeight() / 2) - 30, 80, 80);

		batch.draw(balloon[currentTarget][0], Gdx.graphics.getWidth() - 230, Gdx.graphics.getHeight() - 40 - (int) (paper_bg_small.getHeight() / 2) - 85, 200, 200);


		if (lives == 0) {

		}

		/* END */
		batch.end();
	}


	int poppedBalloon;
	private Timer t;

	public void animateBalloon() {
		if (t != null) {
			t.cancel();
/*
			for (int i = 0; i < balloonAmount; i++) {
				if (balloonAnimationState[i] != 0) {
					//balloonAnimationState[i] = 6;                TODO
					balloonSprite[poppedBalloon].setAlpha(0);
				}
			}*/
		}

		t = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				if (balloonAnimationState[poppedBalloon] < 6) {
					balloonAnimationState[poppedBalloon]++;
				}

				if (balloonAnimationState[poppedBalloon] < 5) {
					balloonSprite[poppedBalloon].setTexture(balloon[balloonType[poppedBalloon]][balloonAnimationState[poppedBalloon]]);
				} else if (balloonAnimationState[poppedBalloon] == 6) {
					balloonSprite[poppedBalloon].setAlpha(0);
					t.cancel();
				}
			}
		};
		t.scheduleAtFixedRate(tt,5,35);
	}


	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		font.dispose();

		for (int i = 0; i < balloon.length; i++) {
			for (int j = 0; j < balloon[0].length; j++) {
				balloon[i][j].dispose();
			}
		}
	}

	/* For restarting the game  */
	@Override
	public void resume() {
		//Runtime.getRuntime().freeMemory();
	}

	@Override
	public void pause() {
		Runtime.getRuntime().exit(0);
	}


	private void assignAssets() {
		backgroundPath = "background/bg_" + (r.nextInt(3) + 1) + "_" + (r.nextInt(6) + 1) + ".png";

		background = new Texture(backgroundPath);
		paper_bg_small = new Texture("ui/bg_paper_small.png");

		i_life_full = new Texture("ui/i_life_full.png");
		i_life_empty = new Texture("ui/i_life_empty.png");

		font = new BitmapFont(Gdx.files.internal("ui/font/FAMiW_9W23pwKBYJl_99vL_Y.ttf.fnt"), false);

		balloon[0][0] = new Texture(Gdx.files.internal("balloon/b_1_1.png"));
		balloon[0][1] = new Texture(Gdx.files.internal("balloon/b_1_2.png"));
		balloon[0][2] = new Texture(Gdx.files.internal("balloon/b_1_3.png"));
		balloon[0][3] = new Texture(Gdx.files.internal("balloon/b_1_4.png"));
		balloon[0][4] = new Texture(Gdx.files.internal("balloon/b_1_5.png"));
		balloon[0][5] = new Texture(Gdx.files.internal("balloon/b_1_6.png"));

		balloon[1][0] = new Texture(Gdx.files.internal("balloon/b_2_1.png"));
		balloon[1][1] = new Texture(Gdx.files.internal("balloon/b_2_2.png"));
		balloon[1][2] = new Texture(Gdx.files.internal("balloon/b_2_3.png"));
		balloon[1][3] = new Texture(Gdx.files.internal("balloon/b_2_4.png"));
		balloon[1][4] = new Texture(Gdx.files.internal("balloon/b_2_5.png"));
		balloon[1][5] = new Texture(Gdx.files.internal("balloon/b_2_6.png"));

		balloon[2][0] = new Texture(Gdx.files.internal("balloon/b_3_1.png"));
		balloon[2][1] = new Texture(Gdx.files.internal("balloon/b_3_2.png"));
		balloon[2][2] = new Texture(Gdx.files.internal("balloon/b_3_3.png"));
		balloon[2][3] = new Texture(Gdx.files.internal("balloon/b_3_4.png"));
		balloon[2][4] = new Texture(Gdx.files.internal("balloon/b_3_5.png"));
		balloon[2][5] = new Texture(Gdx.files.internal("balloon/b_3_6.png"));

		balloon[3][0] = new Texture(Gdx.files.internal("balloon/b_4_1.png"));
		balloon[3][1] = new Texture(Gdx.files.internal("balloon/b_4_2.png"));
		balloon[3][2] = new Texture(Gdx.files.internal("balloon/b_4_3.png"));
		balloon[3][3] = new Texture(Gdx.files.internal("balloon/b_4_4.png"));
		balloon[3][4] = new Texture(Gdx.files.internal("balloon/b_4_5.png"));
		balloon[3][5] = new Texture(Gdx.files.internal("balloon/b_4_6.png"));

		balloon[4][0] = new Texture(Gdx.files.internal("balloon/b_5_1.png"));
		balloon[4][1] = new Texture(Gdx.files.internal("balloon/b_5_2.png"));
		balloon[4][2] = new Texture(Gdx.files.internal("balloon/b_5_3.png"));
		balloon[4][3] = new Texture(Gdx.files.internal("balloon/b_5_4.png"));
		balloon[4][4] = new Texture(Gdx.files.internal("balloon/b_5_5.png"));
		balloon[4][5] = new Texture(Gdx.files.internal("balloon/b_5_6.png"));

		balloon[5][1] = new Texture(Gdx.files.internal("balloon/b_6_2.png"));
		balloon[5][0] = new Texture(Gdx.files.internal("balloon/b_6_1.png"));
		balloon[5][2] = new Texture(Gdx.files.internal("balloon/b_6_3.png"));
		balloon[5][3] = new Texture(Gdx.files.internal("balloon/b_6_4.png"));
		balloon[5][4] = new Texture(Gdx.files.internal("balloon/b_6_5.png"));
		balloon[5][5] = new Texture(Gdx.files.internal("balloon/b_6_6.png"));

		for (int i = 0; i < balloonAmount; i++) {
			balloonType[i] = r.nextInt(balloon.length);
			balloonSprite[i] = new Sprite(balloon[balloonType[i]][0]);
		}

		string[0] = new Texture(Gdx.files.internal("balloon/string_1.png"));
		string[1] = new Texture(Gdx.files.internal("balloon/string_2.png"));

		for (int i = 0; i < balloonAmount; i++) {
			stringSprite[i] = new Sprite(string[r.nextInt(2)]); // 2

			/*if (r.nextInt(101) < 50) {
			TODO	stringSprite[i].flip(false, true);
			}*/
		}

		balloonWidth = balloon[0][0].getWidth();
		balloonHeight = balloon[0][0].getHeight();
	}
}