package app.anisan.pomAnalyzer;

import app.anisan.pomAnalyzer.log.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public final class VersionUpdater {
    public static void updateLatestVersion(POMDependencyObject pOMDependencyObject) throws Throwable {

        String targetURL = "https://search.maven.org/solrsearch/select" + "?q=g:"
                + pOMDependencyObject.getGroupID() + "%20AND%20a:" + pOMDependencyObject.getArtifactID() + "&wt=json&rows=1&sort=version+desc";
        Logger.log(targetURL, App.verbose);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        HttpGet request = new HttpGet(targetURL);
        CloseableHttpResponse response = httpClient.execute(request);
        Logger.log(response.getProtocolVersion() + "", App.verbose); // HTTP/1.1
        Logger.log(String.valueOf(response.getStatusLine().getStatusCode()), App.verbose); // 200
        Logger.log(response.getStatusLine().getReasonPhrase(), App.verbose); // OK
        Logger.log(response.getStatusLine().toString(), App.verbose);


        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                JSONObject obj = new JSONObject(result);
                Logger.log(result, App.verbose);
                String latestVersion = obj.getJSONObject("response").getJSONArray("docs").getJSONObject(0).getString("latestVersion");
                pOMDependencyObject.setLatestVersion(latestVersion);
                Logger.log(pOMDependencyObject.toString(), App.verbose);
            }
        } else {
            Logger.error(response.getStatusLine().getStatusCode()); // <>200
            Logger.error(response.getStatusLine().getReasonPhrase()); // OK
            Logger.error(response.getStatusLine().toString());
            Logger.error(targetURL);
        }
    }
}