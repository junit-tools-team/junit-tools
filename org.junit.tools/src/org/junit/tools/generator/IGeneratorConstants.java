package org.junit.tools.generator;

/**
 * General constants for the generators.
 * 
 * @author JUnit-Tools-Team
 */
public interface IGeneratorConstants {

    public static final String TML_VERSION_ACTUAL = "1.0.0";

    public static final String MOD_PUBLIC = "public";
    public static final String MOD_PROTECTED = "protected";
    public static final String MOD_PRIVATE = "private";
    public static final String MOD_PACKAGE = "";
    public static final String MOD_STATIC_WITH_BLANK = " static";

    public static final String TYPE_VOID = "void";
    public static final String TYPE_STRING = "String";
    public static final String TYPE_CHAR = "char";
    public static final String TYPE_BYTE = "byte";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_INT = "int";
    public static final String TYPE_INTEGER = "Integer";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_FLOAT = "float";

    public static final String TYPE_ARRAY = "[]";

    public static final String RETURN = "\n";

    public final static String QUOTES = "\"";

    public static final String VERSION = "org.junit-tools-1.0.2";

    public static final String ANNO_GENERATED_NAME = "Generated";
    public static final String ANNO_GENERATED = "@" + ANNO_GENERATED_NAME;

    public static final String ANNO_METHOD_REF_NAME = "MethodRef";
    public static final String ANNO_METHOD_REF = "@";

    public static final String INCREMENT_SEPERATOR = "_";
}
