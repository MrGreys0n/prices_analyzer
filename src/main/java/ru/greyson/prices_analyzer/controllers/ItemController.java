package ru.greyson.prices_analyzer.controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.greyson.prices_analyzer.models.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

@Controller
public class ItemController {

//    @GetMapping("/search")
//    public String searchItem(@RequestParam String name, Model model) throws IOException {
//        String url = "https://shops-prices.ru/search/" + name;
//        System.out.println(url);
//        Document doc = Jsoup.connect(url).get();
//
//        // Переход по ссылке "Сравнить цены"
//        String compareLink = doc.select(".mbtn").first().attr("href");
//        Document compareDoc = Jsoup.connect(compareLink).get();
//        Elements offerItems = compareDoc.select(".am_offer_tr");
//
//        // Парсинг информации о каждом товаре
//        List<Item> items = new ArrayList<>();
//        for (Element offerItem : offerItems) {
//            String itemName = offerItem.select(".am_offer_title").text();
//            String itemPrice = offerItem.select(".am_shop_price").text();
//            String itemLink = offerItem.select(".am_shop_more a").attr("href");
//
//            Item item = new Item();
//            item.setName(itemName);
//            item.setPrice(itemPrice);
//            item.setShop(itemLink);
//            items.add(item);
//        }
//
//        model.addAttribute("items", items);
//        return "item-list";
//    }




    @GetMapping("/search")
    public String searchItems(@RequestParam String query, Model model) {
        String baseUrl = "https://justalk.ru/catalog/find/?find=" + query;

        try {
            Document doc = Jsoup.connect(baseUrl).get();
            List<Item> items = parseItems(doc);
            model.addAttribute("items", items);
        } catch (IOException e) {
            e.printStackTrace();
            // handle the error appropriately, e.g., show an error page
        }

        return "item/item-list"; // assuming you have a Thymeleaf template named "item-list.html"
    }

    private List<Item> parseItems(Document doc) {
        List<Item> items = new ArrayList<>();

        Elements productItems = doc.select(".product-item");
        for (Element productItem : productItems) {
            String itemName = productItem.select(".title a").text();
            String itemPrice = productItem.select(".price span").text().replaceAll(" ", " ");
            String itemLink = productItem.select(".title a").attr("href");

            Item item = new Item();
            item.setName(itemName);
            item.setPrice(itemPrice);
            item.setShop("https://justalk.ru" + itemLink);



            items.add(item);
        }
        Collections.sort(items, Comparator.comparingInt(item -> Integer.parseInt(item.getPrice().replaceAll(" ", ""))));
        return items;
    }

}