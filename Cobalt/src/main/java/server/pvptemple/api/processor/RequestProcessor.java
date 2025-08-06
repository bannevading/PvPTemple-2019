package server.pvptemple.api.processor;

import com.google.gson.JsonElement;
import server.pvptemple.api.callback.Callback;
import server.pvptemple.api.request.Request;

public interface RequestProcessor {
   boolean shouldSend();

   void sendRequestAsync(Request var1);

   void sendRequestAsync(Request var1, Callback var2);

   JsonElement sendRequest(Request var1, Callback var2);

   void sendRequest(Request var1, Callback var2, boolean var3);

   JsonElement sendRequest(Request var1);

   void runTaskAsynchronously(Runnable var1);

   void runTask(Runnable var1);
}
