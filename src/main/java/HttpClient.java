import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpClient {

    private org.apache.http.client.HttpClient client;

    RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(20000)
            .setConnectTimeout(20000)
            .setConnectionRequestTimeout(20000)
            .build();

    public HttpClient() {

        PoolingHttpClientConnectionManager connManager
                = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(20);
        connManager.setDefaultMaxPerRoute(20);
        HttpHost host = new HttpHost("localhost", 8080);
        connManager.setMaxPerRoute(new HttpRoute(host), 15);

        client = HttpClientBuilder.create().setConnectionManager(connManager).build();

    }

    public Double getAverageSizeTx() {
        String url = "https://blockchain.info/q/avgtxsize/4500";

        HttpGet request = new HttpGet(url);
        HttpResponse response = get(request);

        if(response != null) {
            return Double.parseDouble(readResponseToString(response));
        }

        throw new RuntimeException();
    }

    public HttpResponse get(HttpGet request) {

        request.setConfig(requestConfig);
        HttpResponse response = execute(request);

        return response;
    }
    private HttpResponse execute(HttpUriRequest request) {

        HttpResponse rawResponse = null;

        try {
            rawResponse = client.execute(request);

            if(rawResponse.getStatusLine().getStatusCode() >= 300) {
                throw new IOException();
            }

        } catch (IOException e) {
            System.out.println("Error on collecting data");
        }


        return rawResponse;
    }


    public String readResponseToString(HttpResponse rawResponse) {


        BufferedReader streamReader;
        StringBuilder responseStrBuilder = new StringBuilder();

        try {
            streamReader = new BufferedReader(new InputStreamReader(rawResponse.getEntity().getContent(), "UTF-8"));
            String response;
            while ((response = streamReader.readLine()) != null)
                responseStrBuilder.append(response);

        } catch (IOException e) {
            System.out.println("Error on collecting data");
        }

        return responseStrBuilder.toString();

    }



}
