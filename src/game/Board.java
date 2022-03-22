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
import java.awt.Rectangle;

/**
 * Class representing the game board
 */
public class Board {

	public final static byte BOARD_LINES 			= 20;
	public final static byte BOARD_COLUMNS 			= 10;
	public final static byte BOARD_TOP	 			= 10;
	public final static short BOARD_LEFT 			= 110;
	public final static short BOARD_BORDER 			= 1;
	public final static byte HOLD_BOX_LEFT 			= 10;
	public final static byte HOLD_BOX_TOP 			= 20;
	public final static byte HOLD_BOX_WIDTH 		= 80;
	public final static byte HOLD_BOX_HEIGHT		= 80;
	public final static short SORTED_BOX_LEFT		= 451;
	public final static short SORTED_BOX_TOP		= 20;
	protected final static byte SHADOW_THICKNESS	= 4;
	public final static byte INITIAL_SQUARE_X		= 3;
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
	private LinkedList<BasePiece> nextPieces		= new LinkedList<BasePiece>();
	private short [][] gameBoard					= null;
	private Color [][] gameBoardColor				= null;
	private boolean drawPieceGhost					= true;

	//Gameplay variables
	private double TMP								= 1;
	private double gameSpeed						= TMP;
	private long framecounter						= 0;
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
		this.boardSmallSquareHeight = (short)(this.nonePiece.getPieceSmallSquareHeight() + 1);
		this.boardSmallSquareWidth	= (short)(this.nonePiece.getPieceSmallSquareWidth() + 1);
		this.boardSquareWidth 		= (short)((this.boardSmallSquareWidth * BOARD_COLUMNS) + BOARD_BORDER + BOARD_BORDER);
		this.boardSquareHeight 		= (short)((this.boardSmallSquareHeight * BOARD_LINES) + BOARD_BORDER + BOARD_BORDER);
		this.gameBoard				= new short[BOARD_LINES][BOARD_COLUMNS];
		this.gameBoardColor			= new Color[BOARD_LINES][BOARD_COLUMNS];
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

			if (this.drawPieceGhost) {
				this.actualPiece.updateGhost(frametime);
			}

			//framecounter reach timer or actualPositionY == default
			if ( (this.framecounter >= (1_000_000_000 / this.gameSpeed) ||
				(this.actualPiece.getActualPositionY() == this.actualPiece.getDefaultInitialSquareY())) ) {

				//down one line
				this.actualPiece.downOneLine();

				//reset counter
				this.framecounter = 0;

				//check if at least one line is complete
				this.checkLineClear();
			}
		}
	}

	public void checkLineClear() {

		byte testValue 					= 0;
		boolean hasLineFull 			= false;
		byte maxLines					= 4;
		byte firstline					= -1;
		byte linesToEnd					= -1;
		byte sumValue					= 0;
		short [][] tempGameBoardP1 		= null;
		short [][] tempGameBoardP2 		= null;
		Color [][] tempGameBoardColorP1 = null;
		Color [][] tempGameBoardColorP2 = null;
		//based on the sum of clear lines, how much lines to add at top (starting from 0 to 10)
		final byte [] linesToAdd 		= {0, 1, 0, 2, 2, 2, 3, 3, 3, 0, 4};

		//iterate through all lines in the board looking for lines to clear (completed)
		for (byte linhas = 0; this.gameBoard != null && linhas < this.gameBoard.length; linhas++) {

			hasLineFull = false;
			testValue = 0;

			for (byte colunas = 0; colunas < gameBoard[linhas].length; colunas++) { 
				testValue += gameBoard[linhas][colunas];
			}

			//I found one line full
			if (testValue == BOARD_COLUMNS) {
				hasLineFull = true;
				firstline = linhas;
				sumValue = 1;

				//test next 3 lines (or less depending from the 1st full line found)
				//first calc maxLines until bottom
				//(sum 1, because the 0 index)
				linesToEnd = (byte)(BOARD_LINES - (firstline + 1));
				if (linesToEnd > 0) {
					if (linesToEnd > 3) {
						maxLines = 3;
					} else {
						maxLines = linesToEnd;
					}

					/**
					 * Possibles results (sums):
					 * 1 (1)
					 * 1, 2 (3)
					 * 1, 3 (4)
					 * 1, 4 (5)
					 * 1, 2, 3 (6)
					 * 1, 2, 4 (7)
					 * 1, 3, 4 (8)
					 * 1, 2, 3, 4 (tetris) (10)
					 */
					for (byte i = 1; i < (maxLines + 1); i++) {
						testValue = 0;						
						for (byte colunas = 0; colunas < gameBoard[firstline + i].length; colunas++) { 
							testValue += gameBoard[firstline + i][colunas];
						}
						if (testValue == BOARD_COLUMNS) {
							sumValue += (i + 1);
						}
					}
				}
			}

			if (hasLineFull) {

				System.out.println("sumValue: " + sumValue);
				
				//continuous
				if (sumValue == 1 || sumValue == 3 || sumValue == 6 || sumValue == 10) {

					//Add on top, n white lines, with the exactly same number of lines, that will be dropped in bottom part
					tempGameBoardP1 		= new short[(firstline + linesToAdd[sumValue])][BOARD_COLUMNS];
					tempGameBoardColorP1 	= new Color[(firstline + linesToAdd[sumValue])][BOARD_COLUMNS];
					for (byte i = 0; i < linesToAdd[sumValue]; i++) {
						for (byte j = 0; j < BOARD_COLUMNS; j++) {
							tempGameBoardP1[i][j] 		= -1;
							tempGameBoardColorP1[i][j] 	= null;
						}
					}

					//create the "second" part of board, ignoring the dropped lines
					tempGameBoardP2 		= new short[BOARD_LINES - tempGameBoardP1.length][BOARD_COLUMNS];
					tempGameBoardColorP2 	= new Color[BOARD_LINES - tempGameBoardP1.length][BOARD_COLUMNS];

					//Now copy both parts, then join them...
					for (byte i = linesToAdd[sumValue]; i < (firstline + linesToAdd[sumValue]); i++) {
						tempGameBoardP1[i] = gameBoard[i - linesToAdd[sumValue]];
						tempGameBoardColorP1[i] = gameBoardColor[i - linesToAdd[sumValue]];
					}

					//TODO: BUGGY... WIP
					for (byte i = (byte)(firstline + linesToAdd[sumValue]); i < BOARD_LINES; i++) {
						tempGameBoardP2[i] = gameBoard[i];
						tempGameBoardColorP2[i] = gameBoardColor[i];
					}

					this.gameBoard = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardP1), 
																	java.util.Arrays.stream(tempGameBoardP2)).toArray(short[][]::new));

					this.gameBoardColor = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardColorP1), 
																		java.util.Arrays.stream(tempGameBoardColorP2)).toArray(Color[][]::new));

					tempGameBoardP1 = null;
					tempGameBoardP2 = null;
					tempGameBoardColorP1 = null;
					tempGameBoardColorP2 = null;


					//System.out.println(tempGameBoardP1.length);
					//System.out.println(tempGameBoardP2.length);

					/*
					//first line down
					for (byte i = 0; i < BOARD_COLUMNS; i++) {
						tempGameBoardP1[0][i] = -1;
						tempGameBoardColorP1[0][i] = null;
					}

					for (byte i = 0; i < (linhas); i++) {
						tempGameBoardP1[i + 1] = gameBoard[i];
						tempGameBoardColorP1[i + 1] = gameBoardColor[i];
					}

					for (byte i = 0; i < BOARD_LINES - (linhas + 1); i++) {
						tempGameBoardP2[i] = gameBoard[linhas + i + 1];
						tempGameBoardColorP2[i] = gameBoardColor[linhas + i + 1];
					}

					this.gameBoard = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardP1), 
																	java.util.Arrays.stream(tempGameBoardP2)).toArray(short[][]::new));

					this.gameBoardColor = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardColorP1), 
																		java.util.Arrays.stream(tempGameBoardColorP2)).toArray(Color[][]::new));

					tempGameBoardP1 = null;
					tempGameBoardP2 = null;
					tempGameBoardColorP1 = null;
					tempGameBoardColorP2 = null;


					*/



				} else if (sumValue == 4 || sumValue == 5) {

				} else if (sumValue == 7 || sumValue == 8) {

				}

				System.out.println("outing....");
				break;

			}

			

			/*

			//we have a full line - drop it.
			if (hasLineFull) {
				
				//count the number of droped lines, to point (WIP)
				tempGameBoardP1	 		= new short[(linhas + 1)][BOARD_COLUMNS];
				tempGameBoardP2 		= new short[BOARD_LINES - (linhas + 1)][BOARD_COLUMNS];
				tempGameBoardColorP1 	= new Color[(linhas + 1)][BOARD_COLUMNS];
				tempGameBoardColorP2 	= new Color[BOARD_LINES - (linhas + 1)][BOARD_COLUMNS];

				//first line down
				for (byte i = 0; i < BOARD_COLUMNS; i++) {
					tempGameBoardP1[0][i] = -1;
					tempGameBoardColorP1[0][i] = null;
				}

				for (byte i = 0; i < (linhas); i++) {
					tempGameBoardP1[i + 1] = gameBoard[i];
					tempGameBoardColorP1[i + 1] = gameBoardColor[i];
				}

				for (byte i = 0; i < BOARD_LINES - (linhas + 1); i++) {
					tempGameBoardP2[i] = gameBoard[linhas + i + 1];
					tempGameBoardColorP2[i] = gameBoardColor[linhas + i + 1];
				}

				this.gameBoard = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardP1), 
																 java.util.Arrays.stream(tempGameBoardP2)).toArray(short[][]::new));

				this.gameBoardColor = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardColorP1), 
																  	  java.util.Arrays.stream(tempGameBoardColorP2)).toArray(Color[][]::new));

				tempGameBoardP1 = null;
				tempGameBoardP2 = null;
				tempGameBoardColorP1 = null;
				tempGameBoardColorP2 = null;
			}
			*/
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
		
		//draw list of sorted pieces
		BasePiece nextPiece = null;
		for (byte cnt = 0; this.nextPieces != null && cnt < this.nextPieces.size(); cnt++) {
			nextPiece = this.nextPieces.get(cnt);
			nextPiece.drawNext(frametime, cnt);
		}

		//draw bag of pieces
		this.drawBoardPieces();
	}

	/**
	 * Draw the pieces in the board
	 */
	private void drawBoardPieces() {
		for (int linhas = 0; this.gameBoard != null && linhas < this.gameBoard.length; linhas++) {
			for (int colunas = 0; colunas < gameBoard[linhas].length; colunas++) { 
				if (gameBoard[linhas][colunas] == 1) {
					//calc square by square the position 
					int calcPosX = Board.BOARD_LEFT + Board.BOARD_BORDER + (colunas * (BasePiece.SMALL_SQUARE_WIDTH))  + colunas + renderPositionX;
					int calcPosY = Board.BOARD_TOP  + Board.BOARD_BORDER + (linhas  * (BasePiece.SMALL_SQUARE_HEIGHT)) + linhas  + renderPositionY;

					//draw the flat rect
					this.getG2D().setColor(gameBoardColor[linhas][colunas]);
					this.getG2D().fill(new Rectangle(calcPosX, calcPosY, BasePiece.SMALL_SQUARE_WIDTH, BasePiece.SMALL_SQUARE_HEIGHT));
					
					//draw the bevel effect
					this.getG2D().setColor(Color.WHITE);
					this.getG2D().fill(new Rectangle(calcPosX, calcPosY, 1, BasePiece.SMALL_SQUARE_HEIGHT));
					this.getG2D().fill(new Rectangle(calcPosX, calcPosY, BasePiece.SMALL_SQUARE_WIDTH, 1));
					
					//draw the shadow
					this.getG2D().setColor(Color.DARK_GRAY);
					this.getG2D().fill(new Rectangle(calcPosX + BasePiece.SMALL_SQUARE_WIDTH, calcPosY, 1, BasePiece.SMALL_SQUARE_HEIGHT));
					this.getG2D().fill(new Rectangle(calcPosX, calcPosY + BasePiece.SMALL_SQUARE_HEIGHT, BasePiece.SMALL_SQUARE_WIDTH + 1, 1));
				}
			}
		}
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
		if (!this.stopped) {
			if (keyCode == 39) { //right
				this.getActualPiece().moveRight();
			} else if (keyCode == 37) { //left
				this.getActualPiece().moveLeft();
			} else if (keyCode == 38) { //up
				if (this.canRotate) {
					this.getActualPiece().rotateRight();
					this.canRotate = false;
				}
			} else if (keyCode == 40) { //down
				this.getActualPiece().downOneLine();
			} else if (keyCode == 17) { // r-control
				this.holdPiece();
			} else if (keyCode == 32) { //space
				this.getActualPiece().allDown();
			}
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
				this.sortPiecesList();
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
	 * Sort the list of pieces
	 */
	public void sortPiecesList() {

		BasePiece temp = null;

		//The list is complete
		if (this.nextPieces.size() == 6) {
			//recover last piece
			this.lastPiece = this.nextPieces.getLast();

			//sort & add the next one
			temp = this.nonePiece.sortNextPiece(this, temp, this.lastPiece);
			this.nextPieces.add(temp);

			//get actual piece
			this.actualPiece = this.nextPieces.removeFirst();
		} else {
			//clear the list
			this.nextPieces.clear();

			//sort 7 pieces
			for (byte cnt = 0; cnt < 7; cnt++) {
				temp = this.nonePiece.sortNextPiece(this, temp, this.lastPiece);
				this.lastPiece = temp;
				this.nextPieces.add(temp);
			}

			//store the last & get the first
			this.lastPiece = this.nextPieces.getLast();
			this.actualPiece = this.nextPieces.removeFirst();
		}

		//allow hold
		this.canHold = true;
	}
	
	/**
	 * Reset game method
	 */
	public synchronized void resetGame() {
		
		//re-initialize the variables
		this.nonePiece 				= new NonePiece(this);
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
		this.sortPiecesList();
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
			//the piece is touching right wall
			return (false);
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
			this.gameBoardColor[startPositionY + matrix[cnt][0]]
							   [startPositionX + matrix[cnt][1] + assetLeft] = piece.getColor();
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