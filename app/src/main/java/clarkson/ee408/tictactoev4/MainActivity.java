package clarkson.ee408.tictactoev4;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import clarkson.ee408.tictactoev4.client.AppExecutors;
import clarkson.ee408.tictactoev4.socket.GamingResponse;
import clarkson.ee408.tictactoev4.socket.Request;
import clarkson.ee408.tictactoev4.client.SocketClient;
import clarkson.ee408.tictactoev4.socket.Response;

public class MainActivity extends AppCompatActivity {
    private TicTacToe tttGame;
    private Button [][] buttons;
    private TextView status;
    private Gson gson;
    private SocketClient socketClient;
    private Handler moveHandler = new Handler();
    private boolean shouldRequestMove = false;
    private boolean isPlayerTurn;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        tttGame = new TicTacToe(2);
        buildGuiByCode();
        gson = new Gson();
        socketClient = SocketClient.getInstance();
        isPlayerTurn = tttGame.getPlayer() == 1;
        updateTurnStatus();

        Runnable moveTask = new Runnable() {
            @Override
            public void run() {
                if (shouldRequestMove) {
                    requestMove();
                }
                moveHandler.postDelayed(this, 1000);
            }
        };
        moveHandler.postDelayed(moveTask, 1000);
    }


    private void updateTurnStatus() {
        runOnUiThread(() -> {
            if (isPlayerTurn) {
                status.setText("Your Turn");
                enableButtons(true);
                shouldRequestMove = false;
            } else {
                status.setText("Waiting for Opponent");
                enableButtons(false);
                shouldRequestMove = true;
            }
        });
    }


    private void requestMove() {
        AppExecutors.getInstance().networkIO().execute(() -> {
            // Create a request for a move
            Request moveRequest = new Request(Request.RequestType.REQUEST_MOVE, "Requesting next move");

            // Send the request to the server and expect GamingResponse as the response type
            GamingResponse response = socketClient.sendRequest(moveRequest, GamingResponse.class);
            Log.d("", "The response is getting the move: " + response.getMove());

            if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                // No need to parse JSON, as we directly have the move value
                int move = response.getMove();
                if(move != -1) { // Check if a valid move is received
                    int row = move / 3;
                    int column = move % 3;
                    // Run on the UI thread to update the board
                    Log.d("", "The row is: " + row + " The col is: " + column);
                    runOnUiThread(() -> update(row, column));
                } else {
                    System.out.println("No move received or opponent not active");
                }
            } else {
                // Handle the case where response is null or status is not SUCCESS
                System.out.println("Failed to get a valid response from the server");
            }
        });
    }


    private void sendMove(int move) {
        // Serialize the move
        String serializedMove = gson.toJson(move);

        // Create a request object with the serialized move
        Request moveRequest = new Request(Request.RequestType.SEND_MOVE, serializedMove);

        // Send the request to the server
        AppExecutors.getInstance().networkIO().execute(() -> {
            socketClient.sendRequest(moveRequest, Response.class);
        });
    }


    public void buildGuiByCode( ) {
        // Get width of the screen
        Point size = new Point( );
        getWindowManager( ).getDefaultDisplay( ).getSize( size );
        int w = size.x / TicTacToe.SIDE;

        // Create the layout manager as a GridLayout
        GridLayout gridLayout = new GridLayout( this );
        gridLayout.setColumnCount( TicTacToe.SIDE );
        gridLayout.setRowCount( TicTacToe.SIDE + 2 );

        // Create the buttons and add them to gridLayout
        buttons = new Button[TicTacToe.SIDE][TicTacToe.SIDE];
        ButtonHandler bh = new ButtonHandler( );

//        GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//        bParams.width = w - 10;
//        bParams.height = w -10;
//        bParams.bottomMargin = 15;
//        bParams.rightMargin = 15;

        gridLayout.setUseDefaultMargins(true);

        for( int row = 0; row < TicTacToe.SIDE; row++ ) {
            for( int col = 0; col < TicTacToe.SIDE; col++ ) {
                buttons[row][col] = new Button( this );
                buttons[row][col].setTextSize( ( int ) ( w * .2 ) );
                buttons[row][col].setOnClickListener( bh );
                GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//                bParams.width = w - 10;
//                bParams.height = w -40;

                bParams.topMargin = 0;
                bParams.bottomMargin = 10;
                bParams.leftMargin = 0;
                bParams.rightMargin = 10;
                bParams.width=w-10;
                bParams.height=w-10;
                buttons[row][col].setLayoutParams(bParams);
                gridLayout.addView( buttons[row][col]);
//                gridLayout.addView( buttons[row][col], bParams );
            }
        }

        // set up layout parameters of 4th row of gridLayout
        status = new TextView( this );
        GridLayout.Spec rowSpec = GridLayout.spec( TicTacToe.SIDE, 2 );
        GridLayout.Spec columnSpec = GridLayout.spec( 0, TicTacToe.SIDE );
        GridLayout.LayoutParams lpStatus
                = new GridLayout.LayoutParams( rowSpec, columnSpec );
        status.setLayoutParams( lpStatus );

        // set up status' characteristics
        status.setWidth( TicTacToe.SIDE * w );
        status.setHeight( w );
        status.setGravity( Gravity.CENTER );
        status.setBackgroundColor( Color.GREEN );
        status.setTextSize( ( int ) ( w * .15 ) );
        status.setText( tttGame.result( ) );

        gridLayout.addView( status );

        // Set gridLayout as the View of this Activity
        setContentView( gridLayout );
    }

    public void update( int row, int col ) {
        Log.d("", "In the update function");
        int play = tttGame.play( row, col );
        if (play != 0) {
            if (play == 1) {
                Log.d("", "Updating button with X");
                buttons[row][col].setText("X");
            } else if (play == 2) {
                Log.d("", "Updating button with O");
                buttons[row][col].setText("O");
            }
            if (tttGame.isGameOver()) {
                status.setBackgroundColor(Color.RED);
                enableButtons(false);
                status.setText(tttGame.result());
                showNewGameDialog();  // offer to play again
            } else {
                Log.d("", "Game not over, player is: " + isPlayerTurn);
                isPlayerTurn = !isPlayerTurn;
                updateTurnStatus();
            }
        } else {
            updateTurnStatus();
            int move = row * 3 + col;
            sendMove(move);

        }
    }

    public void enableButtons( boolean enabled ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setEnabled( enabled );
    }

    public void resetButtons( ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setText( "" );
    }

    public void showNewGameDialog( ) {
        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle( tttGame.result() );
        alert.setMessage( "Do You Want To Play Again?" );
        PlayDialog playAgain = new PlayDialog( );
        alert.setPositiveButton( "YES", playAgain );
        alert.setNegativeButton( "NO", playAgain );
        alert.show();
    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("button clicked", "button clicked");

            for (int row = 0; row < TicTacToe.SIDE; row++) {
                for (int column = 0; column < TicTacToe.SIDE; column++) {
                    if (v == buttons[row][column]) {
                        if (isPlayerTurn) {
                            // Converts the row and col to an integer
                            int move = row * 3 + column;
                            sendMove(move);
                            update(row, column);

                        }
                        return;
                    }
                }
            }
        }
    }


    private class PlayDialog implements DialogInterface.OnClickListener {
        public void onClick( DialogInterface dialog, int id ) {
            if( id == -1 ) /* YES button */ {
                int currentPlayer = tttGame.getPlayer();
                if (currentPlayer == 1) {
                    tttGame.setPlayer(2);
                } else if (currentPlayer == 2) {
                    tttGame.setPlayer(1);
                }
                tttGame.resetGame( );
                enableButtons( true );
                resetButtons( );
                status.setBackgroundColor( Color.GREEN );
                status.setText( tttGame.result( ) );
                isPlayerTurn = (tttGame.getPlayer() == 1);
                updateTurnStatus();
            }
            else if( id == -2 ) // NO button
                MainActivity.this.finish( );
        }
    }
}