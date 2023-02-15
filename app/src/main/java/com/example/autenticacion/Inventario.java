package com.example.autenticacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class Inventario extends AppCompatActivity {
    private Toolbar customToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        customToolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(customToolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventario_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.option_1){

            Toast.makeText(this,"Ventas",Toast.LENGTH_LONG).show();
            Intent i= new Intent(getApplicationContext(),Ventas.class);
            i.putExtra("accion","Ventas");
            startActivity(i);
        }else if (id==R.id.option_2){

            Toast.makeText(this,"Compras",Toast.LENGTH_LONG).show();
            Intent i= new Intent(getApplicationContext(),Ventas.class);
            i.putExtra("accion","Compras");
            startActivity(i);
        }else if(id==R.id.option_3){
            //SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
            //preferences.edit().clear().commit();
            Intent i=new Intent(this,Listar.class);
            startActivity(i);
            //finish();
        }else if(id==R.id.option_4){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            Toast.makeText(this,"Cerrando Session",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
