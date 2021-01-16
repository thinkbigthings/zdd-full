package org.thinkbigthings.zdd.server.scraper.keystone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.TerpeneAmount;
import org.thinkbigthings.zdd.server.entity.Subspecies;
import org.thinkbigthings.zdd.server.entity.Terpene;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class EntityExtractor {

    private static Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    // object mapper is thread safe
    private ObjectMapper mapper = new ObjectMapper();

    private Map<String, Subspecies> labelToEnum = new HashMap<>();

    private TypeReference<List<HashMap<String, String>>> parseType = new TypeReference<>() {};

    public EntityExtractor() {
        labelToEnum.put("Sativa",        Subspecies.SATIVA);
        labelToEnum.put("Sative-Hybrid", Subspecies.SATIVA_HYBRID);
        labelToEnum.put("Hybrid",        Subspecies.HYBRID);
        labelToEnum.put("Indica-Hybrid", Subspecies.INDICA_HYBRID);
        labelToEnum.put("Indica",        Subspecies.INDICA);
        labelToEnum.put("High-CBD",      Subspecies.HIGH_CBD);
    }

    public Optional<BigDecimal> parsePercentageNumber(String percentage) {
        try {
            // DecimalFormat is not thread safe
            DecimalFormat decimalFormat = new DecimalFormat("0.0#%");
            decimalFormat.setParseBigDecimal(true);
            decimalFormat.setMaximumFractionDigits(3);
            decimalFormat.setMultiplier(1); // so the number comes out in units of percent
            return Optional.of((BigDecimal) decimalFormat.parse(percentage));
        }
        catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<BigDecimal> parsePercentageNumber(String key, HashMap<String, String> item) {
        return parsePercentageNumber(item.get(key));
    }

    public Optional<TerpeneAmount> parseTerpeneAmount(Terpene terp, HashMap<String, String> item) {
        return parsePercentageNumber(terp.name().toLowerCase(), item)
                .map(parsedPercentage -> new TerpeneAmount(terp, parsedPercentage));
    }

    public List<TerpeneAmount> extractTerpenes(HashMap<String, String> item) {
        return Arrays.asList(Terpene.values()).stream()
                .flatMap(terp -> parseTerpeneAmount(terp, item).stream())
                .sorted(Comparator.comparing(TerpeneAmount::getTerpenePercent).reversed())
                .collect(toList());
    }

    public String extractStrainFromStrainImg(String strainWithTags) {
        return Jsoup.clean(strainWithTags, Whitelist.none()).trim();
    }

    public Subspecies extractSubspeciesFromStrainImg(String strain) {

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

    public Optional<StoreItem> extractItem(HashMap<String, String> item) {

        try {
            StoreItem storeItem = new StoreItem();
            storeItem.setSubspecies(extractSubspeciesFromStrainImg(item.get("strain")));
            storeItem.setStrain(extractStrainFromStrainImg(item.get("strain")));
            storeItem.setThcPercent(parsePercentageNumber(item.get("thc")).get());
            storeItem.setCbdPercent(parsePercentageNumber(item.get("cbd")).get());
            storeItem.getTerpeneAmounts().addAll(extractTerpenes(item));
            storeItem.setPriceDollars(parsePrice(item.get("price")).get());
            storeItem.setVendor(item.get("vendor"));
            storeItem.setWeightGrams(parseGrams(item.get("wt")).get());

            storeItem.getTerpeneAmounts().forEach(t -> t.setStoreItem(storeItem));

            return Optional.of(storeItem);
        }
        catch(Exception e) {
            System.out.println("Couldn't parse item " + item);
            return Optional.empty();
        }

    }

    public List<StoreItem> extractItems(String unparsedData) {

        LOG.info("Parsing data: " + unparsedData);

        try {
            var parser = mapper.createParser(unparsedData);
            var items = mapper.readValue(parser, parseType);
            return items.stream()
                    .flatMap(item -> extractItem(item).stream())
                    .collect(toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
