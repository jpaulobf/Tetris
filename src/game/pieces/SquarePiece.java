package game.pieces;

import java.awt.Color;
import game.Board;

/**
 * Class representing the Square piece
 */
public class SquarePiece extends BasePiece {

	//piece properties
	private Color color 				= Color.YELLOW;
	protected short [][][] positions 	= {{{1, 1}, {1, 1}}};
	protected short [][] assetsLeft		= {{0, 0}};
	protected short [][] assetsRight	= {{0, 2}};
	protected short [][] assetsHeight	= {{0, 2}};
	protected short [][] assetsWidth	= {{0, 2}};
	protected short [][] pieceHeight	= {{0, 2}};
	protected short [][] piece 			= {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
	
	/**
	 * Constructor
	 * @param board
	 */
	public SquarePiece(Board board) {
		this.boardRef = board;
		this.actualPositionX = 4;
	}

	@Override
	public void rotateRight() {	
		return;																	
	}

	@Override
	public short getAssetsLeft() {	
		byte asset = 0;
		return (this.getAssetsLeft(asset));
	}

	@Override
	public short getAssetsHeight()	{
		byte asset = 0;
		return (this.getAssetsHeight(asset));
	}

	@Override
	public short getPieceSquareHeight()	{
		byte asset = 0;
		return (this.getPieceSquareHeight(asset));
	}

	@Override
	public short getAssetsRight() {	
		return (assetsRight[0][1]);
	}
	
	@Override
	public short getPieceHeight() {
		return ((short)(this.assetsHeight[0][1] * this.pieceSmallSquareHeight));
	}

	@Override
	public short getPieceWidth() {
		return ((short)(this.assetsWidth[0][1] * this.pieceSmallSquareWidth));
	}

	@Override
	public short[][] getPieceMatrix() {
		return (this.piece);
	}

	@Override
	public short[][] getNextStatePieceMatrix() {
		return (this.piece);
	}

	//getters
	public Color getColor() 							{	return (this.color);							}
	public short[][] getActualPosition() 				{	return (this.positions[0]);						}
	public short[][] getAssetsLeftMatrix()				{	return (this.assetsLeft);						}
	public short[][] getAssetsRightMatrix()				{	return (this.assetsRight);						}
	public short[][] getAssetsHeightMatrix()			{	return (this.assetsHeight);						}
	public short[][] getAssetsWidthMatrix()				{	return (this.assetsWidth);						}
	public short[][] getPieceHeightMatrix()				{	return (this.pieceHeight);						}
	public short[][][] getPiece()						{ 	return (null);									}
}