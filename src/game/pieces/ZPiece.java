package game.pieces;

import java.awt.Color;
import game.Board;

/**
 * Class representing the Z piece
 */
public class ZPiece extends BasePiece {

	//piece properties
	private Color color 				= Color.GREEN;
	protected short [][] assetsLeft		= {{0, 0}, {1,  0}, {2, 0}, {3, 0}};
	protected short [][] assetsRight	= {{0, 3}, {1, 2}, {2, 3}, {3, 2}};
	protected short [][] assetsHeight	= {{0, 2}, {1, 3}, {2, 2}, {3, 3}};
	protected short [][] assetsWidth	= {{0, 3}, {1, 2}, {2, 2}, {3, 3}};
	protected short [][] pieceHeight	= {{0, 2}, {1, 3}, {2, 2}, {3, 3}};
	protected short [][][] piece 		= {{{0, 0}, {0, 1}, {1, 1}, {1, 2}},
									   	   {{0, 1}, {1, 0}, {1, 1}, {2, 0}},
									   	   {{0, 0}, {0, 1}, {1, 1}, {1, 2}},
									   	   {{0, 1}, {1, 0}, {1, 1}, {2, 0}}};
	protected short [][][] positions 	= {{{ 1,  1, -1}, {-1,  1,  1}, {-1, -1, -1}},
										   {{-1,  1, -1}, { 1,  1, -1}, { 1, -1, -1}},
										   {{ 1,  1, -1}, {-1,  1,  1}, {-1, -1, -1}},
									  	   {{-1,  1, -1}, { 1,  1, -1}, { 1, -1, -1}}};

	/**
	 * Constructor
	 * @param board
	 */
	public ZPiece(Board board) {
		super();
		this.boardRef = board;
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