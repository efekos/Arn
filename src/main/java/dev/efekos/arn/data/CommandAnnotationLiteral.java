package dev.efekos.arn.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandAnnotationLiteral {
    // instance
    private String literal;
    private int offset;

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

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    // static
    public static final char SEPARATOR_CHAR = '.';
    public static final String SEPARATOR_CHAR_STRING = ".";
    public static final Pattern OFFSET_REGEX = Pattern.compile("^([ba]):(\\d*):([a-z]+)$");

    public static CommandAnnotationLiteral parse(String input){
        Matcher matcher = OFFSET_REGEX.matcher(input);
        if(!matcher.matches()) return new CommandAnnotationLiteral(input, 0);

        boolean isAfter = matcher.group(1).equals("a");
        int offset = Integer.parseInt(matcher.group(2));
        String actualLiteral = matcher.group(3);

        return new CommandAnnotationLiteral(actualLiteral,isAfter ? offset + 1 : offset);
    }
}
