package aplicacionesmoviles.avanzado.todosalau.ejemplochat.presenter;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import aplicacionesmoviles.avanzado.todosalau.ejemplochat.model.RegistroModel;
import aplicacionesmoviles.avanzado.todosalau.ejemplochat.view.RegistroContract;

//[Clase que actúa como presentador para la funcionalidad de registro]
public class RegistroPresenter {
    private RegistroContract.View view; // Interfaz de vista que maneja la interacción con la interfaz de usuario
    private RegistroModel model; // Modelo que maneja la lógica de negocio y las operaciones de datos

    // Constructor del presentador que recibe la vista y el modelo
    public RegistroPresenter(RegistroContract.View view, RegistroModel model) {
        this.view = view;
        this.model = model;
    }

    // Método para registrar un usuario
    public void registerUser() {
        String name = view.getName(); // Obtiene el nombre del usuario desde la vista
        String email = view.getEmail(); // Obtiene el correo electrónico del usuario desde la vista
        String password = view.getPassword(); // Obtiene la contraseña del usuario desde la vista
        Uri imagePath = view.getImagePath(); // Obtiene la ruta de la imagen del usuario desde la vista

        // Verifica si algún campo está vacío
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || imagePath == null) {
            view.showToast("Por favor, completa todos los campos"); // Muestra un mensaje de advertencia en la vista
            return; // Sale del método si algún campo está vacío
        }

        // Verifica si la contraseña tiene al menos 6 caracteres
        if (password.length() < 6) {
            view.showToast("La contraseña debe tener al menos 6 caracteres"); // Muestra un mensaje de advertencia en la vista
            return; // Sale del método si la contraseña es demasiado corta
        }

        // Llama al método del modelo para registrar al usuario en Firebase Authentication
        model.registerUser(email, password, new RegistroModel.RegistroCallback() {
            @Override
            public void onSuccess(Object result) {
                FirebaseUser user = (FirebaseUser) result; // Obtiene el usuario registrado
                Map<String, Object> userData = new HashMap<>(); // Crea un mapa para almacenar los datos del usuario
                userData.put("name", name); // Agrega el nombre del usuario al mapa
                userData.put("email", email); // Agrega el correo electrónico del usuario al mapa

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imageRef = storageRef.child("images/" + user.getUid() + ".jpg");  // Crea una referencia de almacenamiento para la imagen del usuario

                UploadTask uploadTask = imageRef.putFile(imagePath);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri-> {
                        String imageUrl = uri.toString();
                        userData.put("profilePicture", imageUrl);

                        // Llama al método del modelo para almacenar los datos del usuario en Firestore
                        model.storeUserData(user, userData, new RegistroModel.RegistroCallback() {
                            @Override
                            public void onSuccess(Object result) {
                            view.showToast("Registro exitoso"); // Muestra un mensaje de éxito en la vista
                            view.clearInputFields(); // Limpia los campos de entrada en la vista
                            view.navigateToLogin(); // Navega a la pantalla de inicio de sesión en la vista
                            }

                            @Override
                            public void onFailure(Exception e) {
                                view.showToast("Error al registrar en Firestore"); // Muestra un mensaje de error en la vista
                            }
                        });
                    });
                }).addOnFailureListener(e -> {
                    view.showToast("Error al subir la imagen");
                });
            }

            @Override
            public void onFailure(Exception e) {
                view.showToast("Error al registrar usuario"); // Muestra un mensaje de error en la vista
            }
        });
    }
}