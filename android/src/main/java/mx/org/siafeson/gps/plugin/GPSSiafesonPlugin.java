package mx.org.siafeson.gps.plugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

@CapacitorPlugin(
    name = "GPSSiafeson",
    permissions = {
        @Permission(strings = { Manifest.permission.ACCESS_FINE_LOCATION }, alias = "location")
    }
)
public class GPSSiafesonPlugin extends Plugin implements LocationListener {

    private LocationManager locationManager;
    private static final long MIN_TIME_MS = 1000;
    private static final float MIN_DISTANCE_M = 1;
    private Location lastValidLocation;
    private static final float MAX_REASONABLE_SPEED_MPS = 83.3f; // ≈ 300 km/h
    private static final float MAX_DISTANCE_JUMP_METERS = 1000f; // 1 km de salto súbito

    @PluginMethod
    public void startWatch(PluginCall call) {
        Log.i("GPSSiafeson", "Iniciando monitoreo GPS (sin NMEA)");

        try {
            locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                call.reject("Permiso de ubicación no concedido");
                return;
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                this,
                Looper.getMainLooper()
            );

            call.resolve();
        } catch (Exception e) {
            Log.e("GPSSiafeson", "Error al iniciar GPS", e);
            call.reject("Error al iniciar GPS: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GPSSiafeson", "Ubicación recibida: " + location.toString());
        boolean isMock = location.isFromMockProvider(); //Ubicacion simulada

        boolean isJumpDetected = false;
        boolean isSpeedUnrealistic = false;

        if (lastValidLocation != null) {
          float distance = location.distanceTo(lastValidLocation);
          long timeDiffMs = location.getTime() - lastValidLocation.getTime();

          if (timeDiffMs > 0) {
              float speedMps = distance / (timeDiffMs / 1000.0f);
              isSpeedUnrealistic = speedMps > MAX_REASONABLE_SPEED_MPS;
          }

          isJumpDetected = distance > MAX_DISTANCE_JUMP_METERS;
        }

        lastValidLocation = location;

        long gpsTime = location.getTime(); // Esta es la hora del GPS
        sendLocationUpdate(location, gpsTime, isMock, isJumpDetected, isSpeedUnrealistic);
    }

    private void sendLocationUpdate(Location location, long correctTime, boolean isMock, boolean isJumpDetected, boolean isSpeedUnrealistic) {
      try {
          JSObject ret = new JSObject();
          ret.put("latitude", location.getLatitude());
          ret.put("longitude", location.getLongitude());
          ret.put("accuracy", location.getAccuracy());
          ret.put("altitude", location.getAltitude());
          ret.put("speed", location.getSpeed());
          ret.put("bearing", location.getBearing());
          ret.put("timestamp", correctTime);
          ret.put("timeSource", "gps");
          ret.put("provider", location.getProvider());
          ret.put("isMock", isMock);
          ret.put("jumpDetected", isJumpDetected);
          ret.put("unrealisticSpeed", isSpeedUnrealistic);

          // Asegurar que notifyListeners se ejecute en el hilo principal
          if (Looper.myLooper() == Looper.getMainLooper()) {
              notifyListeners("gpsData", ret);
          } else {
              // Si no estamos en el hilo principal, usar Handler para ejecutar en el hilo principal
              new android.os.Handler(Looper.getMainLooper()).post(() -> {
                  notifyListeners("gpsData", ret);
              });
          }
          Log.d("GPSSiafeson", "Datos GPS enviados: " + ret.toString());
      } catch (Exception e) {
          Log.e("GPSSiafeson", "Error al enviar ubicación", e);
      }
    }

    @PluginMethod
    public void stopWatch(PluginCall call) {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }
            call.resolve();
        } catch (Exception e) {
            Log.e("GPSSiafeson", "Error al detener GPS", e);
            call.reject("Error al detener GPS: " + e.getMessage());
        }
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onProviderDisabled(String provider) {}

    @Override
    protected void handleOnDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
