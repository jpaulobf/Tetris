package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import game.pieces.BasePiece;
import game.pieces.NonePiece;
import interfaces.GameInterface;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import util.LoadingStuffs;

/**
 * Class representing the game board
 */
public class Board {

	public final static short BOARD_LINES 			= 20;
	public final static short BOARD_COLUMNS 		= 10;
	public final static short BOARD_TOP	 			= 10;
	public final static short BOARD_LEFT 			= 110;
	public final static short BOARD_BORDER 			= 1;
	public final static short HOLD_BOX_LEFT 		= 10;
	public final static short HOLD_BOX_TOP 			= 20;
	public final static short HOLD_BOX_WIDTH 		= 80;
	public final static short HOLD_BOX_HEIGHT		= 80;
	protected final static short SHADOW_THICKNESS	= 4;
	public final static short INITIAL_SQUARE_X		= 3;
	protected final static short MAX_GAME_LEVEL		= 20;
	protected final static short SPEED_FACTOR		= 2;
	protected final static short MIN_GAME_SPEED		= 44;
	protected final static short MAX_GAME_SPEED		= 20;
	private BufferedImage background         		= null;
    private Graphics2D bg2d             			= null;
	private BufferedImage bgimage					= null;
	private boolean fillColor						= false;

	//Game variables
	private NonePiece nonePiece 					= null;
	protected short boardSmallSquareWidth 			= 0;
	protected short boardSmallSquareHeight 			= 0;
	protected short boardSquareWidth 				= 0;
	protected short boardSquareHeight 				= 0;
	private BasePiece holdPiece						= null;
	private BasePiece actualPiece					= null;
	private BasePiece lastPiece 					= null;
	private short [][] gameBoard					= null;
	private boolean drawPieceGhost					= true;

	//Gameplay variables
	private double TMP								= 1;
	private double gameSpeed						= TMP;
	private long framecounter						= 0;
	private LinkedList<BasePiece> pieceList			= null;
	protected short renderPositionX 				= 100;
	protected short renderPositionY 				= 50;
	protected GameInterface gameRef 				= null;
	protected volatile boolean canRotate			= true;
	protected volatile boolean canHold				= true;
	protected volatile boolean stopped				= false;

	/**
	 * Construtor
	 */
	public Board(GameInterface game) {

		//recovery canvas g2d
		this.gameRef				= game;
		this.nonePiece 				= new NonePiece(this);
		this.pieceList				= new LinkedList<BasePiece>();
		this.boardSmallSquareHeight = (short)(this.nonePiece.getPieceSmallSquareHeight() + 1);
		this.boardSmallSquareWidth	= (short)(this.nonePiece.getPieceSmallSquareWidth() + 1);
		this.boardSquareWidth 		= (short)((this.boardSmallSquareWidth * BOARD_COLUMNS) + BOARD_BORDER + BOARD_BORDER);
		this.boardSquareHeight 		= (short)((this.boardSmallSquareHeight * BOARD_LINES) + BOARD_BORDER + BOARD_BORDER);
		this.gameBoard				= new short[BOARD_LINES][BOARD_COLUMNS];
		this.gameSpeed 				= TMP;//(byte)(MIN_GAME_SPEED - (this.actualLevel * SPEED_FACTOR));

		//define the bg width/height
		int bgwidth 				= BOARD_LEFT + boardSquareWidth + 150;
		int bgheight				= BOARD_TOP + boardSquareHeight + 150;

		//create the bg structure
		this.bgimage				= (BufferedImage)LoadingStuffs.getInstance().getStuff("background");    
		this.background				= GraphicsEnvironment.getLocalGraphicsEnvironment()
														 .getDefaultScreenDevice().getDefaultConfiguration()
														 .createCompatibleImage(bgwidth, bgheight, Transparency.TRANSLUCENT);
		
		//get the G2D (from backbuffered image)
		this.bg2d					= (Graphics2D)this.background.getGraphics();

		//clear the gameboard
		for (short i = 0; i < this.gameBoard.length; i++) {
			for (short j = 0; j < this.gameBoard[i].length; j++) {
				this.gameBoard[i][j] = -1;
			}
		}

		//just one, draw game background
		this.drawBackground(true);
	}

	/**
	 * Update method
	 */
	public void update(long frametime) {
		if (!this.stopped) {
			//add framecounter
			this.framecounter += frametime; 

			//framecounter reach timer or actualPositionY == default
			if ( (this.framecounter >= (1_000_000_000 / this.gameSpeed) ||
				(this.actualPiece.getActualPositionY() == this.actualPiece.getDefaultInitialSquareY())) ) {

				//down one line
				this.actualPiece.downOneLine();

				//reset counter
				this.framecounter = 0;
			}

			if (this.drawPieceGhost) {
				this.actualPiece.updateGhost(frametime);
			}
		}
	}
	
	/**
	 * Draw method
	 * @param frametime
	 */
	public void draw(long frametime) {

		//draw the background and bgimage
		this.getG2D().drawImage(this.bgimage, 0, 0, null);
		this.getG2D().drawImage(this.background, renderPositionX, renderPositionY, null);

		//Draw hold piece
		if (this.holdPiece != null) {
			byte actual = 0;
			this.holdPiece.setPieceActualState(actual);
			this.holdPiece.drawHold(frametime);
		}
		
		//Draw actual piece
		if (this.actualPiece.getActualPositionX() == -10) {
			this.actualPiece.setActualPositionX(INITIAL_SQUARE_X);
		}
		if (this.actualPiece.getActualPositionY() == -10) {
			this.actualPiece.setActualPositionY(this.actualPiece.getDefaultInitialSquareY());
		}
		this.actualPiece.draw(frametime, true);
		
		//draw list of pieces
		BasePiece placeholder = null;
		for (int cnt = 0; this.pieceList != null && cnt < this.pieceList.size(); cnt++) {
			placeholder = this.pieceList.get(cnt);
			placeholder.draw(frametime, false);
		}
		
		//hidde the piece before enter in the board
		// this.graphics2D.setColor(Color.WHITE);
		// this.graphics2D.fillRect(100, 0, 500 , BOARD_TOP);
		// this.graphics2D.setColor(Color.LIGHT_GRAY);
		// this.graphics2D.drawRect(BOARD_LEFT, BOARD_TOP, this.boardSquareWidth, this.boardSquareHeight);
	}

	/**
	 * Draw game background (once).
	 */
	private void drawBackground(boolean filled) {

		//fill the bg in white, case selected not checkered filled
		if (!filled) {
			this.bg2d.setColor(Color.WHITE);
			this.bg2d.fillRect(BOARD_LEFT, BOARD_TOP, this.boardSquareWidth-1, this.boardSquareHeight);
		}

		//Draw the board
		for (short i = 0; i < BOARD_LINES; i++) {
			for (short j = 0; j < BOARD_COLUMNS; j++) {
				if (filled) {
					if (this.fillColor) {
						this.bg2d.setColor(new Color(235, 235, 240));
					} else {
						this.bg2d.setColor(Color.WHITE);
					}

					//exchange color
					this.fillColor = !this.fillColor;

					//in the last column exchange twice
					if (j == (BOARD_COLUMNS - 1)) {
						this.fillColor = !this.fillColor;
					}

					//draw filled rect
					this.bg2d.fillRect(BOARD_LEFT + BOARD_BORDER + (j * this.boardSmallSquareWidth), 
									   BOARD_TOP + BOARD_BORDER + (i * this.boardSmallSquareHeight), 
									   this.boardSmallSquareWidth, 
									   this.boardSmallSquareHeight);	
				} else {
					this.bg2d.setColor(Color.WHITE);

					//draw just the rect (not filled)
					this.bg2d.drawRect(BOARD_LEFT + BOARD_BORDER + (j * this.boardSmallSquareWidth), 
									   BOARD_TOP + BOARD_BORDER + (i * this.boardSmallSquareHeight), 
									   this.boardSmallSquareWidth, 
									   this.boardSmallSquareHeight);	
				}
			}	
		}
		
		//draw lines & shadows
		this.bg2d.setColor(Color.LIGHT_GRAY);
		this.bg2d.drawRect(BOARD_LEFT, BOARD_TOP, this.boardSquareWidth-1, this.boardSquareHeight);
		this.bg2d.setColor(Color.DARK_GRAY);
		this.bg2d.fillRect(BOARD_LEFT + this.boardSquareWidth, BOARD_TOP + SHADOW_THICKNESS, SHADOW_THICKNESS, this.boardSquareHeight);
		this.bg2d.setColor(Color.DARK_GRAY);
		this.bg2d.fillRect(BOARD_LEFT + SHADOW_THICKNESS, BOARD_TOP + this.boardSquareHeight + 1, this.boardSquareWidth, SHADOW_THICKNESS);
		
		//Draw hold box
		this.bg2d.setColor(Color.DARK_GRAY);
		this.bg2d.drawString("HOLD", 33, 10);
		this.bg2d.setColor(Color.LIGHT_GRAY);
		this.bg2d.drawRect(HOLD_BOX_LEFT, HOLD_BOX_TOP - 8, HOLD_BOX_WIDTH, HOLD_BOX_HEIGHT);
		this.bg2d.setColor(Color.WHITE);
		this.bg2d.fillRect(HOLD_BOX_LEFT + 1, HOLD_BOX_TOP - 8 + 1, HOLD_BOX_WIDTH - 1, HOLD_BOX_HEIGHT - 1);
		this.bg2d.setColor(Color.DARK_GRAY);
		this.bg2d.fillRect(HOLD_BOX_LEFT + HOLD_BOX_WIDTH, HOLD_BOX_TOP - 8 + SHADOW_THICKNESS, SHADOW_THICKNESS, HOLD_BOX_HEIGHT);
		this.bg2d.fillRect(HOLD_BOX_LEFT + SHADOW_THICKNESS, HOLD_BOX_TOP - 8 + HOLD_BOX_HEIGHT, HOLD_BOX_WIDTH, SHADOW_THICKNESS);

		//Next boxes
		final short nextPosX = (short)(BOARD_LEFT + boardSquareWidth + 20);
		this.bg2d.setColor(Color.DARK_GRAY);
		this.bg2d.drawString("NEXT", nextPosX + 35, 10);
		for (byte i = 0; i < 6; i++) {
			short nextPosY = (short)((i * 100) - 8);
			this.bg2d.setColor(Color.LIGHT_GRAY);
			this.bg2d.drawRect(HOLD_BOX_LEFT + nextPosX, HOLD_BOX_TOP + nextPosY, HOLD_BOX_WIDTH, HOLD_BOX_HEIGHT);
			this.bg2d.setColor(Color.WHITE);
			this.bg2d.fillRect(HOLD_BOX_LEFT + 1 + nextPosX, HOLD_BOX_TOP + nextPosY + 1, HOLD_BOX_WIDTH - 1, HOLD_BOX_HEIGHT - 1);
			this.bg2d.setColor(Color.DARK_GRAY);
			this.bg2d.fillRect(HOLD_BOX_LEFT + HOLD_BOX_WIDTH + nextPosX, HOLD_BOX_TOP + nextPosY + SHADOW_THICKNESS, SHADOW_THICKNESS, HOLD_BOX_HEIGHT);
			this.bg2d.fillRect(HOLD_BOX_LEFT + SHADOW_THICKNESS + nextPosX, HOLD_BOX_TOP + nextPosY + HOLD_BOX_HEIGHT, HOLD_BOX_WIDTH, SHADOW_THICKNESS);
		}
	}

	/**
	 * Move the game
	 * @param keyCode
	 */
	public synchronized void move(int keyCode) {
		if (keyCode == 39) {
			this.getActualPiece().moveRight();
		} else if (keyCode == 37) {
			this.getActualPiece().moveLeft();
		} else if (keyCode == 38) {
			if (this.canRotate) {
				this.getActualPiece().rotateRight();
				this.canRotate = false;
			}
		} else if (keyCode == 40) {
			this.getActualPiece().downOneLine();
		} else if (keyCode == 32) {
			this.holdPiece();
		}
	}
	
	/**
	 * Hold piece method
	 */
	public void holdPiece() {
		if (this.canHold) {
			//no piece holding
			if (this.holdPiece == null) {
				this.holdPiece = this.actualPiece;
				this.canHold = false;
				this.sortPiece();
			} else {
				//there is a piece, so exchange
				BasePiece temp = this.actualPiece;
				this.holdPiece.reset();
				this.actualPiece = this.holdPiece;
				this.holdPiece = temp;
				this.canHold = false;
			}
		}
	}

	/**
	 * Sort a new piece
	 */
	public void sortPiece() {
		this.actualPiece 	= this.nonePiece.sortNextPiece(this, this.actualPiece, this.lastPiece);
		this.lastPiece 		= this.actualPiece;
		this.canHold 		= true;
	}
	
	/**
	 * Reset game method
	 */
	public synchronized void resetGame() {
		
		//Clear the stored piece list
		if (this.pieceList != null) {
			this.pieceList.clear();
			this.pieceList = null;
		}

		//re-initialize the variables
		this.nonePiece 				= new NonePiece(this);
		this.pieceList				= new LinkedList<BasePiece>();
		this.boardSmallSquareHeight = (short)(this.nonePiece.getPieceSmallSquareHeight() + 1);
		this.boardSmallSquareWidth	= (short)(this.nonePiece.getPieceSmallSquareWidth() + 1);
		this.boardSquareWidth 		= (short)((this.boardSmallSquareWidth * BOARD_COLUMNS) + BOARD_BORDER + BOARD_BORDER);
		this.boardSquareHeight 		= (short)((this.boardSmallSquareHeight * BOARD_LINES) + BOARD_BORDER + BOARD_BORDER);
		this.gameSpeed 				= TMP;//(short)(MIN_GAME_SPEED - (this.actualLevel * SPEED_FACTOR));
		this.gameBoard				= new short[BOARD_LINES][BOARD_COLUMNS];

		//clear the hold, last & actual piece
		this.lastPiece 				= null;
		this.holdPiece 				= null;
		this.actualPiece 			= null;
		
		//Clear the gameboard
		for (int cnt = 0; cnt < this.gameBoard.length; cnt++) {
			for (int cnt2 = 0; cnt2 < this.gameBoard[cnt].length; cnt2++) {
				this.gameBoard[cnt][cnt2] = -1;
			}
		}
		
		//Sort a new piece
		this.sortPiece();
	}
	
	/**
	 * Verify if can move to the right
	 * @return
	 */
	public synchronized boolean canMoveRight() {
		
		//first, check if the piece isn't touching the wall
		int assetRight 		= this.actualPiece.getAssetsRight();
		int finalPosition	= this.actualPiece.getActualPositionX() + assetRight;

		//Next test colision with others pieces
		if (finalPosition < Board.BOARD_COLUMNS) {
			
			int startPositionY 		= this.actualPiece.getActualBottonPosition() - this.actualPiece.getPieceSquareHeight();
			int startPositionX 		= 0;
			int assetLeft 			= this.actualPiece.getAssetsLeft();
			startPositionX 			= this.actualPiece.getActualPositionX() - assetLeft;
			short [][] matrix 		= this.actualPiece.getPieceMatrix();
			
			for (int cnt = 0; cnt < matrix.length; cnt++) {
				int yPosition = startPositionY + matrix[cnt][0];
				int xPosition = startPositionX + matrix[cnt][1] + assetLeft + 1;

				if (yPosition >= 0 && this.gameBoard[yPosition][xPosition] == 1) {
					return (false);
				}
			}
		} else {
			return false;
		}
		
		//if reach here, isn't touching anything
		return (true);
	}
	
	/**
	 * Verify if can move to the left
	 * @return
	 */
	public synchronized boolean canMoveLeft() {
		
		int startPositionY 		= this.actualPiece.getActualBottonPosition() - this.actualPiece.getPieceSquareHeight();
		int startPositionX 		= 0;
		int assetLeft 			= this.actualPiece.getAssetsLeft();
		startPositionX 			= this.actualPiece.getActualPositionX() - assetLeft; //Corrige o asset
		short [][] matrix 		= this.actualPiece.getPieceMatrix();
		
		for (int cnt = 0; cnt < matrix.length; cnt++) {
			int yPosition = startPositionY + matrix[cnt][0];
			int xPosition = startPositionX + matrix[cnt][1] + assetLeft - 1;
			
			if (yPosition >= 0 && xPosition >= 0 && this.gameBoard[yPosition][xPosition] == 1) {
				return (false);
			}
		}
		
		//if reach here, isn't touching anything
		return (true);
	}
	
	/**
	 * Verify if can move down
	 * @return
	 */
	public synchronized boolean canMoveDown() {
		//if reach the base, return
		if (this.actualPiece.getActualBottonPosition() == BOARD_LINES) {
			this.pieceList.add(this.actualPiece);
			this.storePiecePosition(this.actualPiece);
			return (false);
		} else {
			int startPositionY 		= this.actualPiece.getActualBottonPosition() - this.actualPiece.getPieceSquareHeight();
			int startPositionX 		= 0;
			int assetLeft 			= this.actualPiece.getAssetsLeft();
			startPositionX 			= this.actualPiece.getActualPositionX() - assetLeft; //Corrige o asset
			short [][] matrix 		= this.actualPiece.getPieceMatrix();
			
			//check colision with others pieces
			for (int cnt = 0; cnt < matrix.length; cnt++) {
				int yPosition = startPositionY + matrix[cnt][0] + 1;
				int xPosition = startPositionX + matrix[cnt][1] + assetLeft;
				if (yPosition > -1 && this.gameBoard[yPosition][xPosition] == 1) {
					this.pieceList.add(this.actualPiece);
					this.storePiecePosition(this.actualPiece);
					return (false);
				}
			}
		}
		
		//if reach here, isn't touching anything
		return (true);
	}
	
	/**
	 * Verify if can rotate to the right
	 * @return
	 * TODO: LPiece is buggy
	 */
	public boolean canRotateRight() {
		byte nextState			= this.actualPiece.getPieceNextState();
		short [][] matrix 		= this.actualPiece.getNextStatePieceMatrix();

		//first test against the left wall
		int assetLeft 			= this.actualPiece.getAssetsLeft(nextState);
		int piecePositionX 		= this.actualPiece.getActualPositionX();
				
		if (assetLeft == -1) {
			if (piecePositionX < -1) {
				piecePositionX = -1;
			}
		} else {
			if (piecePositionX < 0) {
				piecePositionX = 0;
			}	
		}
		
		//now with the right wall
		int assetRight = this.actualPiece.getAssetsRight();
		int finalPositionX = piecePositionX + assetRight;
		if (finalPositionX > Board.BOARD_COLUMNS) {
			piecePositionX--;
		}
		
		//get actual Y position
		int startPositionY = this.actualPiece.getActualBottonPosition(nextState) - this.actualPiece.getPieceSquareHeight(nextState);
		
		//get actual X position
		int startPositionX = piecePositionX - assetLeft; //Corrige o asset

		//test against all pieces
		for (int cnt = 0; cnt < matrix.length; cnt++) {
			int yPosition = startPositionY + matrix[cnt][0];
			int xPosition = startPositionX + matrix[cnt][1] + assetLeft;
			
			if (yPosition >= 0 &&
				xPosition >= 0 &&
				yPosition < BOARD_LINES &&
				xPosition < BOARD_COLUMNS &&
				this.gameBoard[yPosition][xPosition] == 1) {
				return (false);
			}
		}
		
		//if reach here, isn't touching anything
		return (true);
	}
	
	/**
	 * Store the piece position
	 * @param piece
	 */
	public void storePiecePosition(BasePiece piece) {
		
		//recover Y position
		int startPositionY 	= piece.getActualBottonPosition() - piece.getPieceSquareHeight();

		//recover X position
		int startPositionX 	= 0;
		int assetLeft 		= piece.getAssetsLeft();
		startPositionX 		= piece.getActualPositionX() - assetLeft; //Corrige o asset
		short [][] matrix 	= piece.getPieceMatrix();

		for (int cnt = 0; cnt < matrix.length; cnt++) {
			
			//--- TODO: gameover...
			if ((startPositionY + matrix[cnt][0]) >= BOARD_LINES || ((startPositionY + matrix[cnt][0]) < 0)) {
				this.resetGame();
				break;
			}
			
			this.gameBoard[startPositionY + matrix[cnt][0]]
						  [startPositionX + matrix[cnt][1] + assetLeft] = 1;	
		}
	}

	/**
	 * Toogle game pause
	 */
	public void tooglePause() {
		this.stopped = !this.stopped;
	}

	//Getters/setters
	public BasePiece getActualPiece() 			{	return (this.actualPiece);				}
	public short getBoardSmallSquareWidth() 	{	return (this.boardSmallSquareWidth);	}
	public short getBoardSmallSquareHeight() 	{	return (this.boardSmallSquareHeight);	}
	public short[][] getGameBoard() 			{	return (this.gameBoard);				}
	public boolean isToDrawGhost() 				{	return (this.drawPieceGhost);			}
	public short getRenderPositionX() 			{	return (this.renderPositionX);			}
	public short getRenderPositionY() 			{ 	return (this.renderPositionY);			}
	public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());			}
}