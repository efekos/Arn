package dev.efekos.arn.data;

import dev.efekos.arn.annotation.Command;

import java.util.List;
import java.util.Objects;

public class CommandAnnotationData {
    private String description;
    private String permission;
    private List<CommandAnnotationLiteral> literals;

    public CommandAnnotationData(Command ann) {
        setDescription(ann.description());
        setPermission(ann.permission());
    }

    @Override
    public String toString() {
        return "CommandAnnotationData{" +
                "description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                ", literals=" + literals +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAnnotationData that = (CommandAnnotationData) o;
        return Objects.equals(description, that.description) && Objects.equals(permission, that.permission) && Objects.equals(literals, that.literals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, permission, literals);
    }

    public List<CommandAnnotationLiteral> getLiterals() {
        return literals;
    }

    public void setLiterals(List<CommandAnnotationLiteral> literals) {
        this.literals = literals;
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
