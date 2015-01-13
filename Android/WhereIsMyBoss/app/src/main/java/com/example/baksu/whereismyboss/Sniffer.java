package com.example.baksu.whereismyboss;

import java.util.List;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
*   Klasa odpowiedzialna za skanowanie wszystkich dostępnych access pointów
 */
public class Sniffer{
    WifiManager wifiManager;
    List<ScanResult> wifiScanList;
    String lista[] = null;
    JSONArray arr;
    private List<ScanResult> all;
    private List<ScanResult> numbers;

/**
* Konstruktor wifiManagere'a
 */
    public Sniffer(WifiManager wM) {
        this.wifiManager = wM;
    }
/**
* Metoda odpowiedzialna za przeskanowanie wszystkich dostępnych access pointów w zasięgu sygnału
* i stworzenie na ich podstawie JSONArray w której są JSONObject.
*
 */
    public void startScan()
    {
        wifiManager.startScan();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        lista = new String[wifiScanList.size()];
        arr = new JSONArray();
        for(int i = 0; i< wifiScanList.size(); i++)
        {
            lista[i] = wifiScanList.get(i).toString();
            JSONObject obj = new JSONObject();
            try {
                obj.put("ssid", wifiScanList.get(i).SSID);
                obj.put("bssid", wifiScanList.get(i).BSSID);
                obj.put("level", wifiScanList.get(i).level);
                obj.put("frequency", wifiScanList.get(i).frequency);
                obj.put("timestamp",System.currentTimeMillis());                    //Pobiera obecny czas jaki jest wyświetlony na device
                arr.put(i,obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda odpowiedzialna za wyczyszczenie tablicy
     */
    public void clearArr()
    {
        arr = new JSONArray();
    }

    /**
     * Metoda odpowiedzialna za zebranie jednego skanu i odpowiednie wpisanie go do tablicy
     */
    public void oneScan()
    {
        boolean jest;
        wifiManager.startScan();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        lista = new String[wifiScanList.size()];

        for(int i = 0; i < wifiScanList.size(); i++) {
            jest = false;
            for (int j = 0; j < arr.length(); j++) {
                try {
                    if (wifiScanList.get(i).BSSID.equals(arr.getJSONObject(j).getString("bssid")))
                    {
                        arr.getJSONObject(j).put("level", arr.getJSONObject(j).getInt("level") + wifiScanList.get(i).level);
                        arr.getJSONObject(j).put("raz", arr.getJSONObject(j).getInt("raz") + 1);
                        jest = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(!jest) {
                    JSONObject obj = new JSONObject();
                    obj.put("ssid", wifiScanList.get(i).SSID);
                    obj.put("bssid", wifiScanList.get(i).BSSID);
                    obj.put("level", wifiScanList.get(i).level);
                    obj.put("frequency", wifiScanList.get(i).frequency);
                    obj.put("timestamp", System.currentTimeMillis());                    //Pobiera obecny czas jaki jest wyświetlony na device
                    obj.put("raz", 1);
                    arr.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda odpowiedzialna za wyliczenie średniej mocy sygnału ze wczesniej zebranych danych
     */
    public void averageLevel()
    {
        for (int i = 0; i < arr.length(); i++) {
            try {
                arr.getJSONObject(i).put("level", arr.getJSONObject(i).getInt("level") / arr.getJSONObject(i).getInt("raz"));
                arr.getJSONObject(i).remove("raz");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getList()
    {
        return lista;
    }

    /**
     * Metoda odpowiedzialna za pobranie z klasy listy skanow
     * @return
     */
    public JSONArray getListToSend()
    {
        return arr;
    }

}
