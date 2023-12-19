package kq.miniproject.projectss.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.model.Product;
import kq.miniproject.projectss.repository.WishlistRepo;

@Service
public class WishlistService {

    @Autowired
    WishlistRepo repo;

    private final String lazadaUrl = "https://lazada-datahub.p.rapidapi.com/item_search";
    private final String region = "SG";
    private final String page = "1";

    @Value("${lazada.datahub.apiKey}")
    private String apiKey;

    public List<Object> accessWishlist(Person person, Object wish) {
        return repo.updateWishlist(person, wish);
    }

    public void removeWish(Person person, String id) {
        repo.deleteWish(person, Long.valueOf(id));
    }

    public List<Product> getProducts(String search) throws RestClientException {

        String url = UriComponentsBuilder
                .fromUriString(lazadaUrl)
                .queryParam("q", search)
                .queryParam("region", region)
                .queryParam("page", page)
                .toUriString();

        RequestEntity<Void> request = RequestEntity.get(url)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "lazada-datahub.p.rapidapi.com")
                .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = null;

        response = template.exchange(request, String.class);
        JsonReader jReader = Json.createReader(new StringReader(response.getBody()));
        JsonArray products = jReader.readObject().get("result").asJsonObject().get("resultList").asJsonArray();
        List<Product> productList = new ArrayList<>();

        for (JsonValue product : products) {

            JsonObject item = product.asJsonObject().get("item").asJsonObject();
            productList.add(new Product(
                    item.getString("title"),
                    item.getString("itemUrl"),
                    item.getString("image")));
        }
        
        return productList;
    }
}
