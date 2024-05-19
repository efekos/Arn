package dev.efekos.arn.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a literal node of a command. {@link dev.efekos.arn.Arn} parses {@link dev.efekos.arn.annotation.Command}'s
 * value into a list of CommandAnnotationLiterals.
 * @since 0.1
 * @author efekos
 */
public class CommandAnnotationLiteral {
    // instance

    /**
     * Actual literal that will be used in command structures.
     */
    private String literal;

    /**
     * Offset of the literal. Can't be a negative value. {@link dev.efekos.arn.Arn} will place this literal before the
     * argument that has the same index value with this offset.
     */
    private int offset;

    /**
     * Creates a new CommandAnnotationLiteral.
     * @param literal Actual literal that will be used in command structures.
     * @param offset  Offset of the literal. Can't be a negative value. {@link dev.efekos.arn.Arn} will place this
     *                literal before the argument that has the same index value with this offset.
     */
    public CommandAnnotationLiteral(String literal, int offset) {
        this.literal = literal;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "CommandAnnotationLiteral{" +
                "literal='" + literal + '\'' +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAnnotationLiteral that = (CommandAnnotationLiteral) o;
        return offset == that.offset && Objects.equals(literal, that.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal, offset);
    }

    /**
     * Getter for {@link #literal}
     * @return The literal.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Setter for {@link #literal}
     * @param literal New literal.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

    /**
     * Getter for {@link #offset}.
     * @return The offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Setter for {@link #offset}.
     * @param offset New offset.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    // static

    /**
     * Character that is used to separate multiple literals while parsing a {@link String}.
     */
    public static final char SEPARATOR_CHAR = '.';

    /**
     * {@link #SEPARATOR_CHAR} as a {@link String}.
     */
    public static final String SEPARATOR_CHAR_STRING = ".";

    /**
     * {@link Pattern} of a literal with offset, used while parsing a {@link String} into a literal.
     */
    public static final Pattern OFFSET_REGEX = Pattern.compile("^([ba]):(\\d*):([a-z]+)$");

    /**
     * Parses {@code input} into a {@link CommandAnnotationLiteral}. An offset will be included if {@code input}
     * matches {@link #OFFSET_REGEX}. A literal must be made of lowercase alphabetical characters.
     * @param input Input {@link String}.
     * @return Parsed {@link CommandAnnotationLiteral}.
     */
    public static CommandAnnotationLiteral parse(String input){
        Matcher matcher = OFFSET_REGEX.matcher(input);
        if(!matcher.matches()) return new CommandAnnotationLiteral(input, 0);

        boolean isAfter = matcher.group(1).equals("a");
        int offset = Integer.parseInt(matcher.group(2));
        String actualLiteral = matcher.group(3);

        return new CommandAnnotationLiteral(actualLiteral,isAfter ? offset + 1 : offset);
    }
}
