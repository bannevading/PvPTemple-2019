package server.pvptemple.api.callback;

import server.pvptemple.api.callback.Callback;
import server.pvptemple.api.callback.ErrorCallback;

import java.beans.ConstructorProperties;
import java.util.logging.Logger;

public abstract class AbstractCallback implements Callback, ErrorCallback {
   private final String errorMessage;
   private boolean errorCalled = false;

   public void onError(String message) {
      this.errorCalled = true;
      if (!this.errorMessage.isEmpty()) {
         Logger.getGlobal().severe(this.errorMessage);
      }

      Logger.getGlobal().severe(message);
   }

   public void throwException() throws Exception {
      if (this.errorCalled) {
         throw new Exception(this.errorMessage);
      }
   }

   @ConstructorProperties({"errorMessage"})
   public AbstractCallback(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public boolean isErrorCalled() {
      return this.errorCalled;
   }
}
