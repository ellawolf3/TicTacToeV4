package clarkson.ee408.tictactoev4.socket;

/**
 * Server response to a REQUEST_MOVE request. It is a subclass of Response.java
 */
public class GamingResponse extends Response {

    private int move;
    private boolean active;

    /**
     * Default constructor for the GamingResponse class.
     * Initializes the response status to FAILURE, move to 0, and active to false.
     */
    public GamingResponse() {
        super(ResponseStatus.FAILURE, "");
        this.move = 0;
        this.active = false;
    }

    /**
     * Constructor for creating a GamingResponse with specified attributes.
     *
     * @param move   An integer representing the last move made by the current player's opponent.
     *               The value from 0-8 represents the cell of TicTacToe from top-bottom, left-right.
     * @param active A boolean variable to indicate if the opponent is still active in the game.
     */
    public GamingResponse(int move, boolean active) {
        super(ResponseStatus.SUCCESS, "");
        this.move = move;
        this.active = active;
    }

    /**
     * Gets the last move made by the opponent.
     *
     * @return An integer representing the last move.
     */
    public int getMove() {
        return move;
    }

    /**
     * Sets the last move made by the opponent.
     *
     * @param move The new last move made by the opponent.
     */
    public void setMove(int move) {
        this.move = move;
    }

    /**
     * Checks if the opponent is still active in the game.
     *
     * @return true if the opponent is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the opponent in the game.
     *
     * @param active true to indicate that the opponent is active, false otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
