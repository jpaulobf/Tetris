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
import util.Audio;

/**
 * Class representing the game board
 */
public class Board {

	//constants
	public final static byte BOARD_LINES 			= 20;
	public final static byte BOARD_COLUMNS 			= 10;
	public final static byte BOARD_TOP	 			= 10;
	public final static short BOARD_LEFT 			= 110;
	public final static short BOARD_BORDER 			= 1;
	public final static byte HOLD_BOX_LEFT 			= 10;
	public final static byte HOLD_BOX_TOP 			= 48;
	public final static byte HOLD_BOX_WIDTH 		= 80;
	public final static byte HOLD_BOX_HEIGHT		= 80;
	public final static short SORTED_BOX_LEFT		= 451;
	public final static short SORTED_BOX_TOP		= 20;
	protected final static byte SHADOW_THICKNESS	= 4;
	public final static byte INITIAL_SQUARE_X		= 3;
	
	//images
	private BufferedImage gameBoardBG         		= null;
	private BufferedImage next         				= null;
	private BufferedImage hold         				= null;
	private BufferedImage score         			= null;
	private BufferedImage hiscore 					= null;
	private BufferedImage labelLevel				= null;
	private BufferedImage labelLine					= null;
	private BufferedImage circle 					= null;

	//theme
    private Graphics2D bg2d             			= null;
	private boolean fillColor						= false;
	private Theme theme								= null;
	private byte defaultTheme						= 0;

	//Game variables
	private LinkedList<BasePiece> nextPieces		= new LinkedList<BasePiece>();
	private NonePiece nonePiece 					= null;
	private BasePiece holdPiece						= null;
	private BasePiece actualPiece					= null;
	private BasePiece lastPiece 					= null;
	protected short boardSmallSquareWidth 			= 0;
	protected short boardSmallSquareHeight 			= 0;
	protected short boardSquareWidth 				= 0;
	protected short boardSquareHeight 				= 0;
	private short [][] gameBoard					= null;
	private Color [][] gameBoardColor				= null;
	private Audio turn            					= null;
	private Audio move            					= null;
	private Audio splash          					= null;
	private Audio drop          					= null;
	private Audio tetris          					= null;
	private Audio holdSound							= null;

	//Gameplay variables
	private long framecounter						= 0;
	protected short renderPositionX 				= 0;
	protected short renderPositionY 				= 0;
	protected Game gameRef 							= null;
	protected volatile boolean canRotate			= true;
	protected volatile boolean canHold				= true;
	protected volatile boolean stopped				= false;
	private volatile byte currentLevel             	= 1;
	private volatile short linesCleared				= 0;

	//Variables to control lines to drop
	protected volatile boolean hasLineFull			= false;
	protected volatile byte firstline				= -1;

	//allow ghost/hold
	private boolean allowGhostPiece					= true;
	private boolean allowHold						= true;
	private byte showHowManyNext					= -1;

	/**
	 * Construtor
	 */
	public Board(GameInterface game) {

		//parent object
		this.gameRef				= (Game)game;

		//base piece
		this.nonePiece 				= new NonePiece(this);
		
		//base measures
		this.boardSmallSquareHeight = (short)(this.nonePiece.getPieceSmallSquareHeight() + 1);
		this.boardSmallSquareWidth	= (short)(this.nonePiece.getPieceSmallSquareWidth() + 1);
		this.boardSquareWidth 		= (short)((this.boardSmallSquareWidth * BOARD_COLUMNS) + BOARD_BORDER + BOARD_BORDER);
		this.boardSquareHeight 		= (short)((this.boardSmallSquareHeight * BOARD_LINES) + BOARD_BORDER + BOARD_BORDER);
		
		//define the game board
		this.gameBoard				= new short[BOARD_LINES][BOARD_COLUMNS];
		this.gameBoardColor			= new Color[BOARD_LINES][BOARD_COLUMNS];

		//clear the gameboard
		for (short i = 0; i < this.gameBoard.length; i++) {
			for (short j = 0; j < this.gameBoard[i].length; j++) {
				this.gameBoard[i][j] = -1;
			}
		}

		//set the current theme & load the circle image, based on theme
		this.theme					= new Theme(defaultTheme);
		this.circle 				= this.theme.getCircle();

		//define the bg width/height
		int bgwidth 				= BOARD_LEFT + boardSquareWidth + 150;
		int bgheight				= BOARD_TOP + boardSquareHeight + 150;

		//create the main game image structure
		this.gameBoardBG			= GraphicsEnvironment.getLocalGraphicsEnvironment()
														 .getDefaultScreenDevice().getDefaultConfiguration()
														 .createCompatibleImage(bgwidth, bgheight, Transparency.TRANSLUCENT);
		
		//get the G2D (from backbuffered image)
		this.bg2d					= (Graphics2D)this.gameBoardBG.getGraphics();

		//verify if draw or not the piece ghost
		this.allowGhostPiece		= this.gameRef.isToAllowGhostPiece();
		this.allowHold				= this.gameRef.isToAllowHold();

		//get the next pieces count
		if (this.showHowManyNext < 1) {
			this.showHowManyNext = this.gameRef.getHowManyNextPieces();
		}

		//just one, draw game background
		this.drawGameBoardBG();

		//load the labels
		this.score					= LoadingStuffs.getInstance().getImage("score");
		this.hiscore				= LoadingStuffs.getInstance().getImage("hiscore");
		this.labelLevel				= LoadingStuffs.getInstance().getImage("labelLevel");
		this.labelLine				= LoadingStuffs.getInstance().getImage("labelLine");

		//load audios
		this.turn 					= LoadingStuffs.getInstance().getAudio("turn"); 
		this.move 					= LoadingStuffs.getInstance().getAudio("move"); 
		this.splash 				= LoadingStuffs.getInstance().getAudio("splash");
		this.drop 					= LoadingStuffs.getInstance().getAudio("drop");
		this.holdSound				= LoadingStuffs.getInstance().getAudio("hold");
		this.tetris					= LoadingStuffs.getInstance().getAudio("tetris");

		//calc the render position
		this.renderPositionX 		= (short)((this.gameRef.getInternalResolutionWidth() / 2) - (this.boardSquareWidth / 2) - (BOARD_LEFT + 60));
		this.renderPositionY 		= (short)((this.gameRef.getInternalResolutionHeight() / 2) - (this.boardSquareHeight / 2) - BOARD_TOP);
	}

	/**
	 * Update method
	 */
	public void update(long frametime) {
		if (!this.stopped) {
			//add framecounter
			this.framecounter += frametime;

			if (this.allowGhostPiece) {
				this.actualPiece.updateGhost(frametime);
			}

			//framecounter reach timer or actualPositionY == default
			if ((this.framecounter >= (1_000_000_000 / this.gameRef.getGameSpeed()) ||
				(this.actualPiece.getActualPositionY() == this.actualPiece.getDefaultInitialSquareY())) ) {

				//down one line
				this.actualPiece.downOneLine(true);

				//reset counter
				this.framecounter = 0;

				//check if at least one line is complete
				this.checkLineClear();
			}

			byte tempLevel = (byte)((this.linesCleared / 10) + 1);
			if (tempLevel != this.currentLevel) {
				this.currentLevel = tempLevel;
				this.gameRef.nextLevel();
			}
			this.gameRef.getScore().setCurrentLevel(this.currentLevel);
			this.gameRef.getScore().setLines(this.linesCleared);
		}
	}

	/**
	 * Draw method
	 * @param frametime
	 */
	public void draw(long frametime) {

		//draw the background and bgimage
		this.getG2D().drawImage(this.theme.getBackgroundImage(), 0, 0, null);
		this.getG2D().drawImage(this.gameBoardBG, renderPositionX, renderPositionY, null);
		this.getG2D().drawImage(this.score, 	30, 11, null);
		this.getG2D().drawImage(this.hiscore, 	this.gameRef.getInternalResolutionWidth() - this.hiscore.getWidth() - 30, 11, null);

		this.getG2D().drawImage(this.circle, 	 37, 362, null);
		this.getG2D().drawImage(this.circle, 	 37, 568, null);
		this.getG2D().drawImage(this.labelLevel, 83, 345, null);
		this.getG2D().drawImage(this.labelLine,  76, 553, null);

		//Draw hold piece
		if (this.allowHold && this.holdPiece != null) {
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
		this.actualPiece.draw(frametime, this.allowGhostPiece);
		
		//draw list of sorted pieces
		BasePiece nextPiece = null;
		for (byte cnt = 0; this.nextPieces != null && cnt < this.nextPieces.size() && cnt < this.showHowManyNext; cnt++) {
			nextPiece = this.nextPieces.get(cnt);
			nextPiece.drawNext(frametime, cnt);
		}

		//draw bag of pieces
		this.drawBoardPieces();
	}

	/**
	 * Draw the bag of pieces in the board
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
	private void drawGameBoardBG() {

		this.bg2d.setComposite(java.awt.AlphaComposite.Clear);
		this.bg2d.fillRect(0, 0, this.gameBoardBG.getWidth(), this.gameBoardBG.getHeight());
		this.bg2d.setComposite(java.awt.AlphaComposite.SrcOver);

		boolean filled 		= this.theme.getFilledGrid();
		this.next			= this.theme.getNextLabel();
		this.hold 			= this.theme.getHoldLabel();
		
		//fill the bg with theme color, case selected not checkered filled
		if (!filled) {
			this.bg2d.setColor(this.theme.getBgBoardColor());
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
					//the color of the line
					this.bg2d.setColor(this.theme.getLineColor());

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
		
		if (this.allowHold) {
			//Draw hold box
			this.bg2d.drawImage(this.hold, 5, 11, null);
			this.bg2d.setColor(this.theme.getBgBoardColor());
			this.bg2d.fillRect(HOLD_BOX_LEFT + 1, HOLD_BOX_TOP - 8 + 1, HOLD_BOX_WIDTH - 1, HOLD_BOX_HEIGHT - 1);
			this.bg2d.setColor(Color.DARK_GRAY);
			this.bg2d.fillRect(HOLD_BOX_LEFT + HOLD_BOX_WIDTH, HOLD_BOX_TOP - 8 + SHADOW_THICKNESS, SHADOW_THICKNESS, HOLD_BOX_HEIGHT);
			this.bg2d.fillRect(HOLD_BOX_LEFT + SHADOW_THICKNESS, HOLD_BOX_TOP - 8 + HOLD_BOX_HEIGHT, HOLD_BOX_WIDTH, SHADOW_THICKNESS);
			this.bg2d.setColor(Color.LIGHT_GRAY);
			this.bg2d.drawRect(HOLD_BOX_LEFT, HOLD_BOX_TOP - 8, HOLD_BOX_WIDTH, HOLD_BOX_HEIGHT);
		}

		//Next boxes
		final short nextPosX = (short)(BOARD_LEFT + boardSquareWidth + 20);
		if (this.showHowManyNext > 0) {
			this.bg2d.drawImage(this.next, nextPosX + 3, 11, null);
		}

		for (byte i = 0; i < this.showHowManyNext; i++) {
			short nextPosY = (short)((i * 100) - 8);
			this.bg2d.setColor(this.theme.getBgBoardColor());
			this.bg2d.fillRect(HOLD_BOX_LEFT + 1 + nextPosX, HOLD_BOX_TOP + nextPosY + 1, HOLD_BOX_WIDTH - 1, HOLD_BOX_HEIGHT - 1);
			this.bg2d.setColor(Color.DARK_GRAY);
			this.bg2d.fillRect(HOLD_BOX_LEFT + HOLD_BOX_WIDTH + nextPosX, HOLD_BOX_TOP + nextPosY + SHADOW_THICKNESS, SHADOW_THICKNESS, HOLD_BOX_HEIGHT);
			this.bg2d.fillRect(HOLD_BOX_LEFT + SHADOW_THICKNESS + nextPosX, HOLD_BOX_TOP + nextPosY + HOLD_BOX_HEIGHT, HOLD_BOX_WIDTH, SHADOW_THICKNESS);
			this.bg2d.setColor(Color.LIGHT_GRAY);
			this.bg2d.drawRect(HOLD_BOX_LEFT + nextPosX, HOLD_BOX_TOP + nextPosY, HOLD_BOX_WIDTH, HOLD_BOX_HEIGHT);
		}
	}

	//----------------------------------------------------//
    //------------------ Main logic ----------------------//
    //----------------------------------------------------//
	/**
	 * Very fast algorithm to drop lines & control what lines has to be deleted
	 */
	private void checkLineClear() {
		byte testValue 					= 0;
		byte maxLines					= 4;
		byte linesToEnd					= -1;
		byte sumValue					= 0;
		short [][] tempGameBoardP1 		= null;
		short [][] tempGameBoardP2 		= null;
		Color [][] tempGameBoardColorP1 = null;
		Color [][] tempGameBoardColorP2 = null;
		//based on the sum of clear lines, how much lines to add at top (starting from 0 to 10)
		final byte [] linesToAdd 		= {0, 1, 0, 2, 2, 2, 3, 3, 3, 0, 4};

		//iterate through all lines in the board looking for complete lines to clear
		//--->> but stop when found one of then
		for (byte linhas = 0; this.gameBoard != null && linhas < this.gameBoard.length; linhas++) {

			this.hasLineFull = false;
			testValue = 0;

			for (byte colunas = 0; colunas < gameBoard[linhas].length; colunas++) { 
				testValue += gameBoard[linhas][colunas];
			}

			//I found one line full
			if (testValue == BOARD_COLUMNS) {
				this.hasLineFull = true;
				this.firstline = linhas;

				/**
				 * Possibles 'sumValue' results (sums):
				 * 1 (1)
				 * 1, 2 (3)
				 * 1, 3 (4)
				 * 1, 4 (5)
				 * 1, 2, 3 (6)
				 * 1, 2, 4 (7)
				 * 1, 3, 4 (8)
				 * 1, 2, 3, 4 (tetris) (10)
				 */
				sumValue = 1;

				//test next 3 lines (or less depending where the 1st full line was found)
				//(max three lines because full lines are, at maximum four - tetris)
				//---->> first calc maxLines until bottom (sum 1, because the 0 index)
				linesToEnd = (byte)(BOARD_LINES - (this.firstline + 1));
				if (linesToEnd > 3) {
					maxLines = 3;
				} else {
					maxLines = linesToEnd;
				}

				//iterate begining in 1, because the first line (found before) was the '0' element
				for (byte i = 1; i < (maxLines + 1); i++) {
					testValue = 0;						
					for (byte colunas = 0; colunas < gameBoard[this.firstline + i].length; colunas++) { 
						testValue += gameBoard[this.firstline + i][colunas];
					}
					if (testValue == BOARD_COLUMNS) {
						sumValue += (i + 1);
					}
				}

				//-------------------------------------------------------------//
				//------->>> create the new (2 parts) matrix				---//
				//-------------------------------------------------------------//

				//we will divide the full matrix in 2:
				//--->> the first part, with lines before those we'll drop
				//--->> second part, with line after those we'll drop
				//so, create the "first" part of board
				tempGameBoardP1 		= new short[(this.firstline + linesToAdd[sumValue])][BOARD_COLUMNS];
				tempGameBoardColorP1 	= new Color[(this.firstline + linesToAdd[sumValue])][BOARD_COLUMNS];
					
				//Add on top, n white lines, with the same number of lines that we'll drop in the 'center' part of the matrix
				for (byte i = 0; i < linesToAdd[sumValue]; i++) {
					for (byte j = 0; j < BOARD_COLUMNS; j++) {
						tempGameBoardP1[i][j] 		= -1;
						tempGameBoardColorP1[i][j] 	= null;
					}
				}

				//than, we create the "second" part of board, ignoring the dropped lines
				tempGameBoardP2 		= new short[BOARD_LINES - tempGameBoardP1.length][BOARD_COLUMNS];
				tempGameBoardColorP2 	= new Color[BOARD_LINES - tempGameBoardP1.length][BOARD_COLUMNS];

				//-------------------------------------------------------------//
				//------->>> copy the first part from og matrix				---//
				//-------------------------------------------------------------//

				//copy the first part (from the og matrix)
				for (byte i = linesToAdd[sumValue]; i < (this.firstline + linesToAdd[sumValue]); i++) {
					tempGameBoardP1[i] = gameBoard[i - linesToAdd[sumValue]];
					tempGameBoardColorP1[i] = gameBoardColor[i - linesToAdd[sumValue]];
				}

				//-------------------------------------------------------------//
				//------->>> drop lines & copy the second part from 		---//
				//------->>> og matrix										---//
				//-------------------------------------------------------------//

				//we'll start to drop the completed lines
				//we'll divide the process in 4 situations:
				//1st:
				//--->> when the lines are continuous (1, 2, 3, or 4 lines)
				if (sumValue == 1 || sumValue == 3 || sumValue == 6 || sumValue == 10) {

					//as easy as  jump the dropped lines
					for (byte i = (byte)(this.firstline + linesToAdd[sumValue]), j = 0; i < BOARD_LINES; i++, j++) {
						tempGameBoardP2[j] = gameBoard[i];
						tempGameBoardColorP2[j] = gameBoardColor[i];
					}

					this.gameRef.getScore().addScore(sumValue, this.currentLevel);

				//2nd:
				//--->> the lines are not continuous, drop the elements (1 & 3) or (1 & 4)
				} else if (sumValue == 4 || sumValue == 5) { 
					
					//we have to save 2nd line:
					//--->> save the first element of dropped lines ([..2..])
					tempGameBoardP2[0] 		= gameBoard[this.firstline + 1];
					tempGameBoardColorP2[0] = gameBoardColor[this.firstline + 1];
					byte j = 1, k = 1;

					//and if we are in 1 & 4 situation, we also have to save 3rd line:
					//--->> save the second element ([..3..]) if we are in 1 & 4 situation
					if (sumValue == 5) {
						tempGameBoardP2[1] 		= gameBoard[this.firstline + 2];
						tempGameBoardColorP2[1] = gameBoardColor[this.firstline + 2];
						j = k = 2;
					}

					//then the copy the lasts (4th or 5th and so on...)
					for (byte i = (byte)(this.firstline + k + linesToAdd[sumValue]); i < BOARD_LINES; i++, j++) {
						tempGameBoardP2[j] 		= gameBoard[i];
						tempGameBoardColorP2[j] = gameBoardColor[i];
					}

					this.gameRef.getScore().addScore(Score.DOUBLELINE, this.currentLevel);
				//3rd:
				//--->> the lines are part continuos and part not (1, 2, 4) or (1, 3, 4)
				//--->> we just have to save 1 line
				} else if (sumValue == 7 || sumValue == 8) {

					byte k = (byte)((sumValue == 7)?2:1);
					tempGameBoardP2[0] 		= gameBoard[this.firstline + k];
					tempGameBoardColorP2[0] = gameBoardColor[this.firstline + k];

					//then the copy the lasts
					for (byte i = (byte)(this.firstline + linesToAdd[sumValue] + 1), j = 1; i < BOARD_LINES; i++, j++) {
						tempGameBoardP2[j] = gameBoard[i];
						tempGameBoardColorP2[j] = gameBoardColor[i];
					}

					this.gameRef.getScore().addScore(Score.TRIPELINE, this.currentLevel);
				}				

				this.gameBoard = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardP1), 
																 java.util.Arrays.stream(tempGameBoardP2)).toArray(short[][]::new));

				this.gameBoardColor = (java.util.stream.Stream.concat(java.util.Arrays.stream(tempGameBoardColorP1), 
																	  java.util.Arrays.stream(tempGameBoardColorP2)).toArray(Color[][]::new));

				tempGameBoardP1 = null;
				tempGameBoardP2 = null;
				tempGameBoardColorP1 = null;
				tempGameBoardColorP2 = null;

				if (sumValue != 10) {
					this.drop.play();
				} else {
					this.tetris.play();
				}

				this.linesCleared += linesToAdd[sumValue];

				//end
				break;
			}
		}
	}

	/**
	 * Hold piece method
	 */
	private void holdPiece() {
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

			this.holdSound.play();
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
	private void storePiecePosition(BasePiece piece) {
		
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

		this.splash.play();
	}

	//----------------------------------------------------//
    //------------------- Movements ----------------------//
    //----------------------------------------------------//
	/**
	 * Move the game
	 * @param keyCode
	 */
	public synchronized void move(int keyCode, boolean releaseAfter) {
		if (!this.stopped) {
			if (keyCode == 39) { //right
				this.getActualPiece().moveRight();
				this.move.play();
			} else if (keyCode == 37) { //left
				this.getActualPiece().moveLeft();
				this.move.play();
			} else if (keyCode == 38) { //up
				if (this.canRotate) {
					this.getActualPiece().rotateRight();
					this.turn.play();
					this.canRotate = releaseAfter;
				}
			} else if (keyCode == 40) { //down
				this.getActualPiece().downOneLine(false);
				this.gameRef.getScore().addScore(Score.HARDDROP, this.currentLevel);
				this.move.play();
			} else if (keyCode == 17 && this.allowHold) { // r-control
				this.holdPiece();
			} else if (keyCode == 32) { //space
				this.getActualPiece().allDown();
			}
		}
	}

	/**
	 * Move the game
	 * @param keyCode
	 */
	public synchronized void move(int keyCode) {
		this.move(keyCode, false);
	}

	//----------------------------------------------------//
    //------------------- Apparence ----------------------//
    //----------------------------------------------------//
	/** 
	 * Toogle color theme
	*/
	public void toogleColorTheme() {
		this.defaultTheme = (byte)(++this.defaultTheme%8);
		this.theme.setTheme(this.defaultTheme);
		this.circle = this.theme.getCircle();
		this.drawGameBoardBG();
	}


	public void resetColorTheme() {
		this.setColorTheme((byte)0);
	}

	/** 
	 * set the color theme
	*/
	public void setColorTheme(byte colorTheme) {
		this.theme.setTheme(colorTheme);
		this.circle = this.theme.getCircle();
		this.drawGameBoardBG();
	}

    //----------------------------------------------------//
    //--------------- Music & SFX  -----------------------//
    //----------------------------------------------------//
	/**
	 * Decrease SFX Volume
	 */
	public void decVolumeSFX() {
		this.turn.decVolume(1);
		this.move.decVolume(1);
		this.splash.decVolume(1);
		this.drop.decVolume(1);
		this.tetris.decVolume(1);
		this.holdSound.decVolume(1);
    }

	/**
	 * Decrease SFX Volume
	 */
	public void incVolumeSFX() {
		this.turn.addVolume(1);
		this.move.addVolume(1);
		this.splash.addVolume(1);
		this.drop.addVolume(1);
		this.tetris.addVolume(1);
		this.holdSound.addVolume(1);
    }

	//----------------------------------------------------//
    //--------------- Pause & Reset ----------------------//
    //----------------------------------------------------//
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
		this.gameRef.resetGameLevel();
		this.gameBoard				= new short[BOARD_LINES][BOARD_COLUMNS];

		//clear the hold, last & actual piece
		this.lastPiece 				= null;
		this.holdPiece 				= null;
		this.actualPiece 			= null;
		this.currentLevel           = 1;
		this.linesCleared			= 0;
		
		//Clear the gameboard
		for (int cnt = 0; cnt < this.gameBoard.length; cnt++) {
			for (int cnt2 = 0; cnt2 < this.gameBoard[cnt].length; cnt2++) {
				this.gameBoard[cnt][cnt2] = -1;
			}
		}
		
		//Sort a new piece
		this.sortPiecesList();
		
		//reset the color theme
		this.resetColorTheme();
	}

	/**
	 * Toogle game pause
	 */
	public void tooglePause() {
		this.stopped = !this.stopped;
	}

    //----------------------------------------------------//
    //------------------- Accessors ----------------------//
    //----------------------------------------------------//
	public BasePiece getActualPiece() 			{	return (this.actualPiece);				}
	public short getBoardSmallSquareWidth() 	{	return (this.boardSmallSquareWidth);	}
	public short getBoardSmallSquareHeight() 	{	return (this.boardSmallSquareHeight);	}
	public short[][] getGameBoard() 			{	return (this.gameBoard);				}
	public boolean isToDrawGhost() 				{	return (this.allowGhostPiece);			}
	public short getRenderPositionX() 			{	return (this.renderPositionX);			}
	public short getRenderPositionY() 			{ 	return (this.renderPositionY);			}
	public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());			}
	public Game getGameRef() 					{	return (this.gameRef);					}
	public short getLinesCleared() 				{	return (this.linesCleared);				}

	/**
	 * Terminate the board
	 */
	public void terminate() {
		//images
		this.gameBoardBG = null;
		this.next = null;
		this.hold = null;
		this.score = null;
		this.hiscore = null;
		this.labelLevel = null;
		this.labelLine = null;
		this.circle = null;

		//theme
    	this.bg2d = null;
		this.fillColor = false;
		this.theme = null;
		this.defaultTheme = 0;

		//Game variables
		this.nextPieces.clear();
		this.nextPieces = null;
		this.nonePiece = null;
		this.holdPiece = null;
		this.actualPiece = null;
		this.lastPiece = null;
		this.boardSquareHeight = 0;
		this.gameBoard = null;
		this.gameBoardColor = null;
		
		//stop the audio
		this.turn.stop();
		this.turn = null;
		this.move.stop();
		this.move = null;
		this.splash.stop();
		this.splash = null;
		this.drop.stop();
		this.drop = null;
		this.tetris.stop();
		this.tetris = null;
		this.holdSound.stop();
		this.holdSound = null;

		//Gameplay variables
		this.framecounter = 0;
		this.renderPositionX = 0;
		this.renderPositionY = 0;
		this.gameRef = null;
		this.canRotate = true;
		this.canHold = true;
		this.stopped = false;
		this.currentLevel = 1;
		this.linesCleared = 0;

		//Variables to control lines to drop
		this.hasLineFull = false;
		this.firstline = -1;

		//allow ghost/hold
		this.allowGhostPiece = true;
		this.allowHold = true;
		this.showHowManyNext = -1;
	}
}