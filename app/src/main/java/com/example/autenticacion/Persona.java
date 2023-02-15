package com.example.autenticacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.autenticacion.Modal.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Persona extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText etNombre,etCedula,etProvincia,etCorreo;
    private Spinner spnPais;
    private RadioButton rbMasculino,rbFemenino;
    private Button btnActualizar , btnSalir, btnSiguiente;
    private Toolbar customToolbar;
    private TextView tvCorreo,tvId;
    FirebaseAuth firebaseAuth;
    FirebaseUser user=null;
    Usuario usuario=null;
    String id=null;
    String idUser=null;
    String nombre=null;
    String cedula=null;
    String provincia=null;
    String correo=null;
    String pais=null;
    String genero=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        correo = getIntent().getStringExtra("correo");
        id = getIntent().getStringExtra("idUser");
        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);

        iniciarControles();
        customToolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(customToolbar);
        tvCorreo.setText(correo);
        tvId.setText(id);
        getUser(id);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siguiente();
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerValores();
                postUser(usuario);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listactivity,menu);
        return true;
    }
    private String getGenero(){
        String genero=null;
        if(rbFemenino.isChecked()){
            genero="Femenino";

        }else if(rbMasculino.isChecked()){
            genero="Masculino";
        }
        return genero;
    }
    //Conectamos el archivo xml con nuestro activity
    private void iniciarControles(){
        tvId=findViewById(R.id.tvId);
        etNombre=findViewById(R.id.etNombre);
        etCedula=findViewById(R.id.etCedula);
        etProvincia=findViewById(R.id.etProvincia);
        etCorreo=findViewById(R.id.etCorreo);
        spnPais=findViewById(R.id.spnPais);
        String []opciones={"Seleccione su Pais","Ecuador","Colombia","Bolivia","Chile","Argentina"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,opciones);
        spnPais.setAdapter(adapter);
        rbMasculino=findViewById(R.id.rbMasculino);
        rbFemenino=findViewById(R.id.rbFemenino);
        btnActualizar=findViewById(R.id.btnActualizar);
        btnSalir=findViewById(R.id.btnSalir);
        tvCorreo=findViewById(R.id.tvCorreo);
    }
    //Obtenemos los valores ingresados en nuestros campos de texto
    private void obtenerValores(){
        id=tvId.getText().toString();
        nombre= etNombre.getText().toString();
        cedula=etCedula.getText().toString();
        provincia=etProvincia.getText().toString();
        correo=tvCorreo.getText().toString();
        pais=spnPais.getSelectedItem().toString();
        //pais="Ecuador";
        genero=getGenero();
        usuario=new Usuario(id,nombre,cedula,genero,pais,provincia,correo);

    }
    //Aqui actualizamos los datos del usuario
    private void postUser(Usuario usuario){

        HashMap<String, Object> result = new HashMap<>();
        //result.put("id", usuario.getIdUsuario().toString());
        result.put("nombre",usuario.getNombre().toString());
        result.put("genero", usuario.getGenero().toString());
        result.put("cedula",usuario.getCedula().toString());
        result.put("pais", usuario.getPais().toString());
        result.put("provincia", usuario.getProvincia().toString());
        result.put("correo", usuario.getCorreo().toString());
        mDatabase.child("Usuarios").child(id).updateChildren(result);



    }
    /*private void getUser(String id){
        mDatabase.child("pet").child(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name=documentSnapshot.getString("name");
                String age=documentSnapshot.getString("age");
                String color=documentSnapshot.getString("color");
                etNombre.setText(name);
                etEdad.setText(age);
                etColor.setText(color);
                Toast.makeText(getApplicationContext(),"Mis datos",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error al Cargar los datos",Toast.LENGTH_LONG).show();
            }
        });
    }*/
//Este metodo nos permite buscar los datos del usuario que ha iniciado session y los muestra en nuestro activity para poder actulizarlos
    public void getUser(String id) {
        mDatabase.child("Usuarios").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {

                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String name=String.valueOf(task.getResult().child("nombre").getValue());
                    String cedula=String.valueOf(task.getResult().child("cedula").getValue());
                    //String correo=String.valueOf(task.getResult().child("nombre"));
                    String genero=String.valueOf(task.getResult().child("genero").getValue());
                    String pais=String.valueOf(task.getResult().child("pais").getValue());
                    String provincia=String.valueOf(task.getResult().child("provincia").getValue());

                    etNombre.setText(name);
                    etCedula.setText(cedula);
                    setGenero(genero);
                    setPais(pais);
                    etProvincia.setText(provincia);
                    Toast.makeText(getApplicationContext(),"Mis datos",Toast.LENGTH_LONG).show();
                    Log.d("firebase", "findUser");
                }
            }
        });
    }
    private void setGenero(String genero){
        if(genero.equals("Femenino")){
            rbFemenino.setChecked(true);
            rbMasculino.setChecked(false);
        }else{
            rbFemenino.setChecked(false);
            rbMasculino.setChecked(true);

        }
    }

    private void setPais(String pais) {
        switch (pais) {
            case "Ecuador":
                spnPais.setSelection(1);
                break;
            case "Colombia":
                spnPais.setSelection(2);
                break;
            case "Bolivia":
                spnPais.setSelection(3);
                break;
            case "Chile":
                spnPais.setSelection(4);
                break;
            case "Argentina":
                spnPais.setSelection(5);
                break;
        }
    }
    public void siguiente(){
        Intent i = new Intent(getApplicationContext(), Producto.class);
        startActivity(i);
        finish();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.op1){

            Toast.makeText(this,"Productos",Toast.LENGTH_LONG).show();
            Intent i= new Intent(getApplicationContext(),Producto.class);
            i.putExtra("accion","Productos");
            startActivity(i);
        }else if (id==R.id.op2){
            user=FirebaseAuth.getInstance().getCurrentUser();
            correo=user.getEmail();
            idUser=user.getUid();
            Toast.makeText(this,"Persona",Toast.LENGTH_LONG).show();
            Intent i= new Intent(getApplicationContext(),Persona.class);
            i.putExtra("correo", correo);
            i.putExtra("idUser", idUser);
            startActivity(i);
        }else if(id==R.id.op3){
            //SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
            //preferences.edit().clear().commit();
            Toast.makeText(this,"Invntario",Toast.LENGTH_LONG).show();
            Intent i=new Intent(this,Inventario.class);
            startActivity(i);
           // finish();
        }else if(id==R.id.option_4){
            Toast.makeText(this,"Ventas",Toast.LENGTH_LONG).show();
            Intent i=new Intent(this,Ventas.class);
            startActivity(i);
           // finish();
        }
        return super.onOptionsItemSelected(item);
    }
}