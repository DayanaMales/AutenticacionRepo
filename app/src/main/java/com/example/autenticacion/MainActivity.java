package com.example.autenticacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.autenticacion.Modal.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button btnIniciar,btnRegistrar;
    private EditText etCorreo, etClave;
    private Spinner spnPerfil;
    Usuario usuario= new Usuario();
    FirebaseAuth firebaseAuth;
    FirebaseUser user=null;
    AwesomeValidation awesomeValidation;
    String idUser=null,correo=null;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spnPerfil = (Spinner) findViewById(R.id.spnPerfil);
        String[] opciones = {"SELECCIONE VISTA", "Persona", "Inventario","Registrar","Producto","Listar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones);
        spnPerfil.setAdapter(adapter);
        inicializarControlesAuth();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //findUser(user);

        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.etCorreo, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        awesomeValidation.addValidation(this,R.id.etClave,".{6,}",R.string.invalid_password);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar();
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registrar.class));

            }
        });
    }
    //Conectamos los elemnetos del archivo xml con nuestro mainActivity
    public void inicializarControlesAuth(){
        btnIniciar=findViewById(R.id.btnIniciar);
        btnRegistrar=findViewById(R.id.btnRegistrar);
        etCorreo=findViewById(R.id.etCorreo);
        etClave=findViewById(R.id.etClave);
    }
    //Este metodo busca si existe alguna sesion iniciada
    public void findUser(FirebaseUser user){
        try {
            irHome(user);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    //Este metodo sera usado para acceder al activity Persona si es que existe una session iniciada
    private void irHome(FirebaseUser user) {
        if (user != null) {
            usuario.setNombre(user.getEmail());

            String select = spnPerfil.getSelectedItem().toString();

            if (select.equals("Persona")) {
                Intent i = new Intent(getApplicationContext(), Persona.class);
                i.putExtra("correo", Objects.requireNonNull(user.getEmail()).toString());
                //getIdUser(user.getEmail());
                idUser=user.getUid();
                i.putExtra("idUser",idUser);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }  else if (select.equals("Registrar")) {

                Intent i = new Intent(getApplicationContext(), Registrar.class);
                startActivity(i);
                finish();
            } else if (select.equals("Producto")) {

                Intent i = new Intent(getApplicationContext(), Producto.class);
                startActivity(i);
                finish();
            } else if (select.equals("Listar")) {

                Intent i = new Intent(getApplicationContext(), Listar.class);
                startActivity(i);
                finish();

            }  else if (select.equals("Inventario")) {

                Intent i = new Intent(getApplicationContext(), Inventario.class);
                startActivity(i);
                finish();}
            else {
 Toast.makeText(getApplicationContext(),"Selecciona una vista para ingresar",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Inicia Sesion o Crea una cuenta en nuestra App",Toast.LENGTH_LONG).show();

        }
    }
    //En este metodo iniciamos session con un correo y contraseña ya registrados
    private void iniciar(){


        if(awesomeValidation.validate())   {
            correo=etCorreo.getText().toString();
            String contraseña=etClave.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user=firebaseAuth.getCurrentUser();
                        irHome(user);
                    }else{
                        String errorCode=((FirebaseAuthException)task.getException()).getErrorCode();
                        dameToastdeerror(errorCode);
                    }
                }
            });
        }
    }
    //No usamos este metodo
    public void getIdUser(String correo) {
        mDatabase.child("Usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    idUser=String.valueOf(task.getResult().getValue());
                    Log.d("firebase", idUser);
                }
            }
        });
    }
    // este metodo obtiene los errores al iniciar sesion y lo traduce a un lenguaje entendible
    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(MainActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(MainActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(MainActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                etCorreo.setError("La dirección de correo electrónico está mal formateada.");
                etCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                etClave.setError("la contraseña es incorrecta ");
                etClave.requestFocus();
                etClave.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(MainActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(MainActivity.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(MainActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                etCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                etCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(MainActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(MainActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(MainActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                etClave.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                etClave.requestFocus();
                break;

        }

    }


}