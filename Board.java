import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class Board {

    private Piece piece[][] = new Piece[8][8];
    private String winner;
    private boolean gameOver = false;
    private boolean playerInCheck = false;
    public Image[] moveColors = new Image[5];
    public Image[] chessBoards = new Image[8];
    protected LinkedList<Piece> allPieces = new LinkedList<>();
    protected LinkedList<Piece> tempKings = new LinkedList<>();

    public Board(int pieceSize, int boardSize) {
        initialize();
        try {
            this.chessBoards[0] = ImageIO.read(new File("Images/WoodChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[1] = ImageIO.read(new File("Images/BlackChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[2] = ImageIO.read(new File("Images/BrushedMetalChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[3] = ImageIO.read(new File("Images/MarbleChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[4] = ImageIO.read(new File("Images/SpaceChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[5] = ImageIO.read(new File("Images/AIChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[6] = ImageIO.read(new File("Images/CodeChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[7] = ImageIO.read(new File("Images/FavoriteMovieChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.moveColors[0] = ImageIO.read(new File("Images/HighlightedEmpty.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[1] = ImageIO.read(new File("Images/HighlightedKill.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[2] = ImageIO.read(new File("Images/Check.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[3] = ImageIO.read(new File("Images/BlackTileSpace.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[4] = ImageIO.read(new File("Images/WhiteTileSpace.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                piece[x][y] = null;
            }
        }
    }

    public Piece getPiece(int xPosition, int yPosition) {
        return piece[xPosition][yPosition];
    }

    public String getWinner() {
        return winner;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPlayerChecked() {
        return playerInCheck;
    }

    public void movePiece(Piece piece, int x, int y) {
        printInfo("Moving piece", "Board", "65", null);
        if(piece.getPieceType() == 1) {
            ((Pawn)piece).setFirstFalse();
        }
        removePiece(piece.getPosX(), piece.getPosY());
        piece.setPosX(x);
        piece.setPosY(y);
        this.piece[x][y] = piece;
        checkGameStatus();
    }

    public void addPiece(Piece piece, int x, int y, boolean yes) {
        piece.setPosX(x);
        piece.setPosY(y);
        this.piece[x][y] = piece;
        if(yes) {
            allPieces.add(piece);
        }
    }

    public void removePiece(int x, int y) {
        this.piece[x][y] = null;
    }

    public void killPiece(Piece piece, int x, int y) {
        allPieces.remove(getPiece(x, y));
        movePiece(piece, x, y);
    }

    public void checkGameStatus() {
        System.out.println("Checking game status - Board(line : 96)");
        if((findWKing() == null) || (findBKing() == null)) {
            System.out.println("Game over");
            gameOver();
        }
        moveCheckAll();
        kingChecked(null);
    }

    public Piece addKings(Piece king) {
        printInfo("Adding temp kings", "Board", "105", null);
        king.moveCheck(king.getPosX(), king.getPosY(), this);
        int[][] tempArray = new int[8][8];
        System.arraycopy(king.validMoves, 0, tempArray, 0, king.validMoves.length);
        printArray(tempArray);
        Piece wKing = findWKing();
        Piece bKing = findBKing();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(king.validMoves[x][y] != 0) {
                    Piece savePiece = getPiece(x, y);
                    System.out.println("adding temp king ****************");
                    Piece tempKing = new King("temp", "Images/HighlightedEmpty.png", king.isWhite, king.pieceSize);
                    addPiece(tempKing, x, y, false);
                    tempKings.add(tempKing);
                    moveCheckAll();
                    if(!king.isWhite) {
                        wKing.moveCheck(wKing.getPosX(), wKing.getPosY(), this);
                    } else {
                        bKing.moveCheck(bKing.getPosX(), bKing.getPosY(), this);
                    }
                    kingChecked(tempKing);
                    if(((King)tempKing).isChecked()) {
                        System.out.print(tempKing.getPosX());
                        System.out.print(" , ");
                        System.out.print(tempKing.getPosY());
                        System.out.println(" : temp king pos = 0");
                        king.validMoves[tempKing.getPosX()][tempKing.getPosY()] = 0;
                    }
                    System.out.println("deleting temp king ****************");
                    piece[x][y] = savePiece;
                }
            }
        }
        tempKings.clear();
        return king;
    }

    private void kingChecked(Piece king) {
        printInfo("Checking King", "Board", "138", king);
        boolean Wfound = false;
        boolean Bfound = false;
        Piece Wking = findWKing();
        Piece Bking = findBKing();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece current = this.piece[x][y];
                if(current != null) {
                    if(current.availableKills.contains(Wking)) {
                        System.out.println("White King is in check");
                        Wfound = true;
                        playerInCheck = true;
                        ((King)Wking).setCheck(true);
                    }
                    if(current.availableKills.contains(Bking)) {
                        System.out.println("Black King is in check");
                        Bfound = true;
                        playerInCheck = true;
                        ((King)Bking).setCheck(true);
                    }
                    if(current.availableKills.contains(king)) {
                        System.out.println("temp King is in check");
                        ((King)king).setCheck(true);
                    }
                }
            }
        }
        if((!Wfound) && (Wking != null)) {
            ((King)Wking).setCheck(false);
        }
        if((!Bfound) && (Bking != null)) {
            ((King)Bking).setCheck(false);
        }
        if(!Bfound && !Wfound) {
            playerInCheck = false;
        }
    }

    private void moveCheckAll() {
        printInfo("Move Checking everything", "Board", "178", null);
        for(Piece current : allPieces) {
            if(current.getPieceType() != 5) {
                current.moveCheck(current.getPosX(), current.getPosY(), this);
            }
        }
    }

    private Piece findWKing() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece current = this.piece[x][y];
                if(current != null) {
                    if(current.getPieceType() == 5) {
                        if(current.isWhite) {
                            printInfo("White King Found", "Board", "195", current);
                            return current;
                        }
                    }
                }
            }
        }
        this.winner = "black";
        return null;
    }

    private Piece findBKing() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece current = this.piece[x][y];
                if(current != null) {
                    if(current.getPieceType() == 5) {
                        if(!current.isWhite) {
                            printInfo("Black King Found", "Board", "211", current);
                            return current;
                        }
                    }
                }
            }
        }
        this.winner = "white";
        return null;
    }

    private void gameOver() {
        this.gameOver = true;
    }

    private void printInfo(String what_is_it_doing, String in_what_class, String what_line, Piece the_piece) {
        System.out.print(what_is_it_doing);
        System.out.print(" - ");
        System.out.print(in_what_class);
        System.out.print("(line : ");
        System.out.print(what_line);
        System.out.print(")");
        if(the_piece != null) {
            System.out.print(" - Piece Type(");
            System.out.print(the_piece.getPieceType());
            System.out.print(") - Piece Loc (");
            System.out.print(the_piece.getPosX());
            System.out.print(" , ");
            System.out.print(the_piece.getPosY());
            System.out.println(")");
        } else {
            System.out.println();
        }
    }

    private void printArray(int[][] array) {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                System.out.print(array[x][y]);
            }
            System.out.println();
        }
    }
}
