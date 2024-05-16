package dev.efekos.arn.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "CommandAnnotationData{" +
                "name='" + name + '\'' +
                ", aliases=" + aliases +
                ", description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAnnotationData that = (CommandAnnotationData) o;
        return Objects.equals(name, that.name) && Objects.equals(aliases, that.aliases) && Objects.equals(description, that.description) && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, aliases, description, permission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
