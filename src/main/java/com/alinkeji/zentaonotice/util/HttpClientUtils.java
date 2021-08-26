package com.alinkeji.zentaonotice.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClientUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

  private final static int DEFAULT_RETRY_TIMES = 3;
  private final static int DEFAULT_TIMEOUT = 30000;

  public static CloseableHttpClient getHttpClient() {
    return getHttpClient(DEFAULT_RETRY_TIMES);
  }

  public static CloseableHttpClient getHttpClient(int retryTimes) {
    ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
    LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", plainSocketFactory)
        .register("https", sslSocketFactory).build();

    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
    //设置连接参数
    manager.setMaxTotal(500); // 最大连接数
    manager.setDefaultMaxPerRoute(100); // 路由最大连接数

    //请求失败时,进行请求重试
    HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
      @Override
      public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
        if (i > retryTimes) {
          //重试超过3次,放弃请求
          logger.error("retry has more than 3 time, give up request");
          return false;
        }
        if (e instanceof NoHttpResponseException) {
          //服务器没有响应,可能是服务器断开了连接,应该重试
          logger.error("receive no response from server, retry");
          return true;
        }
        if (e instanceof SSLHandshakeException) {
          // SSL握手异常
          logger.error("SSL hand shake exception");
          return false;
        }
        if (e instanceof InterruptedIOException) {
          //超时
          logger.error("InterruptedIOException");
          return false;
        }
        if (e instanceof UnknownHostException) {
          // 服务器不可达
          logger.error("server host unknown");
          return false;
        }
        if (e instanceof ConnectTimeoutException) {
          // 连接超时
          logger.error("Connection Time out");
          return false;
        }
        if (e instanceof SSLException) {
          logger.error("SSLException");
          return false;
        }

        HttpClientContext context = HttpClientContext.adapt(httpContext);
        HttpRequest request = context.getRequest();
        if (!(request instanceof HttpEntityEnclosingRequest)) {
          //如果请求不是关闭连接的请求
          return true;
        }
        return false;
      }
    };

    CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager)
        .setRetryHandler(handler).build();
    return client;
  }


  public static String post(String url, Object data) {
    return post(url, null, data);
  }

  public static String post(String url, Map<String, String> headers, Object data) {
    return post(url, headers, data, DEFAULT_RETRY_TIMES, DEFAULT_TIMEOUT);
  }

  public static String post(String url, Map<String, String> headers, Object data, int retryTimes,
      int timeout) {
    Pair<Integer, String> pair = request(HttpPost.METHOD_NAME, url, headers, data, retryTimes,
        timeout);
    if (pair.getLeft() >= 300) {
      throw new RuntimeException(
          String.format("request failed: %s: %s", pair.getLeft(), pair.getRight()));
    }
    return pair.getRight();
  }

  public static String put(String url, Object data) {
    return put(url, null, data);
  }

  public static String put(String url, Map<String, String> headers, Object data) {
    Pair<Integer, String> pair = request(HttpPut.METHOD_NAME, url, headers, data);
    if (pair.getLeft() >= 300) {
      throw new RuntimeException(
          String.format("request failed: %s: %s", pair.getLeft(), pair.getRight()));
    }
    return pair.getRight();
  }

  public static String get(String url, Map<String, String> headers) {
    Pair<Integer, String> pair = request(HttpGet.METHOD_NAME, url, headers, null);
    if (pair.getLeft() >= 300) {
      throw new RuntimeException(
          String.format("request failed: %s: %s", pair.getLeft(), pair.getRight()));
    }
    return pair.getRight();
  }

  public static String delete(String url, Map<String, String> headers) {
    Pair<Integer, String> pair = request(HttpDelete.METHOD_NAME, url, headers, null);
    if (pair.getLeft() >= 300) {
      throw new RuntimeException(
          String.format("request failed: %s: %s", pair.getLeft(), pair.getRight()));
    }
    return pair.getRight();
  }

  private static HttpRequestBase buildHttpRequest(String method, String url,
      Map<String, String> headers) {
    HttpRequestBase httpRequest = null;
    if (HttpPost.METHOD_NAME.equals(method)) {
      httpRequest = new HttpPost(url);
    } else if (HttpPost.METHOD_NAME.equals(method)) {
      httpRequest = new HttpPut(url);
    } else if (HttpGet.METHOD_NAME.equals(method)) {
      httpRequest = new HttpGet(url);
    } else if (HttpPut.METHOD_NAME.equals(method)) {
      httpRequest = new HttpPut(url);
    } else if (HttpDelete.METHOD_NAME.equals(method)) {
      httpRequest = new HttpDelete(url);
    }
    return httpRequest;
  }

  public static Pair<Integer, String> request(String method, String url,
      Map<String, String> headers, Object data) {
    return request(method, url, headers, data, DEFAULT_RETRY_TIMES, DEFAULT_TIMEOUT);
  }

  public static Pair<Integer, String> request(String method, String url,
      Map<String, String> headers, Object data, int retryTimes, int timeout) {
    HttpRequestBase httpRequest = buildHttpRequest(method, url, headers);
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getHttpClient(retryTimes);

      //设置请求和传输超时时间
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout)
          .setConnectionRequestTimeout(1000).setConnectTimeout(1000).build();
      httpRequest.setConfig(requestConfig);

      if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
        StringEntity requestEntity = new StringEntity(data == null ? "" : GsonUtil.toJson(data),
            "UTF-8");
        // RFC 7231规范：https://tools.ietf.org/html/rfc7231#section-5.3.4
        // requestEntity.setContentEncoding("UTF-8");
        httpRequest.setHeader("Content-type", "application/json");
        ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(requestEntity);
      }

      CloseableHttpResponse resp = httpClient.execute(httpRequest);
      String content = getResponseString(resp);
      return Pair.of(resp.getStatusLine().getStatusCode(), content);
    } catch (IOException e) {
      logger.info("http request failed {} {} {}", method, url, GsonUtil.toJson(data));
      throw new RuntimeException(e);
    } finally {
      if (httpClient != null) {
        try {
          httpClient.close();
        } catch (Exception e) {
        }
      }
    }
  }

  private static String getResponseString(CloseableHttpResponse resp) {
    try {
      HttpEntity entity = resp.getEntity();
      if (entity == null) {
        return null;
      }
      return EntityUtils.toString(entity);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Makes a http request to the specified endpoint
   */
  public static String request(URL endpointUrl, String httpMethod,
      Map<String, String> headers, String requestBody) {
    HttpURLConnection connection = createHttpConnection(endpointUrl, httpMethod, headers);
    try {
      if (requestBody != null) {
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(requestBody);
        wr.flush();
        wr.close();
      }
    } catch (Exception e) {
      throw new RuntimeException("Request failed. " + e.getMessage(), e);
    }
    return executeHttpRequest(connection);
  }

  private static String executeHttpRequest(HttpURLConnection connection) {
    try {
      // Get Response
      InputStream is;
      try {
        is = connection.getInputStream();
      } catch (IOException e) {
        is = connection.getErrorStream();
      }

      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    } catch (Exception e) {
      throw new RuntimeException("Request failed. " + e.getMessage(), e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private static HttpURLConnection createHttpConnection(URL endpointUrl, String httpMethod,
      Map<String, String> headers) {
    try {
      HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
      connection.setRequestMethod(httpMethod);
      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      return connection;
    } catch (Exception e) {
      throw new RuntimeException("Cannot create connection. " + e.getMessage(), e);
    }
  }

}
