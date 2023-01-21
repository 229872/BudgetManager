package pl.lodz.budgetmanager.model;

import java.util.Arrays;
import java.util.Optional;

import pl.lodz.budgetmanager.exceptions.WrongValueException;

public enum Category {
    FOOD("Food"), MEDICINE("Medicine"),
    CLOTH("Cloth"), DETERGENT("Detergent"),
    ACCESORY("Accessory"), COSMETIC("Cosmetic"),
    OTHER("Other");

    private String cateGoryName;

    Category(String cateGoryName) {
        this.cateGoryName = cateGoryName;
    }

    @Override
    public String toString() {
        return cateGoryName;
    }

    public static Category getFromString(String category) {
        return Arrays.stream(Category.values())
                .filter(cat -> cat.cateGoryName.equals(category))
                .findFirst()
                .orElse(null);
    }
}
