package isocodes.gen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.gradle.api.logging.Logger;

/** Turns the JSON data of one iso-codes release into Java source files. */
final class JavaGenerator {

    /** Rows per data holder class, chosen to stay well inside the 64 KiB method and constant pool limits. */
    private static final int ROWS_PER_HOLDER = 500;

    private static final ClassName STRING = ClassName.get(String.class);
    private static final ClassName OPTIONAL = ClassName.get(Optional.class);
    private static final ClassName LIST = ClassName.get(List.class);
    private static final ClassName MAP = ClassName.get(Map.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final UpstreamSource source;
    private final String version;
    private final String basePackage;
    private final Logger logger;

    JavaGenerator(UpstreamSource source, String version, String basePackage, Logger logger) {
        this.source = source;
        this.version = version;
        this.basePackage = basePackage;
        this.logger = logger;
    }

    /** One JSON property, as it appears on the generated type. */
    private record Field(String json, String java, boolean required, String description) {
        TypeName accessorType() {
            return required ? STRING : ParameterizedTypeName.get(OPTIONAL, STRING);
        }
    }

    void generateAll(Path outputDir) {
        for (Standard standard : Standard.ALL) {
            List<JsonNode> rows = readRows(standard);
            List<Field> fields = fields(standard, rows);
            for (JavaFile file : generate(standard, rows, fields)) {
                write(file, outputDir);
            }
        }
        write(versionClass(), outputDir);
    }

    private List<JsonNode> readRows(Standard standard) {
        JsonNode root = parse(source.fetchRequired(standard.dataFileName()));
        JsonNode array = root.get(standard.code());
        if (array == null || !array.isArray()) {
            throw new IllegalStateException(standard.dataFileName() + " has no \"" + standard.code() + "\" array");
        }
        List<JsonNode> rows = new ArrayList<>();
        array.forEach(rows::add);
        return rows;
    }

    /**
     * Fields are the union of keys present in the data, ordered as the JSON schema lists them (when the release
     * ships one). A field is required when every row has it.
     */
    private List<Field> fields(Standard standard, List<JsonNode> rows) {
        Map<String, String> descriptions = new LinkedHashMap<>();
        source.fetch(standard.schemaFileName()).map(this::parse).ifPresent(schema -> {
            JsonNode properties = schema.path("properties").path(standard.code()).path("items").path("properties");
            properties.properties().forEach(e -> descriptions.put(e.getKey(), e.getValue().path("description").asText("")));
        });

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (JsonNode row : rows) {
            row.fieldNames().forEachRemaining(name -> counts.merge(name, 1, Integer::sum));
        }
        Set<String> ordered = new LinkedHashSet<>();
        descriptions.keySet().stream().filter(counts::containsKey).forEach(ordered::add);
        ordered.addAll(counts.keySet());

        List<Field> fields = new ArrayList<>();
        for (String json : ordered) {
            String description = descriptions.getOrDefault(json, "").replaceAll("\\s*\\(optional\\)$", "");
            fields.add(new Field(json, javaName(json), counts.get(json) == rows.size(), description));
        }
        return fields;
    }

    private List<JavaFile> generate(Standard standard, List<JsonNode> rows, List<Field> fields) {
        String pkg = basePackage + "." + standard.subPackage();
        ClassName type = ClassName.get(pkg, standard.className());
        List<Field> lookups = lookupFields(standard, rows, fields);

        TypeSpec.Builder builder = standard.kind() == Standard.Kind.ENUM
                ? TypeSpec.enumBuilder(type)
                : TypeSpec.classBuilder(type).addModifiers(Modifier.FINAL);
        builder.addModifiers(Modifier.PUBLIC)
                .addJavadoc("$L\n\n<p>Generated from Debian iso-codes $L ($L entries). Do not edit.\n",
                        standard.summary(), version, rows.size());

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder();
        for (Field field : fields) {
            builder.addField(STRING, field.java(), Modifier.PRIVATE, Modifier.FINAL);
            constructor.addParameter(STRING, field.java());
            constructor.addStatement("this.$N = $N", field.java(), field.java());
            builder.addMethod(accessor(field));
        }
        builder.addMethod(constructor.build());

        List<JavaFile> files = new ArrayList<>();
        if (standard.kind() == Standard.Kind.ENUM) {
            addEnumConstants(builder, standard, rows, fields);
            addLookups(builder, type, lookups, CodeBlock.of("values()"));
        } else {
            files.addAll(addTableData(builder, type, rows, fields));
            addLookups(builder, type, lookups, CodeBlock.of("ALL"));
            addTableObjectMethods(builder, type, standard, fields);
        }
        files.add(0, javaFile(pkg, builder.build()));
        return files;
    }

    private MethodSpec accessor(Field field) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(field.java())
                .addModifiers(Modifier.PUBLIC)
                .returns(field.accessorType());
        String description = field.description().isEmpty() ? "The {@code " + field.json() + "} value" : field.description();
        method.addJavadoc("$L.\n\n@return $L\n", description,
                field.required() ? "the value, never null" : "the value, or empty if this entry has none");
        return field.required()
                ? method.addStatement("return $N", field.java()).build()
                : method.addStatement("return $T.ofNullable($N)", OPTIONAL, field.java()).build();
    }

    private void addEnumConstants(TypeSpec.Builder builder, Standard standard, List<JsonNode> rows, List<Field> fields) {
        Set<String> seen = new HashSet<>();
        for (JsonNode row : rows) {
            String constant = constantName(row.path(standard.keyField()).asText());
            if (!seen.add(constant)) {
                throw new IllegalStateException(standard.dataFileName() + ": duplicate enum constant " + constant);
            }
            builder.addEnumConstant(constant, TypeSpec.anonymousClassBuilder(arguments(row, fields))
                    .addJavadoc("$L.\n", javadocText(row.path("name").asText()))
                    .build());
        }
    }

    private List<JavaFile> addTableData(TypeSpec.Builder builder, ClassName type, List<JsonNode> rows, List<Field> fields) {
        TypeName listType = ParameterizedTypeName.get(LIST, type);
        builder.addField(FieldSpec.builder(listType, "ALL", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build());

        CodeBlock.Builder init = CodeBlock.builder()
                .addStatement("$T list = new $T<>($L)", listType, ArrayList.class, rows.size());
        List<JavaFile> holders = new ArrayList<>();
        for (int start = 0, n = 0; start < rows.size(); start += ROWS_PER_HOLDER, n++) {
            ClassName holder = type.peerClass(type.simpleName() + "Data" + n);
            MethodSpec.Builder addTo = MethodSpec.methodBuilder("addTo")
                    .addModifiers(Modifier.STATIC)
                    .addParameter(listType, "out");
            for (JsonNode row : rows.subList(start, Math.min(start + ROWS_PER_HOLDER, rows.size()))) {
                addTo.addStatement("out.add(new $T($L))", type, arguments(row, fields));
            }
            holders.add(javaFile(type.packageName(), TypeSpec.classBuilder(holder)
                    .addModifiers(Modifier.FINAL)
                    .addJavadoc("Entries $L to $L of {@link $T}.\n", start, Math.min(start + ROWS_PER_HOLDER, rows.size()) - 1, type)
                    .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                    .addMethod(addTo.build())
                    .build()));
            init.addStatement("$T.addTo(list)", holder);
        }
        init.addStatement("ALL = $T.copyOf(list)", LIST);
        builder.addStaticBlock(init.build());

        builder.addMethod(MethodSpec.methodBuilder("all")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(listType)
                .addJavadoc("Returns every entry, in upstream order.\n\n@return an unmodifiable list of all entries\n")
                .addStatement("return ALL")
                .build());
        return holders;
    }

    private void addTableObjectMethods(TypeSpec.Builder builder, ClassName type, Standard standard, List<Field> fields) {
        String key = javaName(standard.keyField());
        builder.addMethod(MethodSpec.methodBuilder("equals")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(boolean.class)
                        .addParameter(Object.class, "other")
                        .addStatement("return other instanceof $T that && $N.equals(that.$N)", type, key, key)
                        .build())
                .addMethod(MethodSpec.methodBuilder("hashCode")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(int.class)
                        .addStatement("return $N.hashCode()", key)
                        .build())
                .addMethod(MethodSpec.methodBuilder("toString")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return $N", key)
                        .build());
    }

    /** Lookup fields whose (non-null) values are unique in this release; others are skipped with a warning. */
    private List<Field> lookupFields(Standard standard, List<JsonNode> rows, List<Field> fields) {
        List<Field> result = new ArrayList<>();
        for (String json : standard.lookupFields()) {
            Optional<Field> field = fields.stream().filter(f -> f.json().equals(json)).findFirst();
            if (field.isEmpty()) {
                continue;
            }
            Set<String> values = new HashSet<>();
            boolean unique = rows.stream()
                    .map(row -> row.get(json))
                    .filter(Objects::nonNull)
                    .allMatch(value -> values.add(value.asText()));
            if (unique) {
                result.add(field.get());
            } else {
                logger.warn("iso-codes {}: {}.{} is not unique, skipping lookup method", version, standard.className(), json);
            }
        }
        return result;
    }

    private void addLookups(TypeSpec.Builder builder, ClassName type, List<Field> lookups, CodeBlock source) {
        TypeName mapType = ParameterizedTypeName.get(MAP, STRING, type);
        CodeBlock.Builder init = CodeBlock.builder();
        for (Field field : lookups) {
            String index = "BY_" + constantName(field.json());
            builder.addField(FieldSpec.builder(mapType, index, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build());
            init.addStatement("$T<$T, $T> $N = new $T<>()", Map.class, STRING, type, index.toLowerCase(), HashMap.class);
        }
        if (lookups.isEmpty()) {
            return;
        }
        init.beginControlFlow("for ($T entry : $L)", type, source);
        for (Field field : lookups) {
            String local = ("BY_" + constantName(field.json())).toLowerCase();
            if (field.required()) {
                init.addStatement("$N.put(entry.$N, entry)", local, field.java());
            } else {
                init.beginControlFlow("if (entry.$N != null)", field.java())
                        .addStatement("$N.put(entry.$N, entry)", local, field.java())
                        .endControlFlow();
            }
        }
        init.endControlFlow();
        for (Field field : lookups) {
            String index = "BY_" + constantName(field.json());
            init.addStatement("$N = $T.copyOf($N)", index, MAP, index.toLowerCase());

            String method = "from" + Character.toUpperCase(field.java().charAt(0)) + field.java().substring(1);
            builder.addMethod(MethodSpec.methodBuilder(method)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ParameterizedTypeName.get(OPTIONAL, type))
                    .addParameter(STRING, field.java())
                    .addJavadoc("Finds the entry whose {@code $L} is exactly the given value.\n\n"
                            + "@param $N the value to look up (case-sensitive)\n"
                            + "@return the matching entry, or empty if there is none\n", field.json(), field.java())
                    .addStatement("return $T.ofNullable($N.get($N))", OPTIONAL, index, field.java())
                    .build());
        }
        builder.addStaticBlock(init.build());
    }

    private JavaFile versionClass() {
        return javaFile(basePackage, TypeSpec.classBuilder("IsoCodes")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Information about the Debian iso-codes release these classes were generated from.\n")
                .addField(FieldSpec.builder(STRING, "VERSION", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .addJavadoc("The upstream iso-codes version, e.g. {@code 4.20.1}.\n")
                        .initializer("$S", version)
                        .build())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                .build());
    }

    private static CodeBlock arguments(JsonNode row, List<Field> fields) {
        List<CodeBlock> args = new ArrayList<>();
        for (Field field : fields) {
            JsonNode value = row.get(field.json());
            args.add(value == null || value.isNull() ? CodeBlock.of("null") : CodeBlock.of("$S", value.asText()));
        }
        return CodeBlock.join(args, ", ");
    }

    /**
     * {@code name} would clash with {@link Enum#name()}, and iso-codes names are English (translations ship
     * separately as gettext catalogs), so the field is exposed as {@code englishName}.
     */
    static String javaName(String json) {
        if (json.equals("name")) {
            return "englishName";
        }
        StringBuilder out = new StringBuilder();
        boolean upper = false;
        for (char c : json.toCharArray()) {
            if (c == '_' || c == '-') {
                upper = true;
            } else {
                out.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return out.toString();
    }

    static String constantName(String value) {
        String name = value.toUpperCase(java.util.Locale.ROOT).replaceAll("[^A-Z0-9]", "_");
        return Character.isDigit(name.charAt(0)) ? "_" + name : name;
    }

    private static String javadocText(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("*/", "*&#47;");
    }

    private JavaFile javaFile(String pkg, TypeSpec type) {
        return JavaFile.builder(pkg, type)
                .addFileComment("SPDX-License-Identifier: LGPL-2.1-or-later\n")
                .addFileComment("Generated from Debian iso-codes $L. Do not edit.", version)
                .skipJavaLangImports(true)
                .build();
    }

    private JsonNode parse(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void write(JavaFile file, Path outputDir) {
        try {
            file.writeTo(outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
