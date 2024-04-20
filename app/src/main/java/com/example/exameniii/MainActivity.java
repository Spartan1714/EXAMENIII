package com.example.exameniii;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {

    EditText descripcionEditText, periodistaEditText, fechaEditText;
    Button adjuntarImagenButton, adjuntarAudioButton, guardarButton;
    ImageView imageView;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imagenUri, audioUri;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_AUDIO_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("entrevista");
        //storageReference = FirebaseStorage.getInstance().getReference();

        // Vincular vistas
        descripcionEditText = findViewById(R.id.descripcionEditText);
        periodistaEditText = findViewById(R.id.periodistaEditText);
        fechaEditText = findViewById(R.id.fechaEditText);
        adjuntarImagenButton = findViewById(R.id.adjuntarImagenButton);
        adjuntarAudioButton = findViewById(R.id.adjuntarAudioButton);
        guardarButton = findViewById(R.id.guardarButton);
        imageView = findViewById(R.id.imageView);

        // Acción de los botones
        adjuntarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPictureIntent();
            }
        });

        adjuntarAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioPicker();
            }
        });

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEntrevista();
            }
        });
    }

    private void dispatchSelectPictureIntent() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectPictureIntent.setType("image/*");
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void showAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_AUDIO_PICK);
    }

    private void guardarEntrevista() {
        // Obtener los datos de los EditText
        String descripcion = descripcionEditText.getText().toString().trim();
        String periodista = periodistaEditText.getText().toString().trim();
        String fecha = fechaEditText.getText().toString().trim();

        // Verificar si los campos están vacíos
        if (descripcion.isEmpty() || periodista.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar una clave única para la entrevista
        String entrevistaId = databaseReference.push().getKey();

        // Obtener las URLs de la imagen y el audio
        String urlImagen = obtenerUrlImagen();
        String urlAudio = obtenerUrlAudio();

        // Crear un objeto Entrevista con los datos y las URLs
        Entrevista entrevista = new Entrevista(entrevistaId, descripcion, periodista, fecha, urlImagen, urlAudio);

        // Guardar la entrevista en Firebase Realtime Database
        databaseReference.child(entrevistaId).setValue(entrevista)
                .addOnSuccessListener(aVoid -> {
                    // Limpiar los EditText después de guardar la entrevista
                    descripcionEditText.setText("");
                    periodistaEditText.setText("");
                    fechaEditText.setText("");
                    // Mostrar mensaje de éxito
                    Toast.makeText(MainActivity.this, "Entrevista guardada exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al guardar en la base de datos
                    Toast.makeText(MainActivity.this, "Error al guardar la entrevista en la base de datos", Toast.LENGTH_SHORT).show();
                });
    }

    private String obtenerUrlImagen() {
        // Reemplazar con la lógica real para obtener la URL de la imagen
        if (imagenUri != null) {
            // Aquí se debería subir la imagen a Firebase Storage y obtener la URL de descarga
            // Por ahora, devolvemos una URL falsa para depuración
            return "https://ruta-de-la-imagen-en-storage";
        } else {
            // Si no se ha seleccionado una imagen, devolvemos null
            return null;
        }
    }

    private String obtenerUrlAudio() {
        // Reemplazar con la lógica real para obtener la URL del audio
        if (audioUri != null) {
            // Aquí se debería subir el audio a Firebase Storage y obtener la URL de descarga
            // Por ahora, devolvemos una URL falsa para depuración
            return "https://ruta-del-audio-en-storage";
        } else {
            // Si no se ha seleccionado un audio, devolvemos null
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Obtener la URI de la imagen seleccionada
                imagenUri = data.getData();
                // Mostrar la imagen en el ImageView
                imageView.setImageURI(imagenUri);
            } else if (requestCode == REQUEST_AUDIO_PICK && data != null) {
                // Obtener la URI del audio seleccionado
                audioUri = data.getData();
                // Mostrar mensaje de éxito
                Toast.makeText(this, "Audio seleccionado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
