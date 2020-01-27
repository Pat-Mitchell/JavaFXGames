package application;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
//@formatter:off
	/*
	 * Patrick Mitchell
	 * 
	 * Is PONG. Does pong things. Slightly modified from a tutorial video
	 * 
	 * 01/25/2020 
	 *  -Watched the tutorial
	 * 
	 * 01/26/2020 Added better paddle physics
	 *  -Changed ball speed to double (was integer)
	 *  -This probably creates subpixals which is not a topic worth discussing
	 *  -Changed how Y speed is calculated when hitting a paddle
	 *  -Hitting a wall is unchanged
	 *  -Changed some formatting too for that visual beauty in the source code
	 *  -Source code beauty attracts all the 10/10 plumber babes of the 1s and 0s
	 * 
	 * 01/27/2020 
	 *  -Added a max speed 
	 *  -Added "powerup"
	 *  -Player turns red on Mouse Click
	 *  -Hitting the ball while red increases its X speed
	 *  -Player returns to normal once the ball is hit 
	 *  -ignores speed limit
	 *  -Comp hitting the ball lowers X speed down to limit if it was over
	 * 
	 * Cheers!
	 */
	private static final int 	width = 800, //window width
					height = 600, //window height
					playerHeight = 100, //player paddle height
					playerWidth = 15; //player paddle width
	private static final double 	ballR = 15; //ball radius
	private int 			scoreP1 = 0, //This should be maxed out
					scoreP2 = 0, //This should be as low as possible
					player1XPos = 0, //Left part of the window
					powerSpeed = 5; //Powerup Speed increase
	private double			ballYSpeed = 1,  //Up speed
					ballXSpeed = 1, //right speed
					player1YPos = height / 2, //Start in the middle of the window
					player2YPos = height / 2, //Same as above
					ballXPos = width / 2, //Start ball in the middle of the screen
					ballYPos = width /2, //More start ball in the middle of the screen
					player2XPos = width - playerWidth, //Right part of the screen - the width
					maxSpeed = 10.0; //speedLimit
	private boolean			gameStarted, //is game start. No.
					mouseClicked; //Super Saiyan mode!
	//@formatter:on	

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("PONG"); // Such title. Wow!
		Canvas canvas = new Canvas(width, height); // Window is of dimensions w and h
		GraphicsContext gc = canvas.getGraphicsContext2D(); // 2D, the superior D
		Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc))); // Lambda functions!
		tl.setCycleCount(Timeline.INDEFINITE); // Don't stop. EVER!
		// Mouse control
		canvas.setOnMouseMoved(e -> player1YPos = e.getY()); // Get Y pos of Mouse on screen
		canvas.setOnMouseClicked(e -> {
			if (gameStarted)
				mouseClicked = true; // On Mouse click
			gameStarted = true;
		});
		stage.setScene(new Scene(new StackPane(canvas))); // Set up the stage
		stage.show(); // Turn on the lights
		tl.play(); // Get freaky!!!
	}

	private void run(GraphicsContext gc) {
		// Background color. Black to avoid attracting bugs.
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width, height);
		// set text color. Light to attract good bugs
		gc.setFill(Color.WHITE);
		gc.setFont(Font.font(25));

		if (gameStarted) {
			// set ball movement
			ballXPos += ballXSpeed;
			ballYPos += ballYSpeed;
			// computer movement
			if (ballXPos < width - width / 4) { // Behavior close to Ai paddle
				player2YPos = ballYPos - playerHeight / 2;
			} else { // Behavior from further away
				player2YPos = ballYPos > player2YPos + playerHeight / 2 ? player2YPos += 1 : player2YPos - 1;
			}
			// draw ball
			gc.fillOval(ballXPos, ballYPos, ballR, ballR);
		} else {
			// Set start text
			gc.setStroke(Color.WHITE);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.strokeText("on Click", width / 2, height / 2);
			// Reset start position
			ballXPos = width / 2;
			ballYPos = height / 2;
			// reset speed & direction
			ballXSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
			ballYSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
		}
		// Ball! Don't leave me!
		if (ballYPos > height || ballYPos < 0)
			ballYSpeed *= -1;
		// Ai gains superiority point
		if (ballXPos < player1XPos - playerWidth) {
			scoreP2++;
			gameStarted = false;
			mouseClicked = false;
		}
		// Humanity gains a superiority point
		if (ballXPos > player2XPos + playerWidth) {
			scoreP1++;
			gameStarted = false;
			mouseClicked = false;
		}
		//@formatter:off
		//Faster! FASTER!!! (increase ball speed)
		//Basically, switch ball x & y direction and make it faster when the ball is hit.
		//Probs could make this faster since the check to see if the ball is hit is made every frame
		//Bad practice too. Copy and pasted code for the power up thing
		//Blame feature creep.
		if(( 		mouseClicked //If powered up and player hits
		  		&& ballXPos < player1XPos + playerWidth) 
		  		&& ballYPos >= player1YPos						
		  		&& ballYPos <= player1YPos + playerHeight) {
			ballXSpeed += (powerSpeed * Math.signum(ballXSpeed));
			mouseClicked = false;
			ballXSpeed *= -1;
			ballYSpeed = (ballXPos < (width / 2) ?        //Change ball Y direction based on 
					4 * ((ballYPos - (player1YPos + playerHeight * .5)) / 50) : //where the paddle hits
					4 * ((ballYPos - (player2YPos + playerHeight * .5)) / 50));
		}
		else if 	(((ballXPos + ballR > player2XPos) 		//is ball yeeted to computer
				&& ballYPos >= player2YPos 			//and ball below comp head
				&& ballYPos <= player2YPos + playerHeight)	//and ball above comp foot
				|| ((ballXPos < player1XPos + playerWidth) 	//or ball yeeted to player
				&& ballYPos >= player1YPos			//and ball below player head
				&& ballYPos <= player1YPos + playerHeight)) {	//and ball above player foot
			ballXSpeed += (Math.abs(ballXSpeed) < maxSpeed) ? 1 * Math.signum(ballXSpeed) : 0 ; // Speed limit
			if(ballXSpeed > 10) ballXSpeed = 10;
			ballXSpeed *= -1;
			ballYSpeed = (ballXPos < (width / 2) ?        //Change ball Y direction based on 
					4 * ((ballYPos - (player1YPos + playerHeight * .5)) / 50) : //where the paddle hits
					4 * ((ballYPos - (player2YPos + playerHeight * .5)) / 50));
		}		
		//@formatter:on

		// Draw score
		gc.fillText(scoreP1 + "\t\t\t\t\t\t\t\t" + scoreP2, width / 2, 100);
		// Ready player 1 & 2
		gc.setFill(Color.WHITE); // Set color to white. Completely unnecessary for the most part
		gc.fillRect(player2XPos, player2YPos, playerWidth, playerHeight);
		if (mouseClicked) // If you "power up," draw the player red
			gc.setFill(Color.RED);
		gc.fillRect(player1XPos, player1YPos, playerWidth, playerHeight);
	}
}
