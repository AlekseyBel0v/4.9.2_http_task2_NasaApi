import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Main {
    public static ObjectMapper mapper = new ObjectMapper();
    private static final String URI_NASA = "https://api.nasa.gov/planetary/apod?api_key=nhiDrKTIBjKFu5ErWXhnJv6zARLv4AMPdCj5CNbE";

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build()
        ) {
            //Создание объекта запроса с произвольными загаловками
            HttpGet request = new HttpGet(URI_NASA);
            //Добавление заголовка формата ответа
            request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            //CloseableHttpResponse response = null;
            CloseableHttpResponse response = null;
            response = httpClient.execute(request);
            //Вывод всех заголовков
            Arrays.stream(response.getAllHeaders()).forEach(System.out::println);
            //Чтение ответа
            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            //System.out.println(body);
            Event event = mapper.readValue(body, new TypeReference<>() {
            });
            String fileName = event.getUrl().split("/")[event.getUrl().split("/").length - 1];
            //System.out.println(fileName);
            URL image = new URL(event.getUrl());
            File file = new File("src/main/resources/" + fileName);
            file.createNewFile();
            Files.copy(image.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
