package sdis.utils;

import com.google.gson.Gson;
public class MiPruebaGson {

    public static void main(String[] args) {


        // Escritura de archivo con objeto HashMap<String, MiObjeto>
        java.util.HashMap<String, String> mapa = new java.util.HashMap<>();
        mapa.put("cllamas", "qwerty");
        mapa.put("hector", "lkjlkj");
        mapa.put("sdis", "987123");
        mapa.put("admin", "$%&/()=");
        String json2 = new Gson().toJson(mapa);

        try {
            java.io.PrintWriter archivo2 = new java.io.PrintWriter(new java.io.File("Usuarios"));
            archivo2.println(json2);
            archivo2.close();
        } catch (java.io.IOException ioe) {}
    }
}
