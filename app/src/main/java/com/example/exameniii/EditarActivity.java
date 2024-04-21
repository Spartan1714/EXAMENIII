package com.example.exameniii;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class EditarActivity extends AppCompatActivity {

    EditText descripcionEditText, periodistaEditText, fechaEditText;
    ImageView imageView;

    private Uri newimagen;
    DatabaseReference databaseReference;
    MediaPlayer mediaPlayer;
    String entrevistaId; // Variable para almacenar el ID de la entrevista

    // Código de solicitud para la selección de imagen
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("entrevista");

        // Vincular vistas
        descripcionEditText = findViewById(R.id.descripcionEditText);
        periodistaEditText = findViewById(R.id.periodistaEditText);
        fechaEditText = findViewById(R.id.fechaEditText);
        imageView = findViewById(R.id.imageView);

        // Obtener ID de la entrevista a editar
        entrevistaId = getIntent().getStringExtra("entrevistaId");

        // Si no se recibe un ID de entrevista válido, mostrar un mensaje y finalizar la actividad
        if (entrevistaId == null || entrevistaId.isEmpty()) {
            Toast.makeText(this, "ID de entrevista no válido", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad
        } else {
            // Mostrar el ID de la entrevista en una alerta
            mostrarAlerta("ID de la entrevista", "El ID de la entrevista es: " + entrevistaId);

            // Obtener los datos de la entrevista desde Firebase
            databaseReference.child(entrevistaId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Obtener los datos de la entrevista
                        String descripcion = dataSnapshot.child("descripcion").getValue(String.class);
                        String periodista = dataSnapshot.child("periodista").getValue(String.class);
                        String fecha = dataSnapshot.child("fecha").getValue(String.class);
                        String urlImagen = dataSnapshot.child("urlImagen").getValue(String.class);
                        String urlAudio = dataSnapshot.child("urlAudio").getValue(String.class);

                        // Mostrar los datos en los EditText
                        descripcionEditText.setText(descripcion);
                        periodistaEditText.setText(periodista);
                        fechaEditText.setText(fecha);

                        // Mostrar la imagen si hay una URL válida
                        if (urlImagen != null && !urlImagen.isEmpty()) {
                            // Cargar la imagen si hay una URL
                            Glide.with(EditarActivity.this)
                                    .load(urlImagen)
                                    .into(imageView);
                        } else {
                            // Si no hay URL de imagen, ocultar el ImageView
                            imageView.setVisibility(View.GONE);
                        }

                        // Reproducir el audio si hay una URL válida
                        if (urlAudio != null && !urlAudio.isEmpty()) {
                            reproducirAudio(urlAudio);
                        }
                    } else {
                        Toast.makeText(EditarActivity.this, "La entrevista con el ID " + entrevistaId + " no existe", Toast.LENGTH_SHORT).show();
                        finish(); // Finalizar la actividad si la entrevista no existe
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores
                    Toast.makeText(EditarActivity.this, "Error al cargar la entrevista", Toast.LENGTH_SHORT).show();
                    finish(); // Finalizar la actividad en caso de error
                }
            });
        }

        // Configurar el botón "Actualizar Datos"
        Button btnActualizarDatos = findViewById(R.id.btnActualizarDatos);
        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatosFirebase();
            }
        });

        // Configurar el botón "Cambiar Imagen"
        Button btnCambiarImagen = findViewById(R.id.btnCambiarImagen);
        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarImagen();
            }
        });
    }

    private void reproducirAudio(String urlAudio) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(urlAudio);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir el audio", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para mostrar una alerta al usuario
    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada al hacer clic en Aceptar
                    }
                });
        builder.create().show();
    }

    // Método para cambiar la imagen
    private void cambiarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    // Método para manejar el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtener la URI de la imagen seleccionada
            Uri uri = data.getData();
            newimagen=uri;

            // Mostrar la imagen seleccionada en el ImageView
            Glide.with(this)
                    .load(uri)
                    .into(imageView);
        }
    }

    // Método para actualizar los datos en Firebase
    private void actualizarDatosFirebase() {
        // Obtener los datos de los EditText
        String descripcion = descripcionEditText.getText().toString().trim();
        String periodista = periodistaEditText.getText().toString().trim();
        String fecha = fechaEditText.getText().toString().trim();

        if (!descripcion.isEmpty() && !periodista.isEmpty() && !fecha.isEmpty()) {
            // Crear un mapa con los nuevos datos a actualizar
            Map<String, Object> actualizacion = new HashMap<>();
            actualizacion.put("descripcion", descripcion);
            actualizacion.put("periodista", periodista);
            actualizacion.put("fecha", fecha);
            actualizacion.put("urlImagen", newimagen != null ? newimagen.toString() : "");
           // .put("urlImagen", imagenUri != null ? imagenUri.toString() : "");

            // Actualizar los datos en Firebase
            databaseReference.child(entrevistaId).updateChildren(actualizacion)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditarActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarActivity.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar los recursos del MediaPlayer al destruir la actividad
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
