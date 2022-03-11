package ru.dzhiblavi.sd.client.stock;

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

    private HttpResponse doRequest(final String method, final Map<String, String> parameters) {
        final HttpResponse response = new HttpResponse();
        try {
            final URL url = new URL(this.url + "/" + method);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            final DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            response.code = con.getResponseCode();
            final StringBuilder content = new StringBuilder();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content
                            .append(inputLine)
                            .append(System.lineSeparator());
                }
                con.disconnect();
                response.response = content.toString();
            }
        } catch (final Exception e) {
            response.code = 500;
            response.response = "Failed to perform a request to stock market: " + e.getMessage();
        }
        return response;
    }

    public double modifyStock(final String stockName, final String companyName, final long quantityDelta, final double priceDelta) {
        final HttpResponse resp = this.doRequest(
                "modify-stock",
                Map.of(
                        "name", stockName,
                        "company", companyName,
                        "qdelta", String.valueOf(quantityDelta),
                        "pdelta", String.valueOf(priceDelta)
                )
        );
        if (resp.code != 200) {
            throw new RuntimeException(resp.response);
        }
        final String[] split = resp.response.split(" ");
        return Double.parseDouble(split[split.length - 1]);
    }

    public double queryPrice(final String stockName) {
        final HttpResponse resp = this.doRequest("stock-info", Map.of());
        if (resp.code != 200) {
            throw new RuntimeException(resp.response);
        }
        final String[] split = resp.response.split(System.lineSeparator());
        for (final String line : split) {
            if (line.contains("'" + stockName + "'")) {
                final String[] splitLine = line.split(" ");
                return Double.parseDouble(splitLine[splitLine.length - 1]);
            }
        }
        throw new IllegalArgumentException("No stock " + stockName + " has been found on market");
    }
}
