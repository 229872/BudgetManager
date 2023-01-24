package pl.lodz.budgetmanager.model;

import java.util.Arrays;

public enum Category {
    FOOD("Food"), MEDICINE("Medicine"),
    CLOTH("Cloth"), DETERGENT("Detergent"),
    ACCESSORY("Accessory"), COSMETIC("Cosmetic"),
    OTHER("Other");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    public static Category getFromString(String category) {
        return Arrays.stream(Category.values())
                .filter(cat -> cat.categoryName.equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);
    }
}
