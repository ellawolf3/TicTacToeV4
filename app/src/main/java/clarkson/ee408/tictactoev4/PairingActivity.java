package clarkson.ee408.tictactoev4;

import static clarkson.ee408.tictactoev4.model.Event.EventStatus.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import clarkson.ee408.tictactoev4.client.SocketClient;
import clarkson.ee408.tictactoev4.model.Event;
import clarkson.ee408.tictactoev4.model.User;
import clarkson.ee408.tictactoev4.socket.PairingResponse;
import clarkson.ee408.tictactoev4.socket.Request;
import clarkson.ee408.tictactoev4.socket.Response;

public class PairingActivity extends AppCompatActivity {

    private final String TAG = "PAIRING";

    private Gson gson;

    private TextView noAvailableUsersText;
    private RecyclerView recyclerView;
    private AvailableUsersAdapter adapter;

    private Handler handler;
    private Runnable refresh;

    private boolean shouldUpdatePairing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        Log.e(TAG, "App is now created");
        // TODO: setup Gson with null serialization option
        gson = new GsonBuilder().serializeNulls().create();

        //Setting the username text
        TextView usernameText = findViewById(R.id.text_username);
        // TODO: set the usernameText to the username passed from LoginActivity (i.e from Intent)
        Intent intent = getIntent();
        if (intent.hasExtra("Username")) {
            String username = intent.getStringExtra("Username");
            usernameText.setText(username);
        }

        //Getting UI Elements
        noAvailableUsersText = findViewById(R.id.text_no_available_users);
        recyclerView = findViewById(R.id.recycler_view_available_users);

        //Setting up recycler view adapter
        adapter = new AvailableUsersAdapter(this, this::sendGameInvitation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateAvailableUsers(null);

        handler = new Handler();
        refresh = () -> {
            // TODO: call getPairingUpdate if shouldUpdatePairing is true
            if (shouldUpdatePairing) {
                getPairingUpdate();
            }
            handler.postDelayed(refresh, 1000);
        };
        handler.post(refresh);
    }

    /**
     * Send UPDATE_PAIRING request to the server
     */
    private void getPairingUpdate() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // TODO:  Send an UPDATE_PAIRING request to the server. If SUCCESS call handlePairingUpdate(). Else, Toast the error
            Request request = new Request(Request.RequestType.UPDATE_PAIRING, "");
            PairingResponse response = SocketClient.getInstance().sendRequest(request, PairingResponse.class);

            mainThreadHandler.post(() -> {
                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    handlePairingUpdate(response);
                } else if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        executor.shutdown();
    }

    /**
     * Handle the PairingResponse received form the server
     * @param response PairingResponse from the server
     */
    private void handlePairingUpdate(PairingResponse response) {
        // TODO: handle availableUsers by calling updateAvailableUsers()

        if (response.getAvailableUsers() != null) {
            updateAvailableUsers(response.getAvailableUsers());
        }

        // TODO: handle invitationResponse. First by sending acknowledgement calling sendAcknowledgement()

        if (response.getInvitationResponse() != null) {
            // Send an acknowledgement regardless of the invitation response
            sendAcknowledgement(response.getInvitationResponse());

            // --TODO: If the invitationResponse is ACCEPTED, Toast an accept message and call beginGame
            // --TODO: If the invitationResponse is DECLINED, Toast a decline message

            if (response.getInvitationResponse().getStatus() == Event.EventStatus.ACCEPTED) { // Toast a message indicating acceptance
                Toast.makeText(this, "Invitation Accepted", Toast.LENGTH_SHORT).show();
                beginGame(response.getInvitation(), 1);
            } else if (response.getInvitationResponse().getStatus() == Event.EventStatus.DECLINED) {// Toast a message indicating declination
                Toast.makeText(this, "Invitation Declined", Toast.LENGTH_SHORT).show();
            }
        }

        // TODO: handle invitation by calling createRespondAlertDialog()

        if (response.getInvitation() != null) {
            createRespondAlertDialog(response.getInvitation());
        }
    }

    /**
     * Updates the list of available users
     * @param availableUsers list of users that are available for pairing
     */
    public void updateAvailableUsers(List<User> availableUsers) {
        adapter.setUsers(availableUsers);
        if (adapter.getItemCount() <= 0) {
            // TODO show noAvailableUsersText and hide recyclerView
            noAvailableUsersText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // TODO hide noAvailableUsersText and show recyclerView
            noAvailableUsersText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sends game invitation to an
     * @param userOpponent the User to send invitation to
     */
    private void sendGameInvitation(User userOpponent) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String userJson = gson.toJson(userOpponent.getUsername());
            Request request = new Request(Request.RequestType.SEND_INVITATION, userJson);
            Response response = SocketClient.getInstance().sendRequest(request, Response.class);

            mainThreadHandler.post(() -> {
                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    Toast.makeText(this, "Game invitation sent", Toast.LENGTH_LONG).show();
                } else if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        executor.shutdown();
    }

    /**
     * Sends an ACKNOWLEDGE_RESPONSE request to the server
     * Tell server i have received accept or declined response from my opponent
     */
    private void sendAcknowledgement(Event invitationResponse) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            String invitation = gson.toJson(invitationResponse.getEventId());
            Request request = new Request(Request.RequestType.ACKNOWLEDGE_RESPONSE, invitation);
            SocketClient.getInstance().sendRequest(request, Response.class);
        });
        executor.shutdown();
    }


    /**
     * Create a dialog showing incoming invitation
     * @param invitation the Event of an invitation
     */
    private void createRespondAlertDialog(Event invitation) {
        // TODO: set shouldUpdatePairing to false
        shouldUpdatePairing = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Game Invitation");
        builder.setMessage(invitation.getSender() + " has Requested to play with You");
        builder.setPositiveButton("Accept", (dialogInterface, i) -> acceptInvitation(invitation));
        builder.setNegativeButton("Decline", (dialogInterface, i) -> declineInvitation(invitation));
        builder.show();
    }

    /**
     * Sends an ACCEPT_INVITATION to the server
     * @param invitation the Event invitation to accept
     */
    private void acceptInvitation(Event invitation) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String accept = gson.toJson(invitation.getEventId());
            Request request = new Request(Request.RequestType.ACCEPT_INVITATION, accept);
            Response response = SocketClient.getInstance().sendRequest(request, Response.class);

            mainThreadHandler.post(() -> {
                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    beginGame(invitation, 2);
                } else if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        executor.shutdown();
    }


    /**
     * Sends an DECLINE_INVITATION to the server
     * @param invitation the Event invitation to decline
     */
    private void declineInvitation(Event invitation) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String decline = gson.toJson(invitation.getEventId());
            Request request = new Request(Request.RequestType.DECLINE_INVITATION, decline);
            Response response = SocketClient.getInstance().sendRequest(request, Response.class);

            mainThreadHandler.post(() -> {
                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    Toast.makeText(this, "Declined the invitation", Toast.LENGTH_LONG).show();
                } else if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                }

                // Set shouldUpdatePairing to true after DECLINE_INVITATION is sent.
                shouldUpdatePairing = true;
            });
        });
        executor.shutdown();
    }


    /**
     *
     * @param pairing the Event of pairing
     * @param player either 1 or 2
     */
    private void beginGame(Event pairing, int player) {
        // TODO: set shouldUpdatePairing to false
        shouldUpdatePairing = false;

        // TODO: start MainActivity and pass player as data
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Player", player);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: set shouldUpdatePairing to true
        shouldUpdatePairing = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        // TODO: set shouldUpdatePairing to false
        shouldUpdatePairing = false;

        // TODO: logout by calling close() function of SocketClient
        SocketClient.getInstance().close();
    }

}