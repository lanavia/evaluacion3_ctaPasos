package com.example.anavia.evaluacion3_ctapasos

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LocationListener, OnMapReadyCallback {


    var lm:LocationManager?=null
    var map:GoogleMap?=null
    var latitude =0.0
    var longitude = 0.0
    var saveStep:Boolean = true
    var allPossition:String = ""
    // llave sha1 project
    // 0A:2B:40:D0:E8:BF:49:C5:29:F2:52:67:6D:8B:B7:E3:AB:B7:CD:AF
    // Clave ApiGoogle
    // AIzaSyCpn0xcuusJdaIrB_AmeltZS58Z0V0Hjzg
    val myDB = CustomSQL(this,"myDB", null,1)


    override fun onMapReady(p0: GoogleMap?) {
        map = p0

        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION)

        var granted = true
        for (cadaPermiso in permissions){
            granted = granted and (ActivityCompat.checkSelfPermission(this, cadaPermiso)== PackageManager.PERMISSION_GRANTED)
        }
        if (!granted){
            ActivityCompat.requestPermissions(this,permissions,1)
        }else{
            p0?.isMyLocationEnabled = true
        }

    }
    //otorga los valores del GPS
    override fun onLocationChanged(location: Location?) {

        longitude = location?.longitude.toString().toDouble()
        latitude = location?.latitude.toString().toDouble()

        lblLongitud.text = location?.longitude.toString()
        lblLatitud.text = location?.latitude.toString()

        var marcador = LatLng(latitude,longitude)
        map?.addMarker(MarkerOptions().position(marcador))

        //actual ubication
        var geoCoder = Geocoder(this)
        var getActualAddress = geoCoder.getFromLocation(latitude,longitude,1)


        btnBeginSteps.setOnClickListener {
            Toast.makeText(this,"Inicio contador de pasos", Toast.LENGTH_SHORT).show()
            allPossition = ""
            if (saveStep) {

                if (getActualAddress.size != 0) {
                    allPossition = allPossition + " -" + getActualAddress.get(0).getAddressLine(0)
                    lblAddress.text = allPossition
                }

                map?.addMarker(MarkerOptions().position(marcador))

                myDB.insertar(Ubication(0, longitude, latitude))
            }
        }

        btnStopSteps.setOnClickListener{
            Toast.makeText(this,"Detener contador de pasos", Toast.LENGTH_SHORT).show()
            saveStep = false
        }

        btnDeleteStepsRoute.setOnClickListener {
            Toast.makeText(this,"Eliminar ruta contador de pasos", Toast.LENGTH_SHORT).show()
            //advise delete data
            var alerts = AlertDialog.Builder(this)
                .setTitle("Con esto eliminará los registros de la bd")
                .setMessage("Está seguro?")

            alerts.setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which ->
                map?.clear()
                for (steps in myDB.listar()){
                    map?.addMarker(MarkerOptions().position(marcador))

                    //delete DB
                    myDB.eliminar(steps.id)
                }

            })

            alerts.setNegativeButton("no", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //lm?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)

        val permisos = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)
        var granted = true
        for (permiso in permisos) {
            granted = granted and (ActivityCompat.checkSelfPermission(
                this,
                permiso
            ) == PackageManager.PERMISSION_GRANTED)

        }
        if(!granted){
            ActivityCompat.requestPermissions(this,permisos,1)
        }else{
            lm?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,
                1f,
                this)
        }

        val fragmentoMapa = supportFragmentManager.findFragmentById(R.id.frgMyMap) as SupportMapFragment
        fragmentoMapa.getMapAsync(this)

        var marcador = LatLng(latitude, longitude)


        //inicialize the first marker
        btnBeginSteps.setOnClickListener {
            Toast.makeText(this,"Inicio contador de pasos", Toast.LENGTH_SHORT).show()
              map?.addMarker(MarkerOptions().position(marcador))
              myDB.insertar(Ubication(0, longitude, latitude))

        }




    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when(requestCode){
            1->{
                lm = getSystemService(Context.LOCATION_SERVICE)as LocationManager
                var granted = true
                for (permiso in permissions){
                    granted = granted and (ActivityCompat.checkSelfPermission(
                        this,
                        permiso
                    ) == PackageManager.PERMISSION_GRANTED)
                }
                if(grantResults.size>0 && granted){
                    lm?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        100,
                        1f,
                        this)
                }else {
                    Toast.makeText(this,"Permiso de GPS requerido", Toast.LENGTH_LONG).show()
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }





    //metodo que se lanza cuando cambia el status del proveedor del servicio
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
     }

    //metodo que se ejecuta cuando se habilita el proveedor del servicio de localizacion
    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {
     }


}
