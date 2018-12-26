package com.example.anavia.evaluacion3_ctapasos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.sql.SQLException

class CustomSQL(val context: Context,
                val name: String,
                val factory: SQLiteDatabase.CursorFactory?,
                var version: Int):SQLiteOpenHelper(context,name,factory,version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE Route(id INTEGER PRIMARY KEY AUTOINCREMENT, longitude DOUBLE, latitude DOUBLE)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertar(ubication: Ubication){
        try {
            val db = this.writableDatabase
            //otra forma mas de crear elemento clave valor
            var cv = ContentValues()
            //debe llamarse igual que la columna de la tabla creada
            cv.put("latitude", ubication.latitude)
            cv.put("longitude", ubication.longitude)

            val resultado = db.insert("Route",null,cv)
            db.close()
            if (resultado ==-1L){
                Toast.makeText(context,"Ubicación no agregada", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Ubicación agregada", Toast.LENGTH_SHORT).show()
            }

        }catch (e:SQLException){
            Toast.makeText(context,"Error al insertar ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("sqlListar", e.message)
        }
    }



    fun listar(): ArrayList<Ubication> {
        var lista = ArrayList<Ubication>()
        try {
            val db = this.writableDatabase
            var cursor: Cursor? = null
            cursor = db.rawQuery("select * from Route", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val latitude = cursor.getDouble(1)
                    val longitude = cursor.getDouble(2)
                    val position = Ubication(id, latitude,longitude)
                    lista.add(position)
                } while (cursor.moveToNext())

            }
            db.close()
            return lista
        } catch (e: SQLException) {
            Toast.makeText(context, "Error al listar ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return lista

    }

    fun eliminar(id:Int){
        try {
            val db = this.writableDatabase
            val args = arrayOf(id.toString())
            val resultado = db.delete("Route", "id=?",args)
            db.close()
            if (resultado==0){
                Toast.makeText(context, "no se pudo eliminar", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "eliminado", Toast.LENGTH_SHORT).show()
            }

        }catch (e:SQLException){
            Toast.makeText(context,"Error al eliminar ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

/*    fun count(): ArrayList<Ubication> {
        var lista = ArrayList<Ubication>()
        try {
            val db = this.writableDatabase
            var totalRegister = db.rawQuery("select count(*) from Route", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val latitude = cursor.getDouble(1)
                    val longitude = cursor.getDouble(2)
                    val position = Ubication(id, latitude,longitude)
                    lista.add(position)
                } while (cursor.moveToNext())

            }
            db.close()
            return lista
        } catch (e: SQLException) {
            Toast.makeText(context, "Error al listar ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return lista

    }

    */

}
