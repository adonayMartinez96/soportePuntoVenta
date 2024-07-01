package Kernel.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientManager {

    private final HttpClient httpClient;

    public HttpClientManager() {
        this.httpClient = HttpClients.createDefault();
    }

    public String sendPostRequest(String url, Map<String, String> headers, String requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        StringEntity requestEntity = new StringEntity(requestBody, StandardCharsets.UTF_8);
        httpPost.setEntity(requestEntity);

        return executeRequest(httpPost);
    }

    public String sendGetRequest(String url, Map<String, String> headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }

        // Imprimir detalles de la solicitud
        //System.out.println("Detalles de la solicitud (GET): " + httpGet.getRequestLine());
        //System.out.println("Encabezados: " + Arrays.toString(httpGet.getAllHeaders()));

        return executeRequest(httpGet);
    }

    private String executeRequest(HttpRequestBase request) throws IOException {
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                } else {
                    throw new RuntimeException("La respuesta no tiene contenido.");
                }
            } else {
                System.out.println("Detalles de la respuesta: " + response.getStatusLine());

                throw new RuntimeException("Error en la solicitud. Código de estado HTTP: " + response.getStatusLine().getStatusCode() + ". Detalles: " + response.getStatusLine().getReasonPhrase());

            }
        }
    }
}
