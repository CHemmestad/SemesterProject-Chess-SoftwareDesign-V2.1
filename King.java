import java.util.LinkedList;

public class King extends Piece {

    //protected LinkedList<Piece> validPos = new LinkedList<>();
    private boolean inCheck;
    private boolean moveAvailable;

    public King(String name, String imageName, boolean isWhite, int pieceSize) {
        super(name, imageName, isWhite, pieceSize);
    }

    public boolean isChecked() {
        return inCheck;
    }

    public void setCheck(boolean checked) {
        this.inCheck = checked;
    }

    @Override
    public int[][] moveCheck(int xPosition, int yPosition, Board board) {
        //printInfo("King move check", "King", "23", this);
        reset();
        up(xPosition, yPosition, 1, board);
        down(xPosition, yPosition, 1, board);
        right(xPosition, yPosition, 1, board);
        left(xPosition, yPosition, 1, board);
        diagonalUpRight(xPosition, yPosition, 1, board);
        diagonalUpLeft(xPosition, yPosition, 1, board);
        diagonalDownRight(xPosition, yPosition, 1, board);
        diagonalDownLeft(xPosition, yPosition, 1, board);
        return this.validMoves;
    }
}
