package com.example.myapplication;


import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;

import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class database extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    public interface AuthenticationCallback {
        void onAuthenticationSuccess();

        void onAuthenticationFailure();
    }

    public void storeDataGuessed(String username, String password, Context context) {
        db = FirebaseFirestore.getInstance();
        // Create a new user object
        Map<String, Object> user = new HashMap<>();
        user.put("name", username);
        user.put("password", password);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Data stored in Firestore successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error storing data in Firestore", Toast.LENGTH_SHORT).show());
    }

    public void checkDuplicateUser(String username, CheckDuplicateUserCallback callback) {
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String storedUsername = document.getString("name");
                            if (username.equals(storedUsername)) {
                                // Username exists, invoke callback with true
                                callback.onResult(true);
                                return; // Exit the loop if a match is found
                            }
                        }
                        // Username does not exist, invoke callback with false
                        callback.onResult(false);
                    } else {
                        // Log the exception to identify the issue
                        Log.e("AUTH", "Error getting documents", task.getException());
                        // Invoke callback with an error (you can handle it as needed)
                        callback.onResult(false);
                    }
                });
    }


    public interface CheckDuplicateUserCallback {
        void onResult(boolean isDuplicate);
    }

    public void authenticateUser(String name3, String pwd2, Context context, AuthenticationCallback callback) {
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String storedUsername = document.getString("name");
                            String storedPassword = document.getString("password");

                            if (name3.equals(storedUsername) && pwd2.equals(storedPassword)) {
                                addsharepreference(context,name3);
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                                // Invoke the success callback
                                callback.onAuthenticationSuccess();
                                return; // Exit the loop if a match is found
                            }
                        }
                        // If the loop completes without finding a match
                        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        // Invoke the failure callback
                        callback.onAuthenticationFailure();
                    } else {
                        // Log the exception to identify the issue
                        Log.e("AUTH", "Error getting documents", task.getException());
                        Toast.makeText(context, "Error getting data", Toast.LENGTH_SHORT).show();
                        // Invoke the failure callback
                        callback.onAuthenticationFailure();
                    }
                });
    }


    public void Auth(String idToken, Context context, AuthenticationCallback callback) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            String displayName = user.getDisplayName();
                            String profileUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                            // Check if user already exists in Firestore
                            db.collection("google")
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(documentTask -> {
                                        if (documentTask.isSuccessful()) {
                                            DocumentSnapshot document = documentTask.getResult();
                                            if (document.exists()) {
                                                // User already exists
                                                addsharepreference(context,displayName);
                                                callback.onAuthenticationSuccess();
                                            } else {
                                                // User doesn't exist, add user data to Firestore
                                                HashMap<String, Object> userData = new HashMap<>();
                                                userData.put("id", userId);
                                                userData.put("name", displayName);
                                                userData.put("profile", profileUrl);

                                                db.collection("google")
                                                        .document(userId)
                                                        .set(userData)
                                                        .addOnCompleteListener(writeTask -> {
                                                            if (writeTask.isSuccessful()) {

                                                                callback.onAuthenticationSuccess();
                                                            } else {
                                                                callback.onAuthenticationFailure();
                                                                Toast.makeText(context, "Error storing user data", Toast.LENGTH_SHORT).show();
                                                                Log.e("FIRESTORE", "Error storing user data", writeTask.getException());
                                                            }
                                                        });
                                            }
                                        } else {
                                            callback.onAuthenticationFailure();
                                            Toast.makeText(context, "Error checking user data", Toast.LENGTH_SHORT).show();
                                            Log.e("FIRESTORE", "Error checking user data", documentTask.getException());
                                        }
                                    });
                        }
                    } else {
                        callback.onAuthenticationFailure();
                        // Handle authentication failure if needed
                    }
                });
    }


    public void authenticateFacebookUser(AccessToken accessToken, Context context, AuthenticationCallback callback) {
        firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Check if user already exists in Firestore
                            db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("facebook").document(userId);

                            userRef.get().addOnCompleteListener(documentTask -> {
                                if (documentTask.isSuccessful()) {
                                    DocumentSnapshot document = documentTask.getResult();
                                    if (document.exists()) {
                                        // User already exists
                                        addsharepreference(context, user.getDisplayName());
                                        callback.onAuthenticationSuccess();
                                    } else {
                                        // User doesn't exist, add user data to Firestore
                                        String displayName = user.getDisplayName();
                                        String profileUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                                        HashMap<String, Object> userData = new HashMap<>();
                                        userData.put("id", userId);
                                        userData.put("name", displayName);
                                        userData.put("profile", profileUrl);

                                        db.collection("facebook")
                                                .document(userId)
                                                .set(userData)
                                                .addOnCompleteListener(writeTask -> {
                                                    if (writeTask.isSuccessful()) {
                                                        // User data stored

                                                        callback.onAuthenticationSuccess();
                                                    } else {
                                                        callback.onAuthenticationFailure();
                                                        Toast.makeText(context, "Error storing user data", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }
                                } else {
                                    callback.onAuthenticationFailure();
                                    Toast.makeText(context, "Error checking user data", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    } else {
                        callback.onAuthenticationFailure();
                        // Handle authentication failure if needed
                    }
                });
    }

    public void addsharepreference(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("userName", name); // Replace with actual user name
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public void removesharepreference(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("userName", null);
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }

    public void changepassword(String name, String password, String newpassword,Context c) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("name", name)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Found a matching user, update the password
                            String documentId = document.getId();
                            updatePassword(documentId, newpassword,c);
                        }
                        Toast.makeText(c, "Old Password is wrong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(c,"Sorry this feature only for guest login",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePassword(String documentId, String newPassword,Context context) {
        db.collection("users")
                .document(documentId)
                .update("password", newPassword)
                .addOnSuccessListener(aVoid -> {
                    // Password updated successfully
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle the error if the update fails
                    Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // Print the stack trace for debugging
                });
    }

}
