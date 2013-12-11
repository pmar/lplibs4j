package org.lplibs4j.util;

/**
 * Help class for loading libraries needed for NativeBlas
 *
 * The only use of this class is to have NativeBlas inherit from this class.
 *
 */
public class LibraryLoaderGLPK {

    public static void loadLibraryAndCheckErrors() {

        try {

            String libName = "glpkjni";

            try {

                // Try to load it first, probably it's in the path
                System.loadLibrary(libName);

            } catch (UnsatisfiedLinkError e) {

                // Nope, ok, so let's copy it.
                Logger.getLogger().config(
                        "GLPK native library not found in path. Copying native library "
                                + "from the archive. Consider installing the library somewhere "
                                + "in the path (for Windows: PATH, for Linux: LD_LIBRARY_PATH).");

                // potentially load dependent libraries (mostly Cygwin libs for Windows)
                loadDependentLibraries();

                // Ok, and now load it!
                new LibraryLoader().loadLibrary(libName);

            }

            // TODO
            // Let's do some quick tests to see whether we trigger some errors
            // when dependent libraries cannot be found.
            //
            // In Jblas case this was (something I should put in NEST operators as well)
            //   double[] a = new double[1];
            //   NativeBlas.dgemm('N', 'N', 1, 1, 1, 1.0, a, 0, 1, a, 0, 1, 1.0, a, 0, 1);
            //
            // Make this check LP library specific,

        } catch (UnsatisfiedLinkError e) {

            String arch = System.getProperty("os.arch");
            String name = System.getProperty("os.name");

            if (name.startsWith("Windows") && e.getMessage().contains("Can't find dependent libraries")) {

                System.err.println("On Windows, you need some additional support libraries.\n" +
                        "For example, you can install one package in cygwin:\n" +
                        "\n" +
                        "   mingw64-x86_64-gcc-core\n" +
                        "\n" +
                        "and add the directory <cygwin-home>\\usr\\x86_64-w64-mingw32\\sys-root\\mingw\\bin to your path.\n");

            } else if (name.equals("Linux") && arch.equals("amd64")) {

                System.err.println("On Linux 64bit, you need additional support libraries.\n" +
                        "You need to install libgcc.\n\n" +
                        "For example for debian or Ubuntu, type \"sudo apt-get install gcc\"\n\n" +
                        "For more information, see https://github.com/mikiobraun/jblas/wiki/Missing-Libraries");

            }

        } catch (org.lplibs4j.util.exceptions.UnsupportedArchitectureException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadDependentLibraries() {
        String arch = System.getProperty("os.arch");
        String name = System.getProperty("os.name");


        // These are JBlas dependent libraries
        LibraryLoader loader = new LibraryLoader();

        if (name.startsWith("Windows") && arch.equals("amd64")) {
            //  loader.loadLibrary("libglpk");
            //  loader.loadLibrary("libgfortran-3");
        } else if (name.startsWith("Windows") && arch.equals("x86")) {
            // loader.loadLibrary("libgcc_s_dw2-1");
            // loader.loadLibrary("libgfortran-3");
        }
    }
}
