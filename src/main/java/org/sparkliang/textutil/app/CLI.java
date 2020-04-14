package org.sparkliang.textutil.app;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparkliang.textutil.api.TransformPipe;
import org.sparkliang.textutil.api.Transformer;
import org.sparkliang.textutil.exception.TextTransformUtilException;
import org.sparkliang.textutil.impl.DefaultTransformPipe;
import org.sparkliang.textutil.impl.DefaultXMLParameterFileTransformer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The entry point of entire program.
 *
 * @author spark
 * @date 2020-04-14
 * @since 1.0
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class CLI {
    private static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);

    static Options OPTIONS = null;
    static String HELP_STRING = null;

    static Map<Class<?>, Object> INSTANCE_MAP = new HashMap<>();
    static Properties CONFIGURATION;
    static String SOURCE_PATH;
    static String TARGET_PATH;

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine commandLine = parser.parse(OPTIONS, args);
            if (Arrays.stream(commandLine.getOptions()).anyMatch(opt -> "h".equals(opt.getOpt()))) {
                System.out.println(HELP_STRING);
                return;
            }

            validateCommandLine(commandLine);
            getConfiguration(commandLine);
            doMain(commandLine);
        } catch (TextTransformUtilException e) {
            System.err.println("Error:" + e.getMessage() + "\n" + HELP_STRING);
            System.exit(-1);
        } catch (Exception e) {
            LOGGER.error("Unknown Error!", e);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bao));
            System.err.println("Unknown Error:" + e.getMessage() + "\n" + bao.toString() + "\n" + HELP_STRING);
            System.exit(-2);
        }

    }

    static void validateCommandLine(CommandLine commandLine) {
        String transformPipeName = commandLine.getOptionValue('p', DefaultTransformPipe.class.getName());
        assertClassExistsAndImplementAndCreateInstance(transformPipeName, TransformPipe.class);
        String transformerName = commandLine.getOptionValue('t', DefaultXMLParameterFileTransformer.class.getName());
        assertClassExistsAndImplementAndCreateInstance(transformerName, Transformer.class);

        String[] remainArgs = commandLine.getArgs();
        if (2 != remainArgs.length) {
            logAndThrowException("Invalid arguments.");
        }
        SOURCE_PATH = remainArgs[0];
        TARGET_PATH = remainArgs[1];
    }

    static void assertClassExistsAndImplementAndCreateInstance(String className, Class<?> implementsClass) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!implementsClass.isAssignableFrom(clazz)) {
                logAndThrowException(String.format("The given class \"%s\" is not implements \"%s\".", className, implementsClass.getName()));
            }
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            INSTANCE_MAP.put(implementsClass, implementsClass.cast(instance));
        } catch (ClassNotFoundException e) {
            logAndThrowException(String.format("The given class \"%s\" is not exists.", className));
        } catch (NoSuchMethodException e) {
            logAndThrowException(String.format("The given class \"%s\" does not have the default constructor.", className));
        } catch (IllegalAccessException e) {
            logAndThrowException(String.format("The default constructor of \"%s\" is unable to access.", className));
        } catch (InstantiationException | InvocationTargetException e) {
            logAndThrowException(String.format("The class \"%s\" is unable to create instance. Reason is: %s", className, e.getMessage()));
        }
    }

    static void getConfiguration(CommandLine commandLine) {
        CONFIGURATION = new Properties();
        String configFileDir = commandLine.getOptionValue("default-conf-dir");
        if (null != configFileDir) {
            File confFile = new File(configFileDir);
            try (InputStream inputStream = FileUtils.openInputStream(confFile)) {
                CONFIGURATION.load(inputStream);
            } catch (Exception e) {
                logAndThrowException(String.format("Unable to open %s. Reason is: %s", configFileDir, e.getMessage()));
            }
        }
        String[] configurations = commandLine.getOptionValues("conf");
        if (null != configurations) {
            for (String configuration : configurations) {
                int separatorIdx = configuration.indexOf("=");
                if (0 == separatorIdx) {
                    logAndThrowException(
                            String.format("Invalid configuration %s. The correct format is <key>=<value>.", configuration)
                    );
                }

                CONFIGURATION.setProperty(configuration.substring(0, separatorIdx), configuration.substring(separatorIdx + 1));
            }
        }
    }

    static void doMain(CommandLine commandLine) {
        TransformPipe pipe = (TransformPipe) INSTANCE_MAP.get(TransformPipe.class);
        pipe.set(CONFIGURATION);
        Transformer transformer = (Transformer) INSTANCE_MAP.get(Transformer.class);
        transformer.set(CONFIGURATION);

        pipe.transform(SOURCE_PATH, TARGET_PATH, transformer);
    }

    private static void logAndThrowException(String message) {
        LOGGER.warn(message);
        throw new TextTransformUtilException(message);
    }

    private static void initCliOption() {
        if (OPTIONS == null) {
            Options options = new Options();

            // help
            options.addOption("h", "help", false, "usage help");
            // TransformPipe Class name
            options.addOption(
                    Option.builder("p").longOpt("pipe").argName("Class name of transform pipe").type(String.class)
                            .optionalArg(false).required(false).numberOfArgs(1)
                            .desc("The class to control which file that need to be transformed and how to place the file in target folder.\n" +
                                    "The default value is " + DefaultTransformPipe.class.getName() + '.'
                            )
                            .build()
            );
            // TextUtil Class name
            options.addOption(
                    Option.builder("t").longOpt("transformer").argName("Class name of text transformer").type(String.class)
                            .optionalArg(false).required(false).numberOfArgs(1)
                            .desc("The class to control how the program transform the content in each file.\n" +
                                    "The default value is " + DefaultXMLParameterFileTransformer.class.getName() + '.'
                            )
                            .build()
            );
            // The configuration file path
            options.addOption(
                    Option.builder().longOpt("default-conf-dir").argName("The configuration file path").type(String.class)
                            .optionalArg(false).required(false).numberOfArgs(1)
                            .desc("The file path of the configuration file.")
                            .build()
            );
            // The command line configuration
            options.addOption(
                    Option.builder().longOpt("conf").argName("The configuration").type(String.class)
                            .optionalArg(false).required(false).numberOfArgs(1)
                            .desc("The configuration provided via command line which is usually for override the value config in the file.\n" +
                                    "The format of each argument is <key>=<value>.")
                            .build()
            );

            OPTIONS = options;
        }
    }

    private static void initHelpString() {

        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH * 2, "text-util [options] source target", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
    }

    static {
        initCliOption();
        initHelpString();
    }
}
