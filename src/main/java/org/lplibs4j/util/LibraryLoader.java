package org.lplibs4j.util;

import java.io.*;

/**
 * Class which allows to load a dynamic file as resource (for example, from a
 * jar-file)
 */
public class LibraryLoader {

    private Logger logger;
    private String libpath;

    private static File tempDir;

    static {

        final Logger logger = Logger.getLogger();

        try {
            tempDir = File.createTempFile("lplibs4j", "");

            if (!tempDir.delete() || !tempDir.mkdir()) {
                throw new IOException(String.format("Couldn't create directory \"%s\"", tempDir.getAbsolutePath()));
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    for (File f : tempDir.listFiles()) {
                        logger.info("Deleting " + f.getAbsolutePath());
                        if (!f.delete()) {
                            logger.warning(String.format("Couldn't delete temporary file \"%s\"", f.getAbsolutePath()));
                        }
                    }
                    logger.info("Deleting " + tempDir.getAbsolutePath());
                    if (!tempDir.delete()) {
                        logger.warning(String.format("Couldn't delete temporary directory \"%s\"", tempDir.getAbsolutePath()));
                    }
                }
            });
        } catch (IOException ex) {
            logger.error("Couldn't create temporary directory: " + ex.getMessage());
        }
    }

    public LibraryLoader() {
        logger = Logger.getLogger();
        libpath = null;
    }

    /**
     * <p>Find the library <tt>libname</tt> as a resource, copy it to a tempfile
     * and load it using System.load(). The name of the library has to be the
     * base name, it is mapped to the corresponding system name using
     * System.mapLibraryName(). For example, the library "foo" is called "libfoo.so"
     * under Linux and "foo.dll" under Windows, but you just have to pass "foo"
     * the loadLibrary().</p>
     * <p/>
     * <p>I'm not quite sure if this doesn't open all kinds of security holes. Any ideas?</p>
     * <p/>
     * <p>This function reports some more information to the "org.lplibs4j" logger at
     * the FINE level.</p>
     *
     * @param libname basename of the library
     * @throws UnsatisfiedLinkError if library cannot be founds
     */
    public void loadLibrary(String libname) {

        libname = System.mapLibraryName(libname);

        /*
         * JDK 7 changed the ending for Mac OS from "jnilib" to "dylib".
         *
         * If that is the case, remap the filename.
         */
        String loadLibname = libname;
        if (libname.endsWith("dylib")) {
            loadLibname = libname.replace(".dylib", ".jnilib");
            logger.config("Replaced .dylib with .jnilib");
        }

        logger.debug("Attempting to load \"" + loadLibname + "\".");
        String[] paths = {
                fatJarLibraryPath("static"),
                fatJarLibraryPath("dynamic"),
        };

        InputStream is = findLibrary(paths, loadLibname);

        // Haven't found the lib anywhere? Throw a reception.
        if (is == null) {
            throw new UnsatisfiedLinkError("Couldn't find the resource " + loadLibname + ".");
        }

        logger.config("Loading " + loadLibname + " from " + libpath + ", copying to " + libname + ".");
        loadLibraryFromStream(libname, is);
    }

    private InputStream findLibrary(String[] paths, String libname) {
        InputStream is = null;
        for (String path : paths) {
            is = tryPath(path + libname);
            if (is != null) {
                logger.debug("Found " + libname + " in " + path);
                libpath = path;
                break;
            }
        }
        return is;
    }

    /**
     * Translate all those Windows to "Windows". ("Windows XP", "Windows Vista", "Windows 7", etc.)
     */
    private String unifyOSName(String osname) {
        if (osname.startsWith("Windows")) {
            return "Windows";
        }
        return osname;
    }

    /**
     * Compute the path to the library. The path is basically
     * "/" + os.name + "/" + os.arch + "/" + libname.
     */
    private String fatJarLibraryPath(String linkage) {
        String sep = "/"; //System.getProperty("file.separator");
        String os_name = unifyOSName(System.getProperty("os.name"));
        String os_arch = System.getProperty("os.arch");
        String path = sep + "lib" + sep + linkage + sep + os_name + sep + os_arch + sep;
        return path;
    }

    /**
     * Try to open a file at the given position.
     */
    private InputStream tryPath(String path) {
        Logger.getLogger().debug("Trying path \"" + path + "\".");
        // TODO: here I am fixing stuff to load from hard-drive, not from jar
//        return getClass().getResourceAsStream(path);
        return new ByteArrayInputStream(path.getBytes());
    }

    private File createTempFile(String name) throws IOException {
        return new File(tempDir + File.separator + name);
    }

    /**
     * Load a system library from a stream. Copies the library to a temp file
     * and loads from there.
     *
     * @param libname name of the library (just used in constructing the library name)
     * @param is      InputStream pointing to the library
     */
    private void loadLibraryFromStream(String libname, InputStream is) {
        try {
            File tempfile = createTempFile(libname);
            OutputStream os = new FileOutputStream(tempfile);

            logger.debug("tempfile.getPath() = " + tempfile.getPath());

            long savedTime = System.currentTimeMillis();

            // Leo says 8k block size is STANDARD ;)
            byte buf[] = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }

            double seconds = (double) (System.currentTimeMillis() - savedTime) / 1e3;
            logger.debug("Copying took " + seconds + " seconds.");

            os.close();

            logger.debug("Loading library from " + tempfile.getPath() + ".");
            System.load(tempfile.getPath());
        } catch (IOException io) {
            logger.error("Could not create the temp file: " + io.toString() + ".\n");
        } catch (UnsatisfiedLinkError ule) {
            logger.error("Couldn't load copied link file: " + ule.toString() + ".\n");
            throw ule;
        }
    }
}
