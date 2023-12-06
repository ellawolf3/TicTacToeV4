package clarkson.ee408.tictactoev4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import clarkson.ee408.tictactoev4.client.SocketClient;
import clarkson.ee408.tictactoev4.model.User;
import clarkson.ee408.tictactoev4.socket.Request;
import clarkson.ee408.tictactoev4.socket.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText displayNameField;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Getting Inputs
        Button registerButton = findViewById(R.id.buttonRegistering);
        Button loginButton = findViewById(R.id.buttonBackToLogin);
        usernameField = findViewById(R.id.editTextUsername);
        passwordField = findViewById(R.id.editTextPassword);
        confirmPasswordField = findViewById(R.id.editTextConfirmPassword);
        displayNameField = findViewById(R.id.editTextDisplayName);

        // TODO: Initialize Gson with null serialization option
        gson = new GsonBuilder().serializeNulls().create();

        //Adding Handlers
        //TODO: set an onclick listener to registerButton to call handleRegister()
        registerButton.setOnClickListener(view -> handleRegister());

        //TODO: set an onclick listener to loginButton to call goBackLogin()
        loginButton.setOnClickListener(view -> goBackLogin());
    }

    /**
     * Process registration input and pass it to {@link #submitRegistration(User)}
     */
    public void handleRegister() {
        // TODO: declare local variables for username, password, confirmPassword and displayName. Initialize their values with their corresponding EditText
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        String displayName = displayNameField.getText().toString();

        // TODO: verify that all fields are not empty before proceeding. Toast with the error message
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, "All text boxes must be filled", Toast.LENGTH_LONG).show();
            return;
        }

        // TODO: verify that password is the same af confirm password. Toast with the error message
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_LONG).show();
            return;
        }

        // TODO: Create User object with username, display name and password and call submitRegistration()
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDisplayName(displayName);
        submitRegistration(user);
    }

    /**
     * Sends REGISTER request to the server
     * @param user the User to register
     */
    void submitRegistration(User user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String userJson = gson.toJson(user);
            Request request = new Request(Request.RequestType.REGISTER, userJson);
            Response response = SocketClient.getInstance().sendRequest(request, Response.class);

            mainThreadHandler.post(() -> {
                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    goBackLogin();
                } else if (response != null) {
                    Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        executor.shutdown();
    }

    /**
     * Change the activity to LoginActivity
     */
    private void goBackLogin() {
        //TODO: Close this activity by calling finish(), it will automatically go back to its parent (i.e,. LoginActivity)
        finish();
    }

}