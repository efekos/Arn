package dev.efekos.arn.data;

import dev.efekos.arn.annotation.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper class for {@link dev.efekos.arn.annotation.Command}, used to change data and parse literals into {@link CommandAnnotationLiteral}s.
 * @since 0.1
 * @author efekos
 */
public class CommandAnnotationData {

    /**
     * Description of the command associated with this annotation data. If there isn't a description present by the
     * original {@link Command} annotation, {@link dev.efekos.arn.Arn} defaults it to {@code "No description provided".}.
     */
    private String description;

    /**
     * Permission required to run the command associated with this annotation data. If there isn't a permission present
     * by the original {@link Command} annotation, no permission will be required to run the command.
     */
    private String permission;

    /**
     * A list of the literals of the command associated with this annotation data.
     */
    private List<CommandAnnotationLiteral> literals = new ArrayList<>();

    /**
     * Creates a new annotation data using the {@link Command} annotation given.
     * @param ann Any {@link Command} annotation to get description and permission from.
     */
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

    /**
     * Getter for {@link #literals}.
     * @return The literal list.
     */
    public List<CommandAnnotationLiteral> getLiterals() {
        return literals;
    }

    /**
     * Setter for {@link #literals}
     * @param literals New literal list.
     */
    public void setLiterals(List<CommandAnnotationLiteral> literals) {
        this.literals = literals;
    }

    /**
     * Getter for {@link #description}
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for {@link #description}
     * @param description New description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for {@link #permission}
     * @return The permission.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Setter for {@link #permission}.
     * @param permission New permission.
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }
}
