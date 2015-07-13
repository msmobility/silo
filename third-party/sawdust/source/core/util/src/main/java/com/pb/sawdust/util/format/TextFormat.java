package com.pb.sawdust.util.format;

import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;

import java.util.*;
import java.math.BigInteger;
import java.math.BigDecimal;


/**
 * The {@code TextFormat} class provides a structured class representation of the string formatting syntax used by the
 * {@code java.util.Formatter} class. The intention of the class is to provide greater transparency to formats, to
 * provide a safe structure through which formats can be reused, and to move formatting (syntax) errors from formatting
 * calls to format definitions.
 * <p>
 * Text formats provide a standard through which various inputs (objects or primitives) are printed to text. Each format
 * is defined by a {@link com.pb.sawdust.util.format.TextFormat.Conversion}, which defines, generally, how a given
 * input is converted to text. Formats can contain a (positive integer) <b>width</b>, which specifies the minimum number of
 * characters the text output must contain (in cases where the output is too small, the text will be padded with
 * spaces). Some formats may contain a <b>precision</b>, which indicates either a numerical precision or a maximum number
 * of characters in the text output. In addition, a given format can contain {@link com.pb.sawdust.util.format.TextFormat.FormatFlag}s,
 * which offer modifications to how the output is formatted. Also, the {@link Conversion#DATE_TIME} and
 * {@link Conversion#DATE_TIME_UPPER_CASE} take an additional {@link DateTimeConversion} to further specify the date/time
 * output formatting. For more information on the specifics of formatting, the user should reference the {@code Formatter}
 * documentation, cross-referencing with the {@code enum}s contained in this class (all text format combinations/possibilities
 * can be created using these constructs).
 * <p>
 * To use the formats contained in this class, the {@code toString()} and {@code getFormat*} methods to get format strings
 * which can be used directly in format calls. For example, for formats {@code f1} and {@code f2}, a formatted text output
 * can be created through the following call:
 * <pre><code>
 *     System.out.printf("Object 2: " + f2.getFormat(2) + ", object1: " + f1.getFormat(1) ,object1,object2);
 * </code></pre>
 *
 * @author crf <br/>
 *         Started: Jul 25, 2008 8:08:57 AM
 */
public class TextFormat {
    private final Conversion conversion;
    private final DateTimeConversion dateTimeConversion;
    private final int minimumWidth;
    private final int precision;
    private final EnumSet<FormatFlag> formatFlags;
    private final String formatString;

    /**
     * The {@code TextFormat} used for a new line character.
     */
    public static final TextFormat NEW_LINE_FORMAT = new TextFormat(Conversion.NEW_LINE);

    /**
     * The {@code TextFormat} used for a percent character.
     */
    public static final TextFormat PERCENT_FORMAT = new TextFormat(Conversion.PERCENT);

    private TextFormat(Conversion conversion, DateTimeConversion dateTimeConversion, int minimumWidth, int precision, EnumSet<FormatFlag> flags) {
        this.conversion = conversion;
        this.dateTimeConversion = dateTimeConversion;
        this.minimumWidth = minimumWidth;
        this.precision = precision;
        formatFlags = flags;
        formatString = getFormatString();
    }

    /**
     * Format constructor specifying conversion, width, precision, and flags.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param minimumWidth
     *        The minimum width for this text format.
     *
     * @param precision
     *        The precision for this text format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code width} or {@code precision} is less than one; if {@code conversion}
     *                                  is {@code DATE_TIME} or {@code DATE_TIME_UPPER_CASE} (use constructors taking
     *                                  an explicit {@code DateTimeConversion}); or if {@code flags} and/or
     *                                  {@code precision} cannot be used with the specified {@code conversion}.
     */
    public TextFormat(Conversion conversion, int minimumWidth, int precision, FormatFlag ... flags) {
        this(checkConversionValidity(conversion),null,checkMinimumWidthValidity(minimumWidth),checkPrecisionValidity(precision,conversion),checkFormatFlagsValidity(flags,conversion));
    }

     /**
     * Format constructor specifying conversion, width, and flags.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param minimumWidth
     *        The minimum width for this text format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code width} is less than one; if {@code conversion} is {@code DATE_TIME} or
      *                                 {@code DATE_TIME_UPPER_CASE} (use constructors taking an explicit {@code DateTimeConversion});
     *                                  or if {@code flags} cannot be used with the specified {@code conversion}.
     */
    public TextFormat(Conversion conversion, int minimumWidth, FormatFlag ... flags) {
        this(checkConversionValidity(conversion),null,checkMinimumWidthValidity(minimumWidth),-1,checkFormatFlagsValidity(flags,conversion));
    }

     /**
     * Format constructor specifying conversion and flags.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code conversion} is {@code DATE_TIME} or {@code DATE_TIME_UPPER_CASE} (use
      *                                 constructors taking an explicit {@code DateTimeConversion}); or if {@code flags}
      *                                 cannot be used with the specified {@code conversion}.
     */
    public TextFormat(Conversion conversion, FormatFlag ... flags) {
        this(checkConversionValidity(conversion),null,-1,-1,checkFormatFlagsValidity(flags,conversion));
    }

     /**
     * Format constructor specifying the conversion.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @throws IllegalArgumentException if {@code conversion} is {@code DATE_TIME} or {@code DATE_TIME_UPPER_CASE} (use
      *                                 constructors taking an explicit {@code DateTimeConversion});.
     */
    public TextFormat(Conversion conversion) {
        this(checkConversionValidity(conversion),null,-1,-1, EnumSet.noneOf(FormatFlag.class));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion, width, and flags.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     *
     * @param minimumWidth
     *        The minimum width for this text format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code width} is less than one; if {@code conversion} is not {@code DATE_TIME}
     *                                  or {@code DATE_TIME_UPPER_CASE}; or if {@code flags} cannot be used with the
     *                                  specified {@code conversion}.
     */
    public TextFormat(Conversion conversion, DateTimeConversion dateTimeConversion, int minimumWidth, FormatFlag ... flags) {
        this(checkDatTimeConversionValidity(conversion),dateTimeConversion,checkMinimumWidthValidity(minimumWidth),-1,checkFormatFlagsValidity(flags,conversion));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion and flags.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code conversion} is not {@code DATE_TIME} or {@code DATE_TIME_UPPER_CASE};
     *                                  or if {@code flags} cannot be used with the specified {@code conversion}.
     */
    public TextFormat(Conversion conversion, DateTimeConversion dateTimeConversion, FormatFlag ... flags) {
        this(checkDatTimeConversionValidity(conversion),dateTimeConversion,-1,-1,checkFormatFlagsValidity(flags,conversion));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion.
     *
     * @param conversion
     *        The conversion for this text format.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     *
     * @throws IllegalArgumentException if {@code conversion} is not {@code DATE_TIME} or {@code DATE_TIME_UPPER_CASE}.
     */
    public TextFormat(Conversion conversion, DateTimeConversion dateTimeConversion) {
        this(checkDatTimeConversionValidity(conversion),dateTimeConversion,-1,-1, EnumSet.noneOf(FormatFlag.class));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion, width, and flags.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     *
     * @param minimumWidth
     *        The minimum width for this text format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code width} is less than one, or if {@code flags} cannot be used with the
     *                                  specified {@code conversion}.
     */
    public TextFormat(DateTimeConversion dateTimeConversion, int minimumWidth, FormatFlag ... flags) {
        this(Conversion.DATE_TIME,dateTimeConversion,checkMinimumWidthValidity(minimumWidth),-1,checkFormatFlagsValidity(flags, Conversion.DATE_TIME));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion and flags.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     *
     * @param flags
     *        The flags for this text format.
     *
     * @throws IllegalArgumentException if {@code flags} cannot be used with the specified {@code conversion}.
     */
    public TextFormat(DateTimeConversion dateTimeConversion, FormatFlag ... flags) {
        this(Conversion.DATE_TIME,dateTimeConversion,-1,-1,checkFormatFlagsValidity(flags, Conversion.DATE_TIME));
    }

    /**
     * Constructor for date/time formats specifying date/time conversion.
     *
     * @param dateTimeConversion
     *        The date/time conversion for this format.
     */
    public TextFormat(DateTimeConversion dateTimeConversion) {
        this(Conversion.DATE_TIME,dateTimeConversion,-1,-1, EnumSet.noneOf(FormatFlag.class));
    }

    /**
     * Get the conversion type for this text format.
     *
     * @return this text format's conversion.
     */
    public Conversion getConversion() {
        return conversion;
    }

    /**
     * Get the date/time conversion type for this text format. If this format's conversion is not date/time, then this
     * method returns {@code null}.
     *
     * @return this text format's date/time conversion.
     */
    public DateTimeConversion getDateTimeConversion() {
        return dateTimeConversion;
    }

    /**
     * Get the minimum width for this text format. If no width is set, then zero will be returned.
     *
     * @return this text format's minimum widt.
     */
    public int getMinimumWidth() {
        return minimumWidth;
    }

    /**
     * Get the precision for this text format. If no precision is set, then zero will be returned.
     *
     * @return this text format's precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Get the format flags for this text format.
     *
     * @return this text format's flags.
     */
    public Set<FormatFlag> getFlags() {
        return Collections.unmodifiableSet(formatFlags);
    }

    private String getFormatString() {
        StringBuilder sb = new StringBuilder();
        for (FormatFlag flag : formatFlags)
            sb.append(flag.getFlagCharacter());
        if (minimumWidth > 0)
            sb.append(minimumWidth);
        if (precision > -1)
            sb.append('.').append(precision);
        if (conversion.getFormatClass() == TextFormatClass.DATE_TIME)
            sb.append(conversion.getFormatConversionCharacter()).append(dateTimeConversion.getDateTimeConversionCharacter());
        else
            sb.append(conversion.getFormatConversionCharacter());
        return sb.toString();
    }

    public String toString() {
        return getFormat();
    }

    /**
     * Get the string representation of this format which can be used in formatting calls.
     *
     * @return this format's format string representation
     */
    public String getFormat() {
        return "%" + formatString;
    }

    /**
     * Get the string representation of this format which can be used in formatting calls with numbered arguments.
     *
     * @param argument
     *        The argument number for this format string.
     *
     * @return this format's numbered format string representation
     */
    public String getFormat(int argument) {
        if (argument < 1)
            throw new IllegalArgumentException("Argument index must be greater than 0.");
        return "%" + argument + "$" + formatString;
    }

    /**
     * Get the string representation of this format which can be used in relative argument formatting calls.
     *
     * @return this format's relative argument format string representation
     */
    public String getFormatRelative() {
        return "%<" + formatString;
    }

    /**
     * Convenience method to format a given input into a string using this text format.
     *
     * @param argument
     *        The data to format.
     *
     * @return {@code argument} formatted using this format.
     *
     * @throws IllegalArgumentException if <code>isValidArgument(argument)</code> returns {@code false}.
     */
    public String format(Object argument) {
        if (isValidArgument(argument))
           return String.format(getFormat(),argument);
        else
            throw new IllegalArgumentException("Argument is invalid for use with text format " + formatString + ": " + argument);
    }

    /**
     * Get a text format that replicates a given text format except it changes (or adds) a minimum width.
     *
     * @param format
     *        The input format to replicate.
     *
     * @param minimumWidth
     *        The minimimum width for the output format.
     *
     * @return a text format which replicates {@code format} and has width {@code minimumWidth}.
     *
     * @throws IllegalArgumentException if {@code minimumWidth} is less than one.
     */
    public static TextFormat getMinimumWidthFormat(TextFormat format, int minimumWidth) {
        return new TextFormat(format.conversion,format.dateTimeConversion,checkMinimumWidthValidity(minimumWidth),format.precision,EnumSet.copyOf(format.formatFlags));
    }

    /**
     * Get a text format that replicates a given text format except it changes (or adds) a precision.
     *
     * @param format
     *        The input format to replicate.
     *
     * @param precision
     *        The precision for the output format.
     *
     * @return a text format which replicates {@code format} and has precision {@code precision}.
     *
     * @throws IllegalArgumentException if {@code precisiuon} is less than one, or if {@code format} does not allow
     *                                  a precision.
     */
    public static TextFormat getPrecisionFormat(TextFormat format, int precision) {
        return new TextFormat(format.conversion,format.dateTimeConversion,format.minimumWidth,checkPrecisionValidity(precision,format.conversion),EnumSet.copyOf(format.formatFlags));
    }

    /**
     * Get a text format with a specified precision but no specified minimum width.
     *
     * @param conversion
     *        The conversion for the text format.
     *
     * @param precision
     *        The precision for the text format.
     *
     * @param flags
     *        The flags for the text format.
     *
     * @return the text format with specified conversion, precision, and flags.
     *
     * @throws IllegalArgumentException if @code precision} is less than one, or if {@code flags} and/or
     *                                  {@code precision} cannot be used with the specified {@code conversion}.
     */
    public static TextFormat getPrecisionFormat(Conversion conversion, int precision, FormatFlag ... flags) {
        return new TextFormat(checkConversionValidity(conversion),null,-1,checkPrecisionValidity(precision,conversion),checkFormatFlagsValidity(flags,conversion));
    }

    /**
     * Get a text format that replicates a given text format except it adds format flags.
     *
     * @param format
     *        The input format to replicate.
     *
     * @param flags
     *        The flags for the output format.
     *
     * @return a text format which replicates {@code format} and has width {@code minimumWidth}.
     *
     * @throws IllegalArgumentException if any of the {@code flags} is not allowed for {@code format}.
     */
    public static TextFormat getFlaggedFormat(TextFormat format, FormatFlag ... flags) {
        EnumSet<FormatFlag> formatFlags = checkFormatFlagsValidity(flags,format.conversion);
        formatFlags.addAll(format.formatFlags);
        return new TextFormat(format.conversion,format.dateTimeConversion,format.minimumWidth,format.precision,formatFlags);
    }

    /**
     * Get a text format that replicates a given text format except it removes the minimum width (if it exists).
     *
     * @param format
     *        The input format to replicate.
     *
     * @return a text format which replicates {@code format} with no minimum width.
     */
    public static TextFormat noMinimumWidthFormat(TextFormat format) {
        return new TextFormat(format.conversion,format.dateTimeConversion,-1,format.precision,format.formatFlags);
    }

    /**
     * Get a text format that replicates a given text format except it removes the precision (if it exists).
     *
     * @param format
     *        The input format to replicate.
     *
     * @return a text format which replicates {@code format} with no precision.
     */
    public static TextFormat noPrecisionFormat(TextFormat format) {
        return new TextFormat(format.conversion,format.dateTimeConversion,format.minimumWidth,-1,format.formatFlags);
    }

    /**
     * Get a text format that replicates a given text format except it removes the format flags (if they exists).
     *
     * @param format
     *        The input format to replicate.
     *
     * @return a text format which replicates {@code format} with no format flags.
     */
    public static TextFormat noFlagFormat(TextFormat format) {
        return new TextFormat(format.conversion,format.dateTimeConversion,format.minimumWidth,format.precision,EnumSet.noneOf(FormatFlag.class));
    }

    private static Conversion checkConversionValidity(Conversion conversion) {
        if (conversion.getFormatClass() == TextFormatClass.DATE_TIME)
            throw new IllegalArgumentException("Date-time conversion cannot be used directly, use TextFormat.DateTimeConversion instead.") ;
        return conversion;
    }

    private static Conversion checkDatTimeConversionValidity(Conversion conversion) {
        if (!(conversion.getFormatClass() == TextFormatClass.DATE_TIME))
            throw new IllegalArgumentException("Date-time conversion must use a date time conversion: " + conversion) ;
        return conversion;
    }

    private static int checkMinimumWidthValidity(int minimumWidth) {
        if (minimumWidth < 1)
            throw new IllegalArgumentException("Minimum width must be greater than 0: " + minimumWidth);
        return minimumWidth;
    }

    private static int checkPrecisionValidity(int precision, Conversion conversion) {
        if (!conversion.getFormatClass().allowsPrecision)
            throw new IllegalArgumentException("Text format conversion does not allow precision: " + conversion);
        if (precision < 0)
            throw new IllegalArgumentException("Precision must be greater than -1: " + precision);
        return precision;
    }

    private static EnumSet<FormatFlag> checkFormatFlagsValidity(FormatFlag[] flags, Conversion conversion) {
        TextFormatClass formatClass = conversion.getFormatClass();
        EnumSet<FormatFlag> formatFlags = EnumSet.noneOf(FormatFlag.class);
        for (FormatFlag flag : flags)
            if (!formatClass.formatFlags.contains(flag))
                throw new IllegalArgumentException("Format flag not allowed for conversion type " + conversion + ": " + flag);
            else
                formatFlags.add(flag);
        return formatFlags;
    }

    /**
     * Determine whether a given input is a valid argument for this format. <i>i.e.</i>, whether or not a call
     * to a formatting method using {@code argument} (<i>e.g.</i> <code>System.out.printf(format,argument)</code>)
     * will throw an exception due to an invalid argument or not.
     *
     * @param argument
     *        The argument whose validity is being checked.
     *
     * @return {@code true} if the argument can be used with this format, {@code false} otherwise.
     */
    public boolean isValidArgument(Object argument) {
        return conversion.isValidArgument(argument);
    }

    /**
     * Used as a way of stratifying text formats using the structure defined in {@code Formatter}.
     */
    private static enum TextFormatClass {
        GENERAL(true,new FormatFlag[] {
                    FormatFlag.LEFT_JUSTIFIED,
                    FormatFlag.CONVERSION_DEPENDANT},
                Object.class),
        CHARACTER(new FormatFlag[] {FormatFlag.LEFT_JUSTIFIED},
                Character.class,
                Byte.class,
                Short.class),
        INTEGRAL(new FormatFlag[] {
                    FormatFlag.LEFT_JUSTIFIED,
                    FormatFlag.CONVERSION_DEPENDANT,
                    FormatFlag.INCLUDE_SIGN,
                    FormatFlag.POSITIVE_EXTRA_SPACE,
                    FormatFlag.ZERO_PADDED,
                    FormatFlag.LOCAL_SPECIFIC_GROUPING,
                    FormatFlag.NEGATIVE_PARENTHESES},
                Byte.class,
                Short.class,
                Integer.class,
                Long.class,
                BigInteger.class),
        FLOATING_POINT(true,new FormatFlag[] {
                    FormatFlag.LEFT_JUSTIFIED,
                    FormatFlag.CONVERSION_DEPENDANT,
                    FormatFlag.INCLUDE_SIGN,
                    FormatFlag.POSITIVE_EXTRA_SPACE,
                    FormatFlag.ZERO_PADDED,
                    FormatFlag.LOCAL_SPECIFIC_GROUPING,
                    FormatFlag.NEGATIVE_PARENTHESES},
                Float.class,
                Double.class,
                BigDecimal.class),
        DATE_TIME(new FormatFlag[] {FormatFlag.LEFT_JUSTIFIED},
                Long.class,
                Calendar.class,
                Date.class),
        PERCENT(new FormatFlag[] {FormatFlag.LEFT_JUSTIFIED},
                Void.class),
        NEW_LINE(new FormatFlag[] {},
                Void.class);

        private final boolean allowsPrecision;
        private final EnumSet<FormatFlag> formatFlags;
        private final Set<Class<?>> validClasses;

        private TextFormatClass( boolean allowsPrecision, FormatFlag[] formatFlags, Class<?> ... validClasses) {
            this.allowsPrecision = allowsPrecision;
            this.formatFlags = EnumSet.noneOf(FormatFlag.class);
            this.formatFlags.addAll(Arrays.asList(formatFlags));
            this.validClasses = Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(validClasses)));
        }

        private TextFormatClass(FormatFlag[] formatFlags, Class ... validClasses) {
            this(false,formatFlags,validClasses);
        }
    }

    /**
     * The {@code Conversion} enum defines all of the possible text format conversions available to {@code TextFormat}
     * instances.
     */
    public static enum Conversion {
        /**
         * If the argument is {@code null}, then the result is "false". If arg is a {@code boolean} or {@code Boolean},
         * then the result is the string returned by {@code String.valueOf(argument)}. Otherwise, the result is "true".
         */
        BOOLEAN(TextFormatClass.GENERAL,'b'),
        /**
         * If the argument is {@code null}, then the result is "FALSE". If arg is a {@code boolean} or {@code Boolean},
         * then the result is the string returned by {@code String.valueOf(argument).toUpperCase()}. Otherwise, the result is "TRUE".
         */
        BOOLEAN_UPPER_CASE(TextFormatClass.GENERAL,'B'),
        /**
         * If the argument is {@code null}, then the result is "NULL". Otherwise, the result is obtained by invoking
         * <code>Integer.toHexString(argument.hashCode())</code>.
         */
        HASH_CODE(TextFormatClass.GENERAL,'H'),
        /**
         * If the argument is {@code null}, then the result is "null". Otherwise, the result is obtained by invoking
         * <code>Integer.toHexString(argument.hashCode()).toiUpperCase()</code>.
         */
        HASH_CODE_UPPER_CASE(TextFormatClass.GENERAL,'h'),
        /**
         * If the argument is {@code null}, then the result is "NULL".  If argument implements {@code Formattable}, then
         * <code>arg.formatTo</code> is invoked. Otherwise, the result is obtained by invoking <code>arg.toString()</code>.
         */
        STRING(TextFormatClass.GENERAL,'s'),
        /**
         * If the argument is {@code null}, then the result is "NULL".  If argument implements {@code Formattable}, then
         * <code>arg.formatTo</code> is invoked. Otherwise, the result is obtained by invoking <code>arg.toString().toUpperCase()</code>.
         */
        STRING_UPPER_CASE(TextFormatClass.GENERAL,'S'),
        /**
         * The argument is formatted as a Unicode character.
         */
        CHARACTER(TextFormatClass.CHARACTER,'c'),
        /**
         * The argument is formatted as an upper-case Unicode character.
         */
        CHARACTER_UPPER_CASE(TextFormatClass.CHARACTER,'C'),
        /**
         * The argument is formatted as a decimal integer.
         */
        INTEGER(TextFormatClass.INTEGRAL,'d'),
        /**
         * The argument is formatted as an octal integer.
         */
        INTEGER_OCTAL(TextFormatClass.INTEGRAL,'o'),
        /**
         * The argument is formatted as a hexadecimal integer.
         */
        INTEGER_HEXADECIMAL(TextFormatClass.INTEGRAL,'x'),
        /**
         * The argument is formatted as an upper-case hexadecimal integer.
         */
        INTEGER_HEXADECIMAL_UPPER_CASE(TextFormatClass.INTEGRAL,'X'),
        /**
         * The argument is formatted as a decimal number in computerized scientific notation.
         */
        SCIENTIFIC(TextFormatClass.FLOATING_POINT,'e'),
        /**
         * The argument is formatted as a decimal number in computerized scientific notation using an upper-case "E".
         */
        SCIENTIFIC_UPPER_CASE(TextFormatClass.FLOATING_POINT,'E'),
        /**
         * The argument is formatted as a decimal number.
         */
        FLOATING_POINT(TextFormatClass.FLOATING_POINT,'f'),
        /**
         * The argument is formatted using computerized scientific notation or decimal format, depending on the precision
         * and the value after rounding.
         */
        FLOATING_POINT_OR_SCIENTIFIC(TextFormatClass.FLOATING_POINT,'g'),
        /**
         * The argument is formatted using computerized scientific notation (using an upper-case "E") or decimal format,
         * depending on the precision and the value after rounding.
         */
        FLOATING_POINT_OR_SCIENTIFIC_UPPER_CASE(TextFormatClass.FLOATING_POINT,'G'),
        /**
         * The argument is formatted as a hexadecimal floating-point number with a significand and an exponent.
         */
        FLOATING_POINT_HEXADECIMAL(TextFormatClass.FLOATING_POINT,'a'),
        /**
         * The argument is formatted as an upper-case hexadecimal floating-point number with a significand and an exponent.
         */
        FLOATING_POINT_HEXADECIMAL_UPPER_CASE(TextFormatClass.FLOATING_POINT,'A'),
        /**
         * The argument is formatted as a date and/or time, depending on the specified {@code DateTimeConversion}.
         */
        DATE_TIME(TextFormatClass.DATE_TIME,'t'),
        /**
         * The argument is formatted as an upper-case  date and/or time, depending on the specified {@code DateTimeConversion}.
         */
        DATE_TIME_UPPER_CASE(TextFormatClass.DATE_TIME,'T'),
        /**
         * The result is a literal "%" (<tt>\\u0025</tt>).
         */
        PERCENT(TextFormatClass.PERCENT,'%'),
        /**
         * The result is the platform-specific line separator.
         */
        NEW_LINE(TextFormatClass.NEW_LINE,'n'),
        ;

        private final TextFormatClass formatClass;
        private final char formatConversionCharacter;

        private Conversion(TextFormatClass formatClass,char formatConversionCharacter) {
            this.formatClass = formatClass;
            this.formatConversionCharacter = formatConversionCharacter;
        }

        private TextFormatClass getFormatClass() {
            return formatClass;
        }

        private char getFormatConversionCharacter() {
            return formatConversionCharacter;
        }

        /**
         * Get the format flags which are valid for this conversion.
         *
         * @return this conversion's valid format flag.
         */
        public Set<FormatFlag> getValidFlags() {
            return Collections.unmodifiableSet(formatClass.formatFlags);
        }

        /**
         * Determine whether this conversion allows precision or not.
         *
         * @return {@code true} if this conversion allows a precision, {@code false} otherwise.
         */
        public boolean allowsPrecision() {
            return formatClass.allowsPrecision;
        }

        /**
         * Get the set of classes whose instances can be used as arguments in formatting calls for this conversion.
         *
         * @return the set of classes which can be used as arguments with this conversion.
         */
        public Set<Class<?>> getValidArgumentClasses() {
            return formatClass.validClasses;
        }

        /**
         * Determine whether a given input is a valid argument for this conversion. <i>i.e.</i>, whether or not a call
         * to a formatting method using {@code argument} (<i>e.g.</i> <code>System.out.printf(format,argument)</code>)
         * will throw an exception due to an invalid argument or not.
         *
         * @param argument
         *        The argument whose validity is being checked.
         *
         * @return {@code true} if the argument can be used with this conversion, {@code false} otherwise.
         */
        public boolean isValidArgument(Object argument) {
            switch (this.formatClass) {
                case GENERAL : return true;
                case CHARACTER :
                    //if integer, has to be valid character code
                    if (argument instanceof Integer)
                        return (Character.isValidCodePoint((Integer) argument));
            }
            for (Class<?> validClass : this.formatClass.validClasses)
                if (validClass.isInstance(argument))
                    return true;
            return false;
        }

        private static InjectiveMap<Character, Conversion> conversionMap;

        static {
            conversionMap = new InjectiveHashMap<Character, Conversion>();
            for (Conversion value : values())
                conversionMap.put(value.formatConversionCharacter,value);
        }

        /**
         * Get the conversion corresponding to a given character. If no conversion corresponds to the character,
         * {@code null} is returned.
         *
         * @param conversionCharacter
         *        The character in question.
         *
         * @return the conversion corresponding to {@code conversionCharacter}.
         */
        public static Conversion getTextFormatConversion(char conversionCharacter) {
            return conversionMap.get(conversionCharacter);
        }
    }

    /**
     * The {@code DateTimeConversion} enum defines all of the date time conversions available to {@code TextFormat} instances
     * with the conversion {@code TextFormat.Conversion.DATE_TIME} or {@code TextFormat.Conversion.DATE_TIME_UPPER_CASE}.
     */
    public static enum DateTimeConversion {
        /**
         * Hour of the day for the 24-hour clock, formatted as two digits with a leading zero as necessary <i>i.e.</i> "00" - "23".
         */
        HOUR_24_ZERO_PADDED('H'),
        /**
         * Hour for the 12-hour clock, formatted as two digits with a leading zero as necessary, <i>i.e.</i> "01" - "12".
         */
        HOUR_12_ZERO_PADDED('I'),
        /**
         * Hour of the day for the 24-hour clock, <i>i.e.</i> "0" - "23".
         */
        HOUR_24('k'),
        /**
         * Hour of the day for the 12-hour clock, <i>i.e.</i> "0" - "12".
         */
        HOUR_12('l'),
        /**
         * Minute within the hour formatted as two digits with a leading zero as necessary, <i>i.e.</i> "00" - "59".
         */
        MINUTE_ZERO_PADDED('M'),
        /**
         * Seconds within the minute, formatted as two digits with a leading zero as necessary, <i>i.e.</i> "00" - "60"
         * ("60" is a special value required to support leap seconds)."
         */
        SECOND_ZERO_PADDED('S'),
        /**
         * Millisecond within the second formatted as three digits with leading zeros as necessary, <i>i.e.</i> "000" - "999".
         */
        MILLISECOND_ZERO_PADDED('L'),
        /**
         * Nanosecond within the second, formatted as nine digits with leading zeros as necessary, <i>i.e.</i> "000000000" - "999999999".
         */
        NANOSECOND_ZERO_PADDED('N'),
        /**
         * Locale-specific morning or afternoon marker in lower case, <i>e.g.</i> "am" or "pm".
         */
        AM_PM('p'),
        /**
         * RFC 822 style numeric time zone offset from GMT, <i>e.g.</i> "-0800".
         */
        TIME_ZONE_NUMERIC('z'),
        /**
         * A string representing the abbreviation for the time zone. The Formatter's locale will supersede the locale of
         * the argument (if any).
         */
        TIME_ZONE('Z'),
        /**
         * Seconds since the beginning of the epoch starting at 1 January 1970 00:00:00 UTC, <i>i.e.</i> <code>Long.MIN_VALUE/1000</code>
         * to <code>Long.MAX_VALUE/1000</code>.
         */
        SECONDS_EPOCH('s'),
        /**
         * Milliseconds since the beginning of the epoch starting at 1 January 1970 00:00:00 UTC, <i>i.e.</i> <code>Long.MIN_VALUE</code>
         * to <code>Long.MAX_VALUE</code>.
         */
        MILLISECONDS_EPOCH('Q'),
        /**
         * Locale-specific full month name, <i>e.g.</i> "January", "February".
         */
        MONTH('B'),
        /**
         * Locale-specific abbreviated month name, <i>e.g.</i> "Jan", "Feb".
         */
        MONTH_ABBREVIATE('b'),
        /**
         * Locale-specific full name of the day of the week, <i>e.g.</i> "Sunday", "Monday".
         */
        DAY_OF_WEEK('A'),
        /**
         * Locale-specific short name of the day of the week, <i>e.g.</i> "Sun", "Mon".
         */
        DAY_OF_WEEK_ABBREVIATED('a'),
        /**
         * Four-digit year divided by 100, formatted as two digits with leading zero as necessary, <i>i.e.</i> "00" - "99".
         */
        YEAR_HUNDREDS('C'),
        /**
         * Year, formatted as at least four digits with leading zeros as necessary, <i>e.g.</i> "0092" equals 92 CE for
         * the Gregorian calendar.
         */
        YEAR('Y'),
        /**
         * Last two digits of the year, formatted with leading zeros as necessary, <i>i.e.</i> "00" - "99".
         */
        YEAR_TENS('y'),
        /**
         * Day of year, formatted as three digits with leading zeros as necessary, <i>e.g.</i> "001" - "366" for the
         * Gregorian calendar.
         */
        DAY_366('j'),
        /**
         * Month, formatted as two digits with leading zeros as necessary, <i>i.e.</i> "01" - "13".
         */
        MONTH_12('m'),
        /**
         * Day of month, formatted as two digits with leading zeros as necessary, <i>i.e.</i> "01" - "31".
         */
        DAY_ZERO_PADDED('d'),
        /**
         * Day of month, formatted as two digits, <i>i.e.</i> "1" - "31".
         */
        DAY('e'),
        /**
         * Time formatted for the 24-hour clock as "hour:minute" using {@code HOUR_24_ZERO_PADDED} for the hour and
         * {@code MINUTE_24_ZERO_PADDED} for the minutes.
         */
        HOUR_MINUTE_24('R'),
        /**
         * Time formatted for the 24-hour clock as "hour:minute:second" using {@code HOUR_24_ZERO_PADDED} for the hour,
         * {@code MINUTE_24_ZERO_PADDED} for the minute, and {@code SECOND_24_ZERO_PADDED} for the second.
         */
        HOUR_MINUTE_SECOND_24('T'),
        /**
         * Time formatted for the 12-hour clock as "hour:minute:second marker" using {@code HOUR_12_ZERO_PADDED} for the
         * hour, {@code MINUTE_24_ZERO_PADDED} for the minute, {@code SECOND_24_ZERO_PADDED} for the second, and
         * {@code AM_PM} for the marker.
         */
        HOUR_MINUTE_SECOND_12('r'),
        /**
         * Date formatted as "month/day/year" using {@code MONTH_12} for the month, {@code DAY_ZERO_PADDED} for the day,
         * and {@code YEAR_TENS} for the year.
         */
        MONTH_DAY_YEAR('D'),
        /**
         * ISO 8601 complete date formatted as "year-month-day" using {@code YEAR} for the year, {@code MONTH_12} for
         * the month, and {@code DAY_ZERO_PADDED} for the day.
         */
        YEAR_MONTH_DAY_ISO8601('F'),
        /**
         * Date and time formatted as "weekday month day hour:minute:second time zone year",
         * <i>e.g.</i> "Sun Jul 20 16:17:00 EDT 1969".
         */
        DAY_TIME('c'),
        ;

        private final char dateTimeConversionCharacter;

        private DateTimeConversion(char dateTimeConversionCharacter) {
            this.dateTimeConversionCharacter = dateTimeConversionCharacter;
        }

        private char getDateTimeConversionCharacter() {
            return dateTimeConversionCharacter;
        }

        private static InjectiveMap<Character, DateTimeConversion> conversionMap;

        static {
            conversionMap = new InjectiveHashMap<Character, DateTimeConversion>();
            for (DateTimeConversion value : values())
                conversionMap.put(value.dateTimeConversionCharacter,value);
        }

        /**
         * Get the date/time conversion corresponding to a given character. If no conversion corresponds to the character,
         * {@code null} is returned.
         *
         * @param dateTimeConversionCharacter
         *        The character in question.
         *
         * @return the date/time conversion corresponding to {@code conversionCharacter}.
         */
        public static DateTimeConversion getTextFormatConversion(char dateTimeConversionCharacter) {
            return conversionMap.get(dateTimeConversionCharacter);
        }
    }

    /**
     * The {@code FormatFlag} enum represents every text format flag available to {@code TextFormat} instances.
     */
    public static enum FormatFlag {
        /**
         * The result will be left justified.
         */
        LEFT_JUSTIFIED('-'),
        /**
         * The result should use a conversion-dependent alternate form.
         */
        CONVERSION_DEPENDANT('#'),
        /**
         * The result will always include a sign (for numerical conversions).
         */
        INCLUDE_SIGN('+'),
        /**
         * The result will include a leading space for positive values.
         */
        POSITIVE_EXTRA_SPACE(' '),
        /**
         * The result will be zero-padded.
         */
        ZERO_PADDED('0'),
        /**
         * The result will include locale-specific grouping separators (<i>e.g.</i> 1000 becomes "1,000").
         */
        LOCAL_SPECIFIC_GROUPING(','),
        /**
         * The result will enclose negative numbers in parentheses.
         */
        NEGATIVE_PARENTHESES('(')
        ;

        private final char flagCharacter;

        private FormatFlag(char flagCharacter) {
            this.flagCharacter = flagCharacter;
        }

        private char getFlagCharacter() {
            return flagCharacter;
        }

        private static InjectiveMap<Character, FormatFlag> flagMap;

        static {
            flagMap = new InjectiveHashMap<Character, FormatFlag>();
            for (FormatFlag value : values())
                flagMap.put(value.flagCharacter,value);
        }

        /**
         * Get the format flag  corresponding to a given character. If no conversion corresponds to the character,
         * {@code null} is returned.
         *
         * @param formatFlagCharacter
         *        The character in question.
         *
         * @return the format flag corresponding to {@code conversionCharacter}.
         */
        public static FormatFlag getTextFormatConversion(char formatFlagCharacter) {
            return flagMap.get(formatFlagCharacter);
        }
    }
}
