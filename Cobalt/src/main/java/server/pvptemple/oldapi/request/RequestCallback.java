package server.pvptemple.oldapi.request;

import org.json.simple.JSONObject;

public interface RequestCallback {
   void callback(JSONObject var1);

   void error(String var1);
}
