package server.pvptemple.oldapi;

import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.pvptemple.oldapi.APIMessage;
import server.pvptemple.oldapi.request.RequestCallback;

public abstract class AbstractRequestManager {
   private final String apiUrl;
   private final String apiKey;

   public abstract boolean shouldSend();

   public abstract void runTask(Runnable var1);

   public abstract void runTaskAsynchronously(Runnable var1);

   public void sendRequest(server.pvptemple.oldapi.APIMessage message, RequestCallback callback) {
      this.sendRequest(message, callback, true);
   }

   public JSONObject sendRequestNow(server.pvptemple.oldapi.APIMessage message) {
      if (!this.shouldSend()) {
         try {
            throw new Exception("Request was sent on the main thread");
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      } else {
         Map<String, Object> encoded = new HashMap();
         encoded.put("message", message.getChannel());
         encoded.put("api-key", this.apiKey);
         encoded.putAll(message.toMap());
         HttpClient httpclient = HttpClients.createDefault();
         List<NameValuePair> params = new ArrayList(2);

         for(String key : encoded.keySet()) {
            Object value = encoded.get(key);
            params.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
         }

         try {
            HttpGet httpGet = new HttpGet("http://" + this.apiUrl + "/api?" + URLEncodedUtils.format(params, "utf-8"));

            HttpResponse response;
            try {
               response = httpclient.execute(httpGet);
            } catch (Exception e) {
               e.printStackTrace();
               return null;
            }

            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
               int code = statusLine.getStatusCode();
               if (code != 200) {
                  return null;
               }
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
               try {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                  Throwable var10 = null;

                  JSONObject var12;
                  try {
                     JSONParser parser = new JSONParser();
                     var12 = (JSONObject)parser.parse(reader);
                  } catch (Throwable var26) {
                     var10 = var26;
                     throw var26;
                  } finally {
                     if (reader != null) {
                        if (var10 != null) {
                           try {
                              reader.close();
                           } catch (Throwable var25) {
                              var10.addSuppressed(var25);
                           }
                        } else {
                           reader.close();
                        }
                     }

                  }

                  return var12;
               } catch (ParseException e) {
                  e.printStackTrace();
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }

         return null;
      }
   }

   public void sendRequest(APIMessage message, RequestCallback callback, boolean async) {
      Map<String, Object> encoded = new HashMap();
      encoded.put("message", message.getChannel());
      encoded.put("api-key", this.apiKey);
      encoded.putAll(message.toMap());
      if (async) {
         this.runTaskAsynchronously(() -> this.handleRequest(callback, encoded));
      } else {
         this.handleRequest(callback, encoded);
      }

   }

   private void handleRequest(RequestCallback callback, Map<String, Object> encoded) {
      HttpClient httpclient = HttpClients.createDefault();
      List<NameValuePair> params = new ArrayList(2);

      for(String key : encoded.keySet()) {
         Object value = encoded.get(key);
         params.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
      }

      try {
         HttpGet httpGet = new HttpGet("http://" + this.apiUrl + "/api?" + URLEncodedUtils.format(params, "utf-8"));

         HttpResponse response;
         try {
            response = httpclient.execute(httpGet);
         } catch (Exception e) {
            e.printStackTrace();
            this.runTask(() -> callback.error("Error connecting to " + httpGet.getURI().getHost() + " : " + e.getMessage()));
            return;
         }

         StatusLine statusLine = response.getStatusLine();
         if (statusLine != null) {
            int code = statusLine.getStatusCode();
            if (code != 200) {
               this.runTask(() -> callback.error("Request error code " + code));
               return;
            }
         }

         HttpEntity entity = response.getEntity();
         if (entity != null) {
            try {
               BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
               Throwable var10 = null;

               try {
                  JSONParser parser = new JSONParser();
                  JSONObject jsonObject = (JSONObject)parser.parse(reader);
                  this.runTask(() -> callback.callback(jsonObject));
               } catch (Throwable var23) {
                  var10 = var23;
                  throw var23;
               } finally {
                  if (reader != null) {
                     if (var10 != null) {
                        try {
                           reader.close();
                        } catch (Throwable var22) {
                           var10.addSuppressed(var22);
                        }
                     } else {
                        reader.close();
                     }
                  }

               }
            } catch (ParseException e) {
               e.printStackTrace();
               this.runTask(() -> callback.error("ParseException: " + e.getMessage()));
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
         this.runTask(() -> callback.error("IOException: " + e.getMessage()));
      }

   }

   @ConstructorProperties({"apiUrl", "apiKey"})
   public AbstractRequestManager(String apiUrl, String apiKey) {
      this.apiUrl = apiUrl;
      this.apiKey = apiKey;
   }
}
