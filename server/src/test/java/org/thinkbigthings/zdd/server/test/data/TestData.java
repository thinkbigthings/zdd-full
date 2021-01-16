package org.thinkbigthings.zdd.server.test.data;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.Subspecies;
import org.thinkbigthings.zdd.server.entity.Terpene;
import org.thinkbigthings.zdd.server.entity.TerpeneAmount;
import org.thinkbigthings.zdd.server.scraper.keystone.EntityExtractor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static java.util.UUID.randomUUID;

public class TestData {

    private static Random random = new Random();
    private static Faker faker = new Faker(Locale.US, random);

    public static List<StoreItem> readItems() throws IOException  {
        return readItems("devon-flower-20201218.json");
    }

    public static List<StoreItem> readItems(String filename) throws IOException  {

        Path path = Paths.get("src", "test", "resources", filename);
        String content = Files.readString(path, StandardCharsets.UTF_8);

        EntityExtractor extractor = new EntityExtractor();
        return extractor.extractItems(content);
    }

    public static StoreItem randomItem() {
        StoreItem item = new StoreItem();
        item.setWeightGrams(BigDecimal.ONE);
        item.setVendor(faker.company().name());
        item.setStrain(faker.funnyName().name());
        item.setSubspecies(randomSubspecies());
        item.setPriceDollars(randomLong(100));
        item.setThcPercent(randomBigDecimal(25));
        item.setCbdPercent(randomBigDecimal(25));
        item.setTerpeneAmounts(randomTerpenes());
        return item;
    }

    public static Set<TerpeneAmount> randomTerpenes() {
        return Set.of(new TerpeneAmount(randomTerpene(), randomFraction()));
    }

    public static Terpene randomTerpene() {
        return Terpene.values()[random.nextInt(Terpene.values().length)];
    }

    public static Subspecies randomSubspecies() {
        return Subspecies.values()[random.nextInt(Subspecies.values().length)];
    }

    public static Long randomLong(int maxExclusive) {
        return Integer.toUnsignedLong(random.nextInt(maxExclusive));
    }

    public static BigDecimal randomFraction() {
        return randomBigDecimal(10).divide(BigDecimal.TEN).divide(BigDecimal.TEN);
    }

    public static BigDecimal randomBigDecimal(int maxExclusive) {
        return BigDecimal.valueOf(randomLong(maxExclusive));
    }

    public static PersonalInfo randomPersonalInfo() {

        return new PersonalInfo(
                faker.internet().emailAddress(),
                faker.name().name(),
                faker.phoneNumber().phoneNumber(),
                random.nextInt(40) + 150,
                Set.of(randomAddressRecord()));
    }

    public static RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = randomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    public static AddressRecord randomAddressRecord() {

        Address fakerAddress = faker.address();
        return new AddressRecord(fakerAddress.streetAddress(),
                fakerAddress.city(),
                fakerAddress.state(),
                fakerAddress.zipCode());
    }
}
