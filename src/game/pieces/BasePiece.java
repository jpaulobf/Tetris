package game.pieces;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import game.Board;
import game.Score;

/**
 * Class representing the base piece
 */
public abstract class BasePiece {

	//constants
	public final static byte SMALL_SQUARE_WIDTH			= 30;
	public final static byte SMALL_SQUARE_HEIGHT		= 30;
	public final static byte SMALL_SQUARE_WIDTH_HOLD	= 17;
	public final static byte SMALL_SQUARE_HEIGHT_HOLD	= 17;

	//properties
	protected volatile Board boardRef					= null;
	protected volatile short pieceSmallSquareWidth 		= BasePiece.SMALL_SQUARE_WIDTH;
	protected volatile short pieceSmallSquareHeight 	= BasePiece.SMALL_SQUARE_HEIGHT;
	protected volatile byte pieceActualState			= 0; //0 NORMAL, 1 - R90, 2 - R180, 3 - R270
	protected volatile short actualPositionX			= Board.INITIAL_SQUARE_X;
	protected volatile short actualPositionY			= this.getDefaultInitialSquareY();
	protected volatile short ghostPieceCollision		= Board.BOARD_LINES;
	private volatile byte lastPipe						= 0;

	/**
	 * Constructor
	 */
	public BasePiece() {
	}

	/**
	 * draw method
	 * @param graphics2D
	 */
	public void draw(long frametime, boolean drawGhost) {
		
		Color color 			= this.getColor();
		short [][] pieceAsArray = this.getActualPosition();

		if (pieceAsArray != null) {
			Graphics2D graphics2D 	= boardRef.getG2D();
			int calcPosX 			= 0;
			int calcPosY 			= 0;
			int calcPosYGhost 		= 0;
			int piecePositionX 		= Board.BOARD_LEFT + Board.BOARD_BORDER + (this.boardRef.getBoardSmallSquareWidth() * this.getActualPositionX());
			int piecePositionY 		= Board.BOARD_TOP  + Board.BOARD_BORDER + (this.boardRef.getBoardSmallSquareHeight() * this.getActualPositionY());
			int drawGhostInY		= -1;
			byte width				= BasePiece.SMALL_SQUARE_WIDTH;
			byte height				= BasePiece.SMALL_SQUARE_HEIGHT; 

			//take the piece (as array) and draw it (rect per rect)
			for (int linhas = 0; linhas < pieceAsArray.length; linhas++) {
				for (int colunas = 0; colunas < pieceAsArray[linhas].length; colunas++) { 
					if (pieceAsArray[linhas][colunas] == 1) {

						//calc square by square the position 
						calcPosX = piecePositionX + (colunas * (width)) + colunas + boardRef.getRenderPositionX();
						calcPosY = piecePositionY + (linhas * (height)) + linhas + boardRef.getRenderPositionY();
						
						//draw ghost available
						if (this.boardRef.isToDrawGhost() && drawGhost) {
							
							//define the assets heights based on enconter (ground)
							if (this.ghostPieceCollision == Board.BOARD_LINES) {
								drawGhostInY = Board.BOARD_LINES - this.getAssetsHeight();
							} else {
								//or another piece
								drawGhostInY = this.ghostPieceCollision - this.getAssetsHeight();
							}
							
							//calc ghost Y position
							drawGhostInY 	= Board.BOARD_TOP + Board.BOARD_BORDER + (drawGhostInY * this.boardRef.getBoardSmallSquareHeight());
							calcPosYGhost 	= drawGhostInY + (linhas * (height)) + linhas + boardRef.getRenderPositionY();
							
							//draw the ghost
							if (calcPosYGhost > calcPosY) {
								//alpha to 20%
								Composite c = graphics2D.getComposite();
								graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
								graphics2D.setColor(color);
								
								//draw the flat rect
								graphics2D.fill(new Rectangle(calcPosX, calcPosYGhost, width, height));
								
								//draw the bevel effect
								graphics2D.fill(new Rectangle(calcPosX, calcPosYGhost, 1, height));
								graphics2D.fill(new Rectangle(calcPosX, calcPosYGhost, width, 1));
								
								//draw the shadow
								graphics2D.fill(new Rectangle(calcPosX + width, calcPosYGhost, 1, height));
								graphics2D.fill(new Rectangle(calcPosX, calcPosYGhost + height, width + 1, 1));
								
								//back og parameters
								graphics2D.setComposite(c);
							}
						}

						//if current rect position < top of the --> board don't draw!
						if (calcPosY < this.boardRef.getRenderPositionY()) {
							continue;
						}
						
						//draw the flat rect
						graphics2D.setColor(color);
						graphics2D.fill(new Rectangle(calcPosX, calcPosY, width, height));
						
						//draw the bevel effect
						graphics2D.setColor(Color.WHITE);
						graphics2D.fill(new Rectangle(calcPosX, calcPosY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX, calcPosY, width, 1));
						
						//draw the shadow
						graphics2D.setColor(Color.DARK_GRAY);
						graphics2D.fill(new Rectangle(calcPosX + width, calcPosY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX, calcPosY + height, width + 1, 1));
					}
				}
			}
		}
	}
	
	/**
	 * Paint the holded piece 
	 * @param frametime
	 * @param color
	 * @param pieceAsArray
	 */
	public void drawHold(long frametime) {

		//get the color & piece as array
		Color color 			= this.getColor();
		short [][] pieceAsArray = this.getActualPosition();

		if (pieceAsArray != null) {
			Graphics2D graphics2D 	= boardRef.getG2D();
			int calcPosX 			= 0;
			int calcPosY 			= 0;
			byte width				= BasePiece.SMALL_SQUARE_WIDTH_HOLD;
			byte height				= BasePiece.SMALL_SQUARE_HEIGHT_HOLD; 
			this.setPieceSmallSquareWidth(width);
		  	this.setPieceSmallSquareHeight(height);
			int posX  				= ((Board.HOLD_BOX_LEFT) + (Board.HOLD_BOX_WIDTH / 2) - (this.getPieceWidth() / 2)); 
		  	int posY 				= ((Board.HOLD_BOX_TOP) + (Board.HOLD_BOX_HEIGHT / 2) - (this.getPieceHeight() / 2));
			int renderPositionX		= boardRef.getRenderPositionX();
			int renderPositionY		= boardRef.getRenderPositionY() - 10;
			
		  	//take the piece (as array) and draw it (rect per rect)
			for (int linhas = 0; linhas < pieceAsArray.length; linhas++) {
				for (int colunas = 0; colunas < pieceAsArray[linhas].length; colunas++) { 
					if (pieceAsArray[linhas][colunas] == 1) {

						//calc the position 
						calcPosX = posX + (colunas * (width)) + colunas;
						calcPosY = posY + (linhas * (height)) + linhas;
						
						//draw the flat rect
						graphics2D.setColor(color);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, width, height));

						//draw the bevel effect
						graphics2D.setColor(Color.WHITE);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, width, 1));

						//draw the shadow
						graphics2D.setColor(Color.DARK_GRAY);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX + width, calcPosY + renderPositionY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY + height, width + 1, 1));
					}
				}
			}
		}
	}

	/**
	 * Paint the piece in the next list
	 * @param frametime
	 * @param position
	 */
	public void drawNext(long frametime, byte position) {

		//get the color & piece as array
		Color color 			= this.getColor();
		short [][] pieceAsArray = this.getActualPosition();

		if (pieceAsArray != null) {
			Graphics2D graphics2D 	= boardRef.getG2D();
			int calcPosX 			= 0;
			int calcPosY 			= 0;
			byte width				= BasePiece.SMALL_SQUARE_WIDTH_HOLD;
			byte height				= BasePiece.SMALL_SQUARE_HEIGHT_HOLD; 
			this.setPieceSmallSquareWidth(width);
		  	this.setPieceSmallSquareHeight(height);
			int posX  				= Board.SORTED_BOX_LEFT + (Board.HOLD_BOX_WIDTH / 2) - (this.getPieceWidth() / 2); 
		  	int posY 				= Board.SORTED_BOX_TOP + (Board.HOLD_BOX_HEIGHT / 2) - (this.getPieceHeight() / 2) + (position * (Board.HOLD_BOX_HEIGHT + 20));
			int renderPositionX		= boardRef.getRenderPositionX();
			int renderPositionY		= boardRef.getRenderPositionY() - 10;
			
		  	//take the piece (as array) and draw it (rect per rect)
			for (int linhas = 0; linhas < pieceAsArray.length; linhas++) {
				for (int colunas = 0; colunas < pieceAsArray[linhas].length; colunas++) { 
					if (pieceAsArray[linhas][colunas] == 1) {

						//calc the position 
						calcPosX = posX + (colunas * (width)) + colunas;
						calcPosY = posY + (linhas * (height)) + linhas + 29;
						
						//draw the flat rect
						graphics2D.setColor(color);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, width, height));

						//draw the bevel effect
						graphics2D.setColor(Color.WHITE);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY, width, 1));

						//draw the shadow
						graphics2D.setColor(Color.DARK_GRAY);
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX + width, calcPosY + renderPositionY, 1, height));
						graphics2D.fill(new Rectangle(calcPosX + renderPositionX, calcPosY + renderPositionY + height, width + 1, 1));
					}
				}
			}
		}
	}

	/**
	 * Look for pieces in board colliding with ghost, and store it's position
	 * @param frametime
	 */
	public void updateGhost(long frametime) {

		//reset
		this.ghostPieceCollision = Board.BOARD_LINES;

		//get the piece as array
		short [][] pieceAsArray = this.getActualPosition();

		//piece can't be null & have to draw ghost
		if (pieceAsArray != null) {
			int lineFound			= -1;
			int lastLine			= -1;
			int positionX 			= this.getActualPositionX() - this.getAssetsLeft();
			int positionLineDiff	= 0;
			
			//loop throught the piece looking for rects (value = 1)
			for (int lines = 0; lines < pieceAsArray.length; lines++) {
				for (int rows = 0; rows < pieceAsArray[lines].length; rows++) { 
					if (pieceAsArray[lines][rows] == 1) {

						//loop through the board, looking for a piece in this position
						for (byte lineInBoard = 0; lineInBoard < Board.BOARD_LINES; lineInBoard++) {
							if (this.boardRef.getGameBoard()[lineInBoard][positionX + rows + this.getAssetsLeft()] == 1) {
								if (lineInBoard <= this.ghostPieceCollision) {
									this.ghostPieceCollision = lineInBoard;
									lineFound = lines;
									break;
								}
							}
						}
						//save the last-line of the piece
						lastLine = lines;
					}
				}
			}
		
			//found! have to be above the last-line of the piece
			if (lineFound != -1 && lineFound < lastLine) {
				
				//store the diff between the line found and the last line
				positionLineDiff 	= lastLine - lineFound;
				this.ghostPieceCollision += positionLineDiff;
				
				if (this.ghostPieceCollision > Board.BOARD_LINES) {
					//adjust the piece position if greater than total
					--this.ghostPieceCollision;

				} else {
					//--- Diff = 2, then it's L ou inverted L
					if (positionLineDiff == 2) {
						
						//adjust last line if necessary
						for (int colunas = 0; colunas < pieceAsArray[lastLine].length; colunas++) { 
							if (pieceAsArray[lastLine][colunas] == 1) {
								if (this.boardRef.getGameBoard()[this.ghostPieceCollision - 1][positionX + colunas + this.getAssetsLeft()] == 1) {
									--this.ghostPieceCollision;
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * rotate the piece to the right
	 */
	public void rotateRight() {
		if (this.boardRef.canRotateRight()) {
			
			//update to the next state
			this.pieceActualState = (byte)(++this.pieceActualState % 4);

			//adjust the assets if necessary (in the left wall)
			int assetLeft = this.getAssetsLeft();
			if (assetLeft == -1) {
				if (this.actualPositionX < -1) {
					this.actualPositionX = -1;
				}
			} else {
				if (this.actualPositionX < 0) {
					this.actualPositionX = 0;
				}	
			}
			
			//adjust the assets if necessary (in the right wall)
			int assetRight = this.getAssetsRight();
			int finalPositionX = this.actualPositionX + assetRight;
			if (finalPositionX > Board.BOARD_COLUMNS) {
				this.actualPositionX--;
			}
			
			//adjust the assets if necessary (in the bottom wall)
			while (this.getActualBottonPosition() > Board.BOARD_LINES) {
				this.actualPositionY--;
			}

			//recalculate ghost piece colision
			
		}
	}

	/**
	 * Move the piece to the right
	 */
	public void moveRight() {
		if (this.boardRef.canMoveRight()) {
			this.actualPositionX++;
		}
	}

	/**
	 * Move the piece to the left
	 */
	public void moveLeft() {
		
		if (this.boardRef.canMoveLeft()) {
			//decrease the x position
			--this.actualPositionX;

			//adjust the assets
			int assetLeft = this.getAssetsLeft();
			if (assetLeft == -1) {
				if (this.actualPositionX < -1) {
					this.actualPositionX = -1;
				}
			} else {
				if (this.actualPositionX < 0) {
					this.actualPositionX = 0;
				}	
			}
		}
	}
	
	/**
	 * Move the piece one line down
	 */
	public synchronized void downOneLine(boolean addPoint) {
		if (this.boardRef.canMoveDown()) {
			this.actualPositionY++;
			if (addPoint) {
				this.boardRef.getGameRef().getScore().addScore(Score.SINGLELINE, this.boardRef.getCurrentLevel());
			}
		} else {
			this.boardRef.sortPiecesList();
		}
	}

	/**
	 * Move the piece one line down
	 */
	public synchronized void allDown() {
		while(this.boardRef.canMoveDown()) {
			this.actualPositionY++;
		}
		this.boardRef.sortPiecesList();
	}
	
	/**
	 * Sort the next piece
	 * @param board
	 * @param piece
	 * @param lastPiece
	 * @return
	 */
	public BasePiece sortNextPiece(Board board, BasePiece piece, BasePiece lastPiece) {
		
		this.boardRef = board;
		
		//avoid equal ou null piece
		while (piece == null || (piece.getClass().isInstance(lastPiece))) {
			
			//---------------------------------//
			//--- Rules (weights):			---//
			//--- 	-> 10% for | 			---//
			//--- 	-> 15% for L			---//
			//--- 	-> 15% for inverted L	---//
			//--- 	-> 11% for Square   	---//
			//--- 	-> 17% for S		   	---//
			//--- 	-> 17% for Z		   	---//
			//--- 	-> 15% for T		   	---//
			//---------------------------------//
			int pesoPeca = (int)(Math.random() * 100);
			
			if (pesoPeca >= 0 && pesoPeca < 10) {
				piece = new LinePiece(this.boardRef);
				this.lastPipe = 0;
			} else {
				if (pesoPeca >= 10 && pesoPeca < 25) {
					piece = new LPiece(this.boardRef);
				} else if (pesoPeca >= 25 && pesoPeca < 40) {
					piece = new InvertLPiece(this.boardRef);
				} else if (pesoPeca >= 40 && pesoPeca < 51) {
					piece = new SquarePiece(this.boardRef);
				} else if (pesoPeca >= 51 && pesoPeca < 68) {
					piece = new SPiece(this.boardRef);
				} else if (pesoPeca >= 68 && pesoPeca < 85) {
					piece = new ZPiece(this.boardRef);
				} else if (pesoPeca >= 85 && pesoPeca <= 100) {
					piece = new TPiece(this.boardRef);
				}
				this.lastPipe++;
			}

			//max sort without Pipe = 12;
			if (this.lastPipe > 12) {
				piece = new LinePiece(this.boardRef);
				this.lastPipe = 0;
			}
		}
		return (piece);
	}

	/**
	 * get the next piece state
	 * @return
	 */
	public byte getPieceNextState() {
		byte state = this.pieceActualState;
		state = (byte)(++state % 4);
		return (state);
	}

	/**
	 * Reset piece
	 */
	public void reset() {
		this.pieceSmallSquareWidth 	= BasePiece.SMALL_SQUARE_WIDTH;
		this.pieceSmallSquareHeight = BasePiece.SMALL_SQUARE_HEIGHT;
		this.pieceActualState		= 0;
		this.actualPositionX		= Board.INITIAL_SQUARE_X;
		this.actualPositionY		= this.getDefaultInitialSquareY();
	}
	
	/**
	 * initial position to all pieces (default for some pieces)
	 * @return
	 */
	public short getDefaultInitialSquareY() {
		return (-2);
	}
	
	/**
	 * get assets of left side of the piece
	 * @return
	 */
	public short getAssetsLeft() {
		return (this.getAssetsLeft(this.pieceActualState));
	}
	
	/**
	 * * get assets of top side of the piece
	 * @return
	 */
	public short getAssetsHeight() {
		return (this.getAssetsHeight(this.pieceActualState));
	}
	
	/**
	 * get piece height in squares
	 * @return
	 */
	public short getPieceSquareHeight() {
		return (this.getPieceSquareHeight(this.pieceActualState));
	}

	/**
	 * get left assets
	 * @return
	 */
	public short getAssetsLeft(byte pieceState) {
		return (this.getAssetsLeftMatrix()[pieceState][1]);
	}
	
	/**
	 * get right assets
	 * @return
	 */
	public short getAssetsRight() {	
		return (this.getAssetsRightMatrix()[this.pieceActualState][1]);
	}
	
	/**
	 * get height assets
	 * @return
	 */
	public short getAssetsHeight(byte pieceState) {	
		return (this.getAssetsHeightMatrix()[pieceState][1]);
	}
	
	/**
	 * get piece height in square number
	 * @return
	 */
	public short getPieceSquareHeight(byte pieceState) {
		return (this.getPieceHeightMatrix()[pieceState][1]);
	}

	/**
	 * get piece height
	 * @return
	 */
	public short getPieceHeight() {	
		return ((short)(this.getAssetsHeightMatrix()[this.pieceActualState][1] * this.pieceSmallSquareHeight));
	}
	
	/**
	 * get piece width
	 * @return
	 */
	public short getPieceWidth() {
		return ((short)(this.getAssetsWidthMatrix()[this.pieceActualState][1] * this.pieceSmallSquareWidth));	
	}
	
	/**
	 * get the matrix piece
	 * @return
	 */
	public short[][] getPieceMatrix() {	
		return (this.getPiece()[this.pieceActualState]);
	}

	/**
	 * get rotated matrix piece
	 * @return
	 */
	public short[][] getNextStatePieceMatrix() {	
		return (this.getPiece()[this.getPieceNextState()]);
	}

	//Getters & Setter
	public byte getPieceActualState() 									{	return (this.pieceActualState);												}
	public short getPieceSmallSquareWidth() 							{	return (this.pieceSmallSquareWidth);										}
	public short getPieceSmallSquareHeight() 							{	return (this.pieceSmallSquareHeight);										}
	public short getActualPositionX() 									{	return (this.actualPositionX);												}
	public short getActualPositionY() 									{	return (this.actualPositionY);												}
	public short getActualBottonPosition() 								{	return (short)(this.getActualPositionY() + this.getAssetsHeight());			}
	public short getActualBottonPosition(byte state) 					{	return (short)(this.getActualPositionY() + this.getAssetsHeight(state));	}
	public void setPieceActualState(byte pieceActualState) 				{	this.pieceActualState = pieceActualState;									}
	public void setActualPositionX(short actualSquareX) 				{	this.actualPositionX = actualSquareX;										}
	public void setActualPositionY(short actualSquareY) 				{	this.actualPositionY = actualSquareY;										}
	public void setPieceSmallSquareWidth(short pieceSmallSquareWidth) 	{	this.pieceSmallSquareWidth = pieceSmallSquareWidth;							}
	public void setPieceSmallSquareHeight(short pieceSmallSquareHeight) {	this.pieceSmallSquareHeight = pieceSmallSquareHeight;						}
	
	//abstract
	public abstract Color getColor();
	public abstract short[][] getActualPosition();
	public abstract short[][] getAssetsLeftMatrix();
	public abstract short[][] getAssetsRightMatrix();
	public abstract short[][] getAssetsHeightMatrix();
	public abstract short[][] getPieceHeightMatrix();
	public abstract short[][][] getPiece();
	public abstract short[][] getAssetsWidthMatrix();
}