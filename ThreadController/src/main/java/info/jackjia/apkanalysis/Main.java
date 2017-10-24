package info.jackjia.apkanalysis;

import info.jackjia.apkanalysis.Grepper.GrepController;
import info.jackjia.apkanalysis.Test.TestController;
import info.jackjia.apkanalysis.apkdownloader.DownloadController;
import info.jackjia.apkanalysis.decoder.DecodeController;
import info.jackjia.apkanalysis.installer.InstallController;
import info.jackjia.apkanalysis.lister.ListController;
import info.jackjia.apkanalysis.popularity.Popularity;
import org.apache.commons.cli.*;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ObjectArrayGetAtMetaMethod;
import org.codehaus.groovy.tools.shell.Command;

import java.io.File;

public class Main {

    private static Options allOptions;
    private static Options normalOptions;
    private static Options downloadOptions;
    private static Options decodeOptions;
    private static Options installOptions;
    private static Options grepOptions;
    private static Options listOptions;
    private static Options popularityOptions;
    static {
        allOptions = new Options();
        normalOptions = new Options();
        downloadOptions = new Options();
        decodeOptions = new Options();
        installOptions = new Options();
        grepOptions = new Options();
        listOptions = new Options();
        popularityOptions = new Options();
    }


    private static int nThread = 1;

    public static void main(String[] args) {
        //
        // Parse command line options
        //
        // Refer to:
        // https://github.com/iBotPeaches/Apktool/blob/master/brut.apktool/apktool-cli/src/main/java/brut/apktool/Main.java
        //
        CommandLineParser commandParser = new BasicParser();
        setupOptions();
        CommandLine commandLine = null;

        try {
            commandLine = commandParser.parse(allOptions, args);
        } catch (Exception e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
            printUsage();
            System.exit(-1);
        }

        //
        // Common options
        //
        if (commandLine.hasOption("thread")) {
            nThread = Integer.valueOf(commandLine.getOptionValue("thread"));
        }

        //
        // Parse options for specific mode
        //
        boolean cmdFound = false;
        for (String opt : commandLine.getArgs()) {
            if (opt.equalsIgnoreCase("download")) {
                cmdDownload(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("decode")) {
                cmdDecode(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("analyze")) {
                cmdAnalyze(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("test")) {
                cmdTest(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("install")){
                cmdInstall(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("grep")){
                cmdGrep(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("list")){
                cmdList(commandLine);;
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("popularity")){
                cmdPopularity(commandLine);
                cmdFound = true;
            }

        }

        if (cmdFound == false) {
            printUsage();
            System.exit(-1);
        }
    }
    private static void cmdPopularity(CommandLine cli){
        Popularity p = new Popularity();
        Popularity.readFile();
        Popularity.readMetaData();
        //System.out.println(p.apks.size());
    }
    private static void cmdList(CommandLine cli){
        int nThread = Integer.valueOf(cli.getOptionValue("thread"));
        ListController c = new ListController(nThread);
        if (cli.hasOption("srcdir")) {
            String srcDir = cli.getOptionValue("srcdir");
            if (new File(srcDir).exists()) {
                c.setSrcDir(srcDir);
            } else {
                System.err.println("srcdir: " + srcDir + " doesn't exist");
            }
        }
        if (cli.hasOption("outdir")) {
            c.setOutDir(cli.getOptionValue("outdir"));
        }
        c.start();
    }
    private static void cmdGrep(CommandLine cli){
        int nThreads = Integer.valueOf(cli.getOptionValue("thread"));
        GrepController c = new GrepController(nThreads);
        c.recursiveMode(true);
        if (cli.hasOption("srcdir")) {
            String srcDir = cli.getOptionValue("srcdir");
            if (new File(srcDir).exists()) {
                c.setSrcDir(srcDir);
            } else {
                System.err.println("apkdir: " + srcDir + " doesn't exist");
            }
        }
        if (cli.hasOption("outdir")) {
            c.setOutDir(cli.getOptionValue("outdir"));
        }
        c.start();
    }
    private static void cmdTest(CommandLine cli){
        int nThreads = Integer.valueOf(cli.getOptionValue("thread"));
        TestController c = new TestController(nThreads);
        c.start();

    }
    private static void cmdInstall(CommandLine cli){
        int nThreads = Integer.valueOf(cli.getOptionValue("thread"));
        InstallController c = new InstallController(nThreads);
        c.recursiveMode(true);
        if (cli.hasOption("apkdir")) {
            String apkDir = cli.getOptionValue("apkdir");
            if (new File(apkDir).exists()) {
                c.setApkDir(apkDir);
            } else {
                System.err.println("apkdir: " + apkDir + " doesn't exist");
            }
        }
        if (cli.hasOption("packagelist")){
            String package_list  = cli.getOptionValue("packagelist");
            if (new File(package_list).exists()){
                c.setPackageList(package_list);
            }
            else{
                System.err.println("package list: " + package_list + " doesn't exist");
            }
        }
        if (cli.hasOption("out")) {
            c.setOutDir(cli.getOptionValue("out"));
        }
        c.start();
    }
    private static void cmdDownload(CommandLine cli) {
        int nThreads = Integer.valueOf(cli.getOptionValue("thread"));
        String input = cli.getOptionValue("dataset");
        String destdir = cli.getOptionValue("dest");

        //
        // Start working
        //
        DownloadController c = new DownloadController(nThreads);
        c.setDataFile(input);
        c.setDestDir(destdir);
        c.run();
    }

    private static void cmdDecode(CommandLine cli) {
        DecodeController decoder = new DecodeController(nThread);
        if (cli.hasOption("r")) {
            decoder.recursiveMode(true);
        }
        if (cli.hasOption("apkdir")) {
            String apkDir = cli.getOptionValue("apkdir");
            if (new File(apkDir).exists()) {
                decoder.setApkDir(apkDir);
            } else {
                System.err.println("apkdir: " + apkDir + " doesn't exist");
            }
        }
        if (cli.hasOption("out")) {
            decoder.setOutDir(cli.getOptionValue("out"));
        }

        decoder.start();
    }

    private static void cmdAnalyze(CommandLine cli) {
    }


    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
        // formatter.printHelp("apkanalysis.jar", allOptions);
        formatter.printHelp("apkanalysis.jar", normalOptions);
        formatter.printHelp("apkanalysis.jar download [options]", downloadOptions);
        formatter.printHelp("apkanalysis.jar decode [options]", decodeOptions);
        formatter.printHelp("apkanalysis.jar grep [options]",grepOptions);
        formatter.printHelp("apkanalysis.jar list [options]",listOptions);
        formatter.printHelp("apkanalysis.jar popularity",popularityOptions);
    }

    public static void setupOptions() {
        Option help = new Option("h", "help", false, "print this message");
        Option thread = OptionBuilder.withArgName("num")
                .hasArg()
                .withDescription("number of worker threads")
                .create("thread");

        normalOptions.addOption(help);
        normalOptions.addOption(thread);

        //
        // Download mode options
        //
        Option dataset = OptionBuilder.withArgName("jsonfile")
                .hasArg()
                .withDescription("read apk info from given file")
                .create("dataset");
        Option destdir = OptionBuilder.withArgName("dir")
                .hasArg()
                .withDescription("the destination where apks will be downloaded to")
                .create("dest");

        downloadOptions.addOption(dataset);
        downloadOptions.addOption(destdir);

        //
        // Decode mode options
        //
        Option apkdir = OptionBuilder.withArgName("apkdir")
                .hasArg()
                .withDescription("all APKs in this dir will be decoded")
                .create("apkdir");
        Option recursive = OptionBuilder
                .withDescription("find APKs recursively in <apkdir>")
                .create("r");
        Option output = OptionBuilder.withArgName("output")
                .hasArg()
                .withDescription("output dir")
                .create("out");
        decodeOptions.addOption(apkdir);
        decodeOptions.addOption(recursive);
        decodeOptions.addOption(output);

        //
        // Install options
        //
        Option packagelist = OptionBuilder.withArgName("packagelist")
                .hasArg()
                .withDescription("the selected package list")
                .create("packagelist");
        installOptions.addOption(packagelist);
        //
        // Grep options
        //
        Option srcdir = OptionBuilder.withArgName("srcdir")
                .hasArg()
                .withDescription("the Smali source dir")
                .create("srcdir");
        Option outdir = OptionBuilder.withArgName("outdir")
                .hasArg()
                .withDescription("the output file name and dir")
                .create("outdir");
        Option keyword = OptionBuilder.withArgName("keyword")
                .hasArg()
                .withDescription("the keyword to grep")
                .create("keyword");
        grepOptions.addOption(srcdir);
        grepOptions.addOption(outdir);
        grepOptions.addOption(keyword);
        //
        //List options
        //
        listOptions.addOption(srcdir);
        listOptions.addOption(outdir);

        // Add all options
        for (Object o : normalOptions.getOptions()) {
            allOptions.addOption((Option) o);
        }
        for (Object o : downloadOptions.getOptions()) {
            allOptions.addOption((Option) o);
        }
        for (Object o : decodeOptions.getOptions()) {
            allOptions.addOption((Option) o);
        }
        for (Object o : installOptions.getOptions()){
            allOptions.addOption((Option) o);
        }
        for (Object o : grepOptions.getOptions()){
            allOptions.addOption((Option) o);
        }
        for (Object o : popularityOptions.getOptions()){
            allOptions.addOption((Option) o);
        }
    }
}
