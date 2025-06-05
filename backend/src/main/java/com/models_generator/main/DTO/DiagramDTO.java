package com.models_generator.main.DTO;

import lombok.Data;
import java.util.List;

@Data
public class DiagramDTO {
    private Long id;
    private String title;
    private List<ClassElement> classes;
    private List<RelationshipElement> relationships;

    @Data
    public static class ClassElement {
        private String id;
        private String name;
        private List<AttributeElement> attributes;
        private List<MethodElement> methods;
        private Position position;
    }

    @Data
    public static class AttributeElement {
        private String name;
        private String type;
        private String visibility;
    }

    @Data
    public static class MethodElement {
        private String name;
        private String returnType;
        private List<String> parameters;
    }

    @Data
    public static class RelationshipElement {
        private String id;
        private String source;
        private String target;
        private String type;
    }

    @Data
    public static class Position {
        private int x;
        private int y;
    }
}