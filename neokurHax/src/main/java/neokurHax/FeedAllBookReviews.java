package neokurHax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class FeedAllBookReviews {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
		String phpSessionId = args[0];
		String yorum = args[1];
		String startYid = args[2];
		System.out.println("Parametered yorum:" + yorum);
		yorum = StringUtils.newStringUtf8(yorum.getBytes());
		System.out.println("Running with yorum:" + yorum);
		Long yid = Long.parseLong(startYid);
		String proxyIp = args[3];
		String proxyPort = args[4];
		HttpHost proxy = new HttpHost(proxyIp, Integer.parseInt(proxyPort));
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		String url = "http://www.neokur.com/ax/?ne=inceleme_yorum_ekle";

		for (long i = yid; i > 0; i--) {
			HttpClient client = HttpClientBuilder.create().build();

			HttpPost post = new HttpPost(url);
			post.setConfig(config);
			post.setHeader("Cookie", "PHPSESSID=" + phpSessionId + ";");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			post.setHeader("User-Agent", "Linux");
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("metin", yorum));
			urlParameters.add(new BasicNameValuePair("yid", Long.valueOf(i).toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters, UTF8_CHARSET));

			HttpResponse response = client.execute(post);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			try {
				Thread.sleep(500);
				System.out.println("Thread wait completed.");

			} catch (Exception e) {

			}
		}
	}

}
