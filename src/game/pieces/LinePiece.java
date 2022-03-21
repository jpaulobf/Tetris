package game.pieces;

import java.awt.Color;
import game.Board;

/**
 * Class representing the | piece
 */
public class LinePiece extends BasePiece {

	//piece properties
	private Color color 				= Color.CYAN;
	protected short [][] assetsLeft		= {{0, -1}, {1,  0}};
	protected short [][] assetsRight	= {{0, 2}, {1, 4}};
	protected short [][] assetsHeight	= {{0, 4}, {1, 2}};
	protected short [][] assetsWidth	= {{0, 3}, {1, 4}};
	protected short [][] pieceHeight	= {{0, 4}, {1, 1}};
	protected short [][][] piece 		= {{{0, 1}, {1, 1}, {2, 1}, {3, 1}},
			   						   	   {{0, 0}, {0, 1}, {0, 2}, {0, 3}}};
	protected short [][][] positions 	= {{{-1, 1, -1, -1}, {-1, 1, -1, -1}, {-1, 1, -1, -1}, {-1, 1, -1, -1}},
										   {{-1, -1, -1, -1}, {1, 1, 1, 1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}}};
	
	/**
	 * Constructor
	 * @param board
	 */
	public LinePiece(Board board) {
		this.boardRef = board;
	}
	
	@Override
	/**
	 * Test rotate right
	 */
	public void rotateRight() {
		if (this.boardRef.canRotateRight()) {
			
			//update to the next actual state
			this.pieceActualState = (byte)(++this.pieceActualState % 2);
			
			//test against left wall
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

			//test against right wall
			int assetRight = this.getAssetsRight();
			int finalPosition = this.actualPositionX + assetRight;
			if (finalPosition > Board.BOARD_COLUMNS) {
				this.actualPositionX = (short)(this.actualPositionX - 2);
			}
			
			//test against bottom wall
			while (this.getActualBottonPosition() > Board.BOARD_LINES) {
				this.actualPositionY--;
			}
		}
	}
	
	@Override
	public short getDefaultInitialSquareY() {	
		return (-4);
	}

	/**
	 * Get the next state of the piece
	 */
	@Override
	public byte getPieceNextState() {
		byte state = this.pieceActualState;
		state = (byte)(++state % 2);
		return (state);
	}

	//getters
	public Color getColor() 							{	return (this.color);							}
	public short[][] getActualPosition() 				{	return (this.positions[this.pieceActualState]);	}
	public short[][] getAssetsLeftMatrix()				{	return (this.assetsLeft);						}
	public short[][] getAssetsRightMatrix()				{	return (this.assetsRight);						}
	public short[][] getAssetsHeightMatrix()			{	return (this.assetsHeight);						}
	public short[][] getAssetsWidthMatrix()				{	return (this.assetsWidth);						}
	public short[][] getPieceHeightMatrix()				{	return (this.pieceHeight);						}
	public short[][][] getPiece()						{ 	return (this.piece);							}
}