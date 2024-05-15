package dev.efekos.arn.annotation;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class CommandAnnotationData {
    private String name;
    private List<String> aliases;
    private String description;
    private String permission;

    public CommandAnnotationData(Command ann) {
        setName(ann.value());
        setDescription(ann.description());
        setPermission(ann.permission());
        setAliases(ann.aliases()!=null? Arrays.asList(ann.aliases())  :new ArrayList<>());
    }
}
