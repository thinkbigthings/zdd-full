package org.thinkbigthings.zdd.server.scraper.keystone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.User;
import org.thinkbigthings.zdd.server.IntegrationTest;
import org.thinkbigthings.zdd.server.StoreItemRepository;
import org.thinkbigthings.zdd.server.UserService;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.TerpeneAmount;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.randomPersonalInfo;


public class ScrapeToDatabaseIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ScrapeToDatabaseIntegrationTest.class);

    private static List<Item> items;

    // this plugs in just a piece of the running app to our test code
    // we can use it for quickly bootstrapping test data without going through the API
    @Autowired
    private StoreItemRepository itemRepository;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService, @LocalServerPort int randomServerPort) throws IOException {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        Path path = Paths.get("src", "test", "resources", "devon-flower.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);

        Extractor extractor = new Extractor();

        items = extractor.extractItems(content);
    }

    public TerpeneAmount toEntity(org.thinkbigthings.zdd.server.scraper.keystone.TerpeneAmount record) {

        TerpeneAmount terpeneAmount = new TerpeneAmount();

        terpeneAmount.setTerpene(record.terpene());
        terpeneAmount.setTerpenePercent(record.amount());

        return terpeneAmount;
    }

    public StoreItem toEntity(Item record) {

        StoreItem item = new StoreItem();

        item.setCbdPercent(record.cbd());
        item.setPriceDollars(record.priceDollars());
        item.setStrain(record.strain());
        item.setThcPercent(record.thc());
        item.setSubspecies(record.subspecies());
        item.setWeightGrams(record.weightGrams());
        item.setVendor(record.vendor());

        if(item.getWeightGrams() == null) {
            throw new IllegalArgumentException("weight is null");
        }
        Set<TerpeneAmount> terpeneAmounts = record.terpeneAmounts().stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());

        terpeneAmounts.forEach(t -> t.setStoreItem(item));
        item.setTerpeneAmounts(terpeneAmounts);

        return item;
    }

    @Test
    @DisplayName("Store Items")
    public void testSaveScrapedItems() {

        long startCount = itemRepository.count();

        List<StoreItem> storeItems = items.stream()
                .map(this::toEntity)
                .collect(toList());

        itemRepository.saveAll(storeItems);

        long endCount = itemRepository.count();

        assertTrue(endCount > startCount);
    }

}
