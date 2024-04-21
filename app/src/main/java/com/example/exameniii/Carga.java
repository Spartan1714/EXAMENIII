package com.example.exameniii;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Carga extends AppCompatActivity {

    private ListView listViewEntrevistas;
    private DatabaseReference databaseReference;
    private List<String> entrevistasList;
    private List<String> entrevistasIds; // Lista para almacenar los IDs de las entrevistas

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("entrevista");

        // Vincular ListView
        listViewEntrevistas = findViewById(R.id.listViewEntrevistas);

        // Inicializar las listas de entrevistas y sus IDs
        entrevistasList = new ArrayList<>();
        entrevistasIds = new ArrayList<>();

        // Adaptador para el ListView
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrevistasList);
        listViewEntrevistas.setAdapter(adapter);

        // Obtener y mostrar las entrevistas de Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entrevistasList.clear();
                entrevistasIds.clear(); // Limpiar la lista de IDs

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String entrevistaId = postSnapshot.getKey(); // Obtener el ID de la entrevista
                    String descripcion = postSnapshot.child("descripcion").getValue(String.class);
                    String periodista = postSnapshot.child("periodista").getValue(String.class);
                    String fecha = postSnapshot.child("fecha").getValue(String.class);

                    entrevistasList.add("Descripción: " + descripcion + "\nPeriodista: " + periodista + "\nFecha: " + fecha);
                    entrevistasIds.add(entrevistaId); // Agregar el ID de la entrevista a la lista
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
                Toast.makeText(Carga.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });

        // Agregar listener para el clic en un elemento del ListView
        listViewEntrevistas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el ID de la entrevista seleccionada
                String entrevistaId = entrevistasIds.get(position);
                // Mostrar un cuadro de diálogo con las opciones de editar y eliminar
                mostrarDialogo(entrevistaId);
            }
        });
    }

    // Método para mostrar un cuadro de diálogo con opciones de editar y eliminar
    private void mostrarDialogo(final String entrevistaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(new CharSequence[]{"Editar", "Eliminar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Editar
                                // Abrir la actividad EditarActivity con el ID de la entrevista
                                Intent intent = new Intent(Carga.this, EditarActivity.class);
                                intent.putExtra("entrevistaId", entrevistaId); // Pasar el ID a EditarActivity
                                startActivity(intent);
                                break;
                            case 1: // Eliminar
                                // Eliminar la entrevista
                                eliminarEntrevista(entrevistaId);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    // Método para eliminar una entrevista
    private void eliminarEntrevista(String entrevistaId) {
        databaseReference.child(entrevistaId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Carga.this, "Entrevista eliminada correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Carga.this, "Error al eliminar la entrevista", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
