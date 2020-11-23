package org.thinkbigthings.zdd.server.scraper.keystone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Extractor {

    // object mapper is thread safe
    private ObjectMapper mapper = new ObjectMapper();

    private Map<String, Subspecies> labelToEnum = new HashMap<>();

    public Extractor() {
        labelToEnum.put("Sativa",        Subspecies.SATIVA);
        labelToEnum.put("Sative-Hybrid", Subspecies.SATIVA_HYBRID);
        labelToEnum.put("Hybrid",        Subspecies.HYBRID);
        labelToEnum.put("Indica-Hybrid", Subspecies.INDICA_HYBRID);
        labelToEnum.put("Indica",        Subspecies.INDICA);
        labelToEnum.put("High-CBD",      Subspecies.HIGH_CBD);
    }


    public Optional<BigDecimal> parsePercentage(String percentage) {
        try {
            // DecimalFormat is not thread safe
            DecimalFormat decimalFormat = new DecimalFormat("0.0#%");
            decimalFormat.setParseBigDecimal(true);
            return Optional.of((BigDecimal) decimalFormat.parse(percentage));
        }
        catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<BigDecimal> parsePercentage(String key, HashMap<String, String> item) {
        return parsePercentage(item.get(key));
    }

    public Optional<TerpeneAmount> parseTerpeneAmount(Terpene terp, HashMap<String, String> item) {
        return parsePercentage(terp.name().toLowerCase(), item)
                .map(parsedPercentage -> new TerpeneAmount(terp, parsedPercentage));
    }

    public List<TerpeneAmount> extractTerpenes(HashMap<String, String> item) {
        return Arrays.asList(Terpene.values()).stream()
                .flatMap(terp -> parseTerpeneAmount(terp, item).stream())
                .sorted(Comparator.comparing(TerpeneAmount::amount).reversed())
                .collect(toList());
    }

    public String extractStrain(String strain) {
        Document doc = Jsoup.parseBodyFragment(strain);
        return doc.body().textNodes().iterator().next().text().trim();
    }

    public Subspecies extractSubspecies(String strain) {

        Document doc = Jsoup.parseBodyFragment(strain);

        return doc.getElementsByTag("img").stream()
                .map(element -> element.attr("alt"))
                .filter(alt -> labelToEnum.containsKey(alt))
                .map(labelToEnum::get)
                .findFirst()
                .get();
    }

    public Optional<Long> parsePrice(String priceDollars) {

        try {
            // DecimalFormat is not thread safe
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setParseIntegerOnly(true);
            return Optional.of((Long) format.parse(priceDollars));
        }
        catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<BigDecimal> parseGrams(String weight) {

        try {
            // DecimalFormat is not thread safe
            DecimalFormat decimalFormat = new DecimalFormat("0.0#g");
            decimalFormat.setParseBigDecimal(true);
            return Optional.of((BigDecimal) decimalFormat.parse(weight));
        }
        catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<Item> extractItem(HashMap<String, String> item) {

        try {
            Subspecies subspecies = extractSubspecies(item.get("strain"));
            String strain = extractStrain(item.get("strain"));
            BigDecimal thc = parsePercentage(item.get("thc")).get();
            BigDecimal cbd = parsePercentage(item.get("cbd")).get();
            List<TerpeneAmount> terpenes = extractTerpenes(item);
            Long priceDollars = parsePrice(item.get("price")).get();
            String vendor = item.get("vendor");
            BigDecimal weightGrams = parseGrams(item.get("wt")).get();
            return Optional.of(new Item(subspecies, strain, thc, cbd, terpenes, weightGrams, priceDollars, vendor));
        }
        catch(Exception e) {
            System.out.println("Couldn't parse item " + item);
            return Optional.empty();
        }

    }

    public List<Item> extractItems(String unparsedData) {

        var typeRef = new TypeReference<List<List<HashMap<String, String>>>>() {};

        try {
            var parser = mapper.createParser(unparsedData);
            var parsedData = mapper.readValue(parser, typeRef);
            var items = parsedData.stream()
                    .flatMap(sublist -> sublist.stream())
                    .collect(toList());

            return items.stream()
                    .flatMap(item -> extractItem(item).stream())
                    .collect(toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
