package game.pieces;

import java.awt.Color;
import game.Board;

/**
 * Class representing the L piece
 */
public class LPiece extends BasePiece {
	
	//piece properties
	private Color color 				= Color.BLUE;
	protected short [][] assetsLeft		= {{0, -1}, {1,  0}, {2, 0}, {3, 0}};
	protected short [][] assetsRight	= {{0, 3}, {1, 3}, {2, 2}, {3, 3}};
	protected short [][] assetsHeight	= {{0, 3}, {1, 2}, {2, 3}, {3, 2}};
	protected short [][] assetsWidth	= {{0, 4}, {1, 3}, {2, 4}, {3, 3}};
	protected short [][] pieceHeight	= {{0, 3}, {1, 2}, {2, 3}, {3, 2}};
	protected short [][][] piece 		= {{{0, 1}, {1, 1}, {2, 1}, {2, 2}},
									   	   {{0, 0}, {0, 1}, {0, 2}, {1, 0}},
									   	   {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
									   	   {{0, 2}, {1, 0}, {1, 1}, {1, 2}}};
	protected short [][][] positions 	= {{{-1,  1, -1}, {-1,  1, -1}, {-1,  1,  1}},
										   {{ 1,  1,  1}, { 1, -1, -1}, {-1, -1, -1}},
										   {{ 1,  1, -1}, {-1,  1, -1}, {-1,  1, -1}},
										   {{-1, -1,  1}, { 1,  1,  1}, {-1, -1, -1}}};
	
	/**
	 * Constructor
	 * @param board
	 */
	public LPiece(Board board) {
		super();
		this.boardRef = board;
	}

	@Override
	public short getDefaultInitialSquareY() {	
		return (-3);
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