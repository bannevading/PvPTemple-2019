package server.pvptemple.api.processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import server.pvptemple.api.callback.Callback;
import server.pvptemple.api.callback.ErrorCallback;
import server.pvptemple.api.processor.RequestProcessor;
import server.pvptemple.api.request.Request;

public abstract class AbstractRequestProcessor implements RequestProcessor {
   private final String apiUrl;
   private final String apiKey;

   public JsonElement sendRequest(Request request) {
      return this.sendRequest(request, (Callback)null);
   }

   public void sendRequestAsync(Request request) {
      this.sendRequest(request, (Callback)null, true);
   }

   public void sendRequestAsync(Request request, Callback callback) {
      this.sendRequest(request, callback, true);
   }

   public void sendRequest(Request request, Callback callback, boolean async) {
      if (async) {
         this.runTaskAsynchronously(() -> this.sendRequest(request, callback));
      } else {
         this.sendRequest(request, callback);
      }

   }

   public JsonElement sendRequest(Request request, Callback callback) {
      if (!this.shouldSend()) {
         System.out.println("!!!!!!!!!!!! Attempt on main thread !!!!!!!!!!!!");
         throw new IllegalStateException("Attempted to send an API request on the main thread.");
      } else {
         Map<String, Object> data = request.toMap();
         if (data == null) {
            data = new HashMap();
         }

         List<NameValuePair> parameters = new ArrayList(2);

         for(String key : data.keySet()) {
            Object value = data.get(key);
            parameters.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
         }

         CloseableHttpClient client = HttpClients.createDefault();

         try {
            String url = "http://" + this.apiUrl + "/api/" + this.apiKey + request.getPath();
            HttpPost post = new HttpPost(url.replace(" ", "%20"));
            post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
            CloseableHttpResponse response = null;

            try {
               try {
                  response = client.execute(post);
               } catch (Exception var65) {
                  var65.printStackTrace();
                  if (callback instanceof ErrorCallback) {
                     this.runTask(() -> ((ErrorCallback)callback).onError("Error connecting to " + post.getURI().getHost() + " : " + var65.getMessage()));
                  }

                  System.out.println("Error connecting to: " + url);
                  System.out.println("The issue: " + var65.getMessage());
                  return null;
               }

               StatusLine line = response.getStatusLine();
               BufferedReader reader;
               if (line != null) {
                  int code = line.getStatusCode();
                  if (code != 200) {
                     if (callback instanceof ErrorCallback) {
                        this.runTask(() -> ((ErrorCallback)callback).onError("Error code: " + code));
                     }

                     System.out.println("Error code: " + code);
                     System.out.println("From request: " + url);
                     return null;
                  }
               }
               if (response.getEntity() == null) {
                  return null;
               } else {
                  try {
                     reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                     Throwable e = null;

                     try {
                        JsonParser parser = new JsonParser();
                        JsonElement object = parser.parse((Reader)reader);
                        if (callback != null) {
                           this.runTask(() -> callback.callback(object));
                        }

                        JsonElement var15 = object;
                        return var15;
                     } catch (Throwable var62) {
                        e = var62;
                        throw var62;
                     } finally {
                        if (reader != null) {
                           if (e != null) {
                              try {
                                 reader.close();
                              } catch (Throwable var61) {
                                 e.addSuppressed(var61);
                              }
                           } else {
                              reader.close();
                           }
                        }

                     }
                  } catch (ParseException var64) {
                     var64.printStackTrace();
                     if (callback instanceof ErrorCallback) {
                        this.runTask(() -> ((ErrorCallback)callback).onError("Error parsing Json: " + var64.getMessage()));
                     }

                     return null;
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
               return null;
            } finally {
               if (response != null) {
                  response.close();
               }

            }
         } catch (Exception e) {
            e.printStackTrace();
            if (callback instanceof ErrorCallback) {
               this.runTask(() -> ((ErrorCallback)callback).onError("Unknown error: " + e.getMessage()));
            }

            return null;
         } finally {
            try {
               client.close();
            } catch (IOException e) {
               e.printStackTrace();
            }

         }
      }
   }

   @ConstructorProperties({"apiUrl", "apiKey"})
   public AbstractRequestProcessor(String apiUrl, String apiKey) {
      this.apiUrl = apiUrl;
      this.apiKey = apiKey;
   }

   public String getApiUrl() {
      return this.apiUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }
}
