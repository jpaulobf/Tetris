package game.pieces;

import java.awt.Color;
import game.Board;

/**
 * Class representing NonePiece
 */
public class NonePiece extends BasePiece {
	
	@SuppressWarnings("unused")
	private Board boardRef = null;

	/**
	 * Constructor
	 * @param board
	 */
	public NonePiece(Board board) {
		this.boardRef = board;
	}
	
	//getters
	public Color getColor() 							{	return null;	}
	public short[][] getActualPosition() 				{	return null; 	}
	public short[][] getAssetsLeftMatrix()				{	return null;	}
	public short[][] getAssetsRightMatrix()				{	return null;	}
	public short[][] getAssetsHeightMatrix()			{	return null;	}
	public short[][] getAssetsWidthMatrix()				{	return null;	}
	public short[][] getPieceHeightMatrix()				{	return null;	}
	public short[][][] getPiece()						{ 	return null;	}

	@Override
	public void draw(long frametime, boolean drawGhost) {}
	
	@Override
	public void drawHold(long frametime) {}
	
	@Override
	public short getAssetsLeft(byte pieceState) 		{	return 0;		}

	@Override
	public short getAssetsRight() 						{	return 0;		}

	@Override
	public short getAssetsHeight(byte pieceState) 		{	return 0;		}

	@Override
	public short getPieceSquareHeight(byte pieceState) 	{	return 0;		}

	@Override
	public short getPieceHeight() 						{	return 0;		}

	@Override
	public short getPieceWidth() 						{	return 0;		}

	@Override
	public short[][] getPieceMatrix() 					{	return null;	}

	@Override
	public short[][] getNextStatePieceMatrix() 			{	return null;	}
}