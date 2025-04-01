package app.anisan.pomAnalyzer;

import app.anisan.pomAnalyzer.log.Logger;

import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;

public final class VersionUpdater {
	
	public static void main(String[] args) {
		try {
			WebClient webClient = WebClient.create();
	        String responseStr = webClient.get()
	                .uri(URLDecoder.decode("https://search.maven.org/solrsearch/select?q=g:%22io.netty%22+AND+a:%22netty-codec-socks%22&rows=1&wt=json&sort=version+desc")).retrieve()
	                .bodyToMono(String.class).block();
	        System.out.println(responseStr);
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}
	
	public static void updateLatestVersion(POMDependencyObject pOMDependencyObject) throws Throwable {
		String targetURL = "https://search.maven.org/solrsearch/select" + "?q=g:"
                + pOMDependencyObject.getGroupID() + "%20AND%20a:" + pOMDependencyObject.getArtifactID() + "&wt=json&rows=1&sort=version+desc";
        Logger.log(targetURL, App.verbose);
        
        WebClient webClient = WebClient.create();
        String responseStr = webClient.get()
                .uri(URLDecoder.decode(targetURL)).retrieve()
                .bodyToMono(String.class).block();
        
        JSONObject obj = new JSONObject(responseStr);
        String latestVersion = obj.getJSONObject("response").getJSONArray("docs").getJSONObject(0).getString("latestVersion");
        pOMDependencyObject.setLatestVersion(latestVersion);
        Logger.log(pOMDependencyObject.toString(), App.verbose);
	}
}