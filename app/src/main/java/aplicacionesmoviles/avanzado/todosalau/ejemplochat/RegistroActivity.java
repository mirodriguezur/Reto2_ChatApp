package aplicacionesmoviles.avanzado.todosalau.ejemplochat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import aplicacionesmoviles.avanzado.todosalau.ejemplochat.presenter.RegistroPresenter;
import aplicacionesmoviles.avanzado.todosalau.ejemplochat.model.RegistroModel;
import aplicacionesmoviles.avanzado.todosalau.ejemplochat.view.RegistroContract;
/**
 * Esta clase representa la actividad de registro de la aplicación.
 */
public class RegistroActivity extends AppCompatActivity implements RegistroContract.View {

    // Componentes de la interfaz de usuario
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnRegister;
    private TextView textViewLogin;
    private ImageView userImageView;
    private FloatingActionButton addImageButton;
    private Uri imagePath;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Presentador
    private RegistroPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de componentes de la interfaz de usuario
        editTextName = findViewById(R.id.usernameEditText);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        btnRegister = findViewById(R.id.registerButton);
        textViewLogin = findViewById(R.id.loginText);
        userImageView = findViewById(R.id.userImage);
        addImageButton = findViewById(R.id.addUserImageButton);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();if (data != null && data.getData() != null) {
                            Uri imagePath = data.getData();
                            this.imagePath = imagePath;
                            Glide.with(this)
                                    .load(imagePath)
                                    .into(userImageView);
                        }
                    }
                });

        // Creación del presentador
        presenter = new RegistroPresenter(this, new RegistroModel());

        // Configuración de los listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.registerUser();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(Intent.createChooser(intent, "Selecciona una imagen"));
    }

    @Override
    public void showToast(String message) {
        // Mostrar un mensaje de tostada
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearInputFields() {
        // Limpiar los campos de entrada
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
    }

    @Override
    public void navigateToLogin() {
        // Navegar a la actividad de inicio de sesión
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public String getName() {
        // Obtener el nombre ingresado por el usuario
        return editTextName.getText().toString().trim();
    }

    @Override
    public String getEmail() {
        // Obtener el correo electrónico ingresado por el usuario
        return editTextEmail.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        // Obtener la contraseña ingresada por el usuario
        return editTextPassword.getText().toString().trim();
    }

    @Override
    public Uri getImagePath() {
        return imagePath;
    }
}