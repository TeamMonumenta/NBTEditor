package com.goncalomb.bukkit.nbteditor.nbt.variables;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * A convenience class that takes an enum or list of strings as its values. For existing enums, pass the enum class and
 * whatever method gets the appropriately namespaced name to store in NBT, i.e. `Material.class` and `Material::getKey`.
 * For lists of strings, pass a List&lt;String&gt;.
 */
public class EnumVariable extends StringVariable {
    private final List<String> values;

    public <T extends Enum<?>> EnumVariable(String key, Class<T> clazz) {
        this(key, clazz, e -> e.name().toLowerCase(Locale.ROOT));
    }

    public <T extends Enum<?>> EnumVariable(String key, Class<T> clazz, Function<T, String> accessor) {
        super(key);
        values = Arrays.stream(clazz.getEnumConstants()).map(accessor).toList();
        if (values.isEmpty()) {
            throw new IllegalArgumentException("EnumVariable values cannot be empty");
        }
    }

    public EnumVariable(String key, List<String> values) {
        super(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("EnumVariable values cannot be empty");
        }
        this.values = values;
    }

    @Override
    public List<String> getPossibleValues() {
        return values;
    }

    @Override
    public String getFormat() {
        StringBuilder builder = new StringBuilder("String matching one of the following: " + values.getFirst());
        for (int i = 1; i < Math.max(values.size(), 16); i++) { // print first 16 values
            builder.append(", ").append(values.get(i));
        }
        if (values.size() == 17) { // exactly one value left
            builder.append(", ").append(values.getLast());
        } else if (values.size() > 17) {
            builder.append(", ");
            builder.append("<%d more values not shown>".formatted(values.size() - 16));
        }
        return builder.toString();
    }

    @Override
    public Component getFormatComponent() {
        Component component = Component.text("String matching one of the following: " + values.getFirst(), NamedTextColor.YELLOW);
        for (int i = 1; i < Math.max(values.size(), 16); i++) { // print first 16 values
            component = component.append(Component.text(", ")).append(Component.text(values.get(i)));
        }
        if (values.size() == 17) { // exactly one value left
            component = component.append(Component.text(", ")).append(Component.text(values.getLast()));
        } else if (values.size() > 17) {
            component = component.append(Component.text(", "));
            Component hover = Component.text(values.get(16));
            for (int i = 17; i < values.size(); i++) {
                component = component.append(Component.text(", ")).append(Component.text(values.get(i)));
            }
            component = component.append(Component.text(", "));
            Component last = Component.text("<hover to see %d more values>".formatted(values.size() - 16))
                    .hoverEvent(hover);
            component = component.append(last);
        }
        return component;
    }
}
