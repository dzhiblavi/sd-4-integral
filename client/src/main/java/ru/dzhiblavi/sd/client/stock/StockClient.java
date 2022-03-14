package ru.dzhiblavi.sd.client.stock;

import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StockClient {
    private static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params) {
            final StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                result.append("&");
            }
            final String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }

    private static class HttpResponse {
        public String response;
        public int code;
    }

    private final String url;

    public StockClient(final String url) {
        this.url = url;
    }

    private String readStream(final InputStream reader) throws IOException {
        final StringBuilder content = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(reader))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content
                        .append(inputLine)
                        .append(System.lineSeparator());
            }
            return content.toString();
        }
    }

    private HttpResponse doRequest(final String reqMethod, final Map<String, String> parameters, final String method) {
        final HttpResponse response = new HttpResponse();
        try {
            final URL url = new URL(this.url + "/" + reqMethod);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(true);
            final DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            response.code = con.getResponseCode();
            if (response.code != HttpStatus.OK.value()) {
                response.response = readStream(con.getErrorStream());
            } else {
                response.response = readStream(con.getInputStream());
            }
            con.disconnect();

            if (response.code != HttpStatus.OK.value()) {
                throw new RuntimeException("Bad response code " + response.code + ": " + response.response);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Failed to perform a request: " + e.getMessage());
        }
        return response;
    }

    public HttpResponse doGetRequest(final String reqMethod, final Map<String, String> parameters) {
        return this.doRequest(reqMethod, parameters, "GET");
    }

    public HttpResponse doPostRequest(final String reqMethod, final Map<String, String> parameters) {
        return this.doRequest(reqMethod, parameters, "POST");
    }

    public double modifyStock(final String stockName, final String companyName, final long quantityDelta, final double priceDelta) {
        final HttpResponse resp = this.doGetRequest(
                "modify-stock",
                Map.of(
                        "name", stockName,
                        "company", companyName,
                        "qdelta", String.valueOf(quantityDelta),
                        "pdelta", String.valueOf(priceDelta)
                )
        );
        final String[] split = resp.response.split(" ");
        return Double.parseDouble(split[split.length - 1]);
    }

    public double queryPrice(final String stockQualifiedName) {
        final HttpResponse resp = this.doGetRequest("stock-info", Map.of());
        final String[] split = resp.response.split(System.lineSeparator());
        for (final String line : split) {
            if (line.contains("'" + stockQualifiedName + "'")) {
                final String[] splitLine = line.split(" ");
                return Double.parseDouble(splitLine[splitLine.length - 1]);
            }
        }
        throw new IllegalArgumentException("No stock " + stockQualifiedName + " has been found on market");
    }
}
