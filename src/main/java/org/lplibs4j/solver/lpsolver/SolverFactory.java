package org.lplibs4j.solver.lpsolver;

import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.problems.LinearProgram;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Basic service provider for the LinearProgramSolver interface. Loads all found classes and returns
 * the first found class, if required.
 *
 * Implementation mainly inspired by
 * {@url http://java.sun.com/developer/technicalArticles/javase/extensible/index.html}
 *
 * @author michael
 */
public class SolverFactory {

    private static ServiceLoader<LinearProgramSolver> loader = null;

    private static int DEBUG = 2;
    private static ArrayList<String> loadedLibraries = new ArrayList<>();

    /**
     * This class is a singleton at all and cannot be initialized.
     */
    private SolverFactory () {
    }

    /**
     * Searches for service provider of the LinearProgramSolver service and returns
     * the first instance it found. If no service was found, it prints the stack trace to
     * stderr and returns null.
     *
     * When this method is first invoked, it initializes the ServiceLoader, which locates
     * available services from the classpath. Subsequent calls use the result of this first
     * initialization. Therefore, if there are no available services at the moment of the first call,
     * there will be no available services for the rest of the program run.
     *
     * Future implementations might ignore this problem.
     *
     * @return the first LinearProgramSolver Service Provider found
     */
    public static LinearProgramSolver newDefault() {

        if (loader == null) {
            debug("First call to the solver factory - trying to load the service providers ... done",2);
            loader = ServiceLoader.load(LinearProgramSolver.class);
        } else {
            debug("Subsequent call to the solver factory - service providers already loaded",2);
        }

        LinearProgramSolver lpsolver = null;

        try {

            Iterator<LinearProgramSolver> lpsolvers = loader.iterator();

            while (lpsolver == null && lpsolvers.hasNext()) {

                lpsolver = lpsolvers.next();
                debug("Checking solver: " + lpsolver.getName(),0);

                // If the libraries cannot be loaded we will not return this solver
                if (!loadLibraries(lpsolver.getLibraryNames()))
                    lpsolver = null;
            }


            if (lpsolver == null)
                debug("No service provider found!",0);
            else {
                debug("Returning the following solver: " + lpsolver.getName(),0);
                debug("More available service providers: ",1);
                while (lpsolvers.hasNext())
                    debug(lpsolvers.next().getName(),1);
                debug("End of service providers list!",1);
            }

        } catch (ServiceConfigurationError e) {
            lpsolver = null;
            e.printStackTrace();
        }

        return lpsolver;
    }

    /**
     * Tries to get a specific solver by its name. Like the <code>newDefault</code> method,
     * the <code>getSolver</code> method searches through the list of service providers and
     * compares the <code>name</code> parameter to the result of the method call <code>getName</code>
     * of every service provider, until either the matching service provider is found and
     * returned or there are no further service providers available. In this case, the method
     * returns the same result as the <code>newDefault</code> method.
     *
     * @param name the name of the requested linear program solver
     * @return an instance of the requested solver, if it was found or the result of the
     * <code>newDefault</code> method call
     */
    public static LinearProgramSolver getSolver(String name) {

        if (loader == null) {
            debug("First call to the solver factory - trying to load the service providers ... done",2);

            loader = ServiceLoader.load(LinearProgramSolver.class);
        } else{
            debug("Subsequent call to the solver factory - service providers already loaded",2);
        }

        LinearProgramSolver lpsolver = null;
        Iterator<LinearProgramSolver> foundlpsolvers = loader.iterator();

        debug("Solvers found:",2);
        for (LinearProgramSolver sol : loader) {
            debug(sol.getName(),2);
        }

        try {
            Iterator<LinearProgramSolver> lpsolvers = loader.iterator();

            while (lpsolver == null && lpsolvers.hasNext()) {
                lpsolver = lpsolvers.next();

                // We're trying to find a specific solver
                if (!name.equals(lpsolver.getName())) {
                    lpsolver = null;
                    continue;
                }
                else
                    debug("Found solver: " + name,0);

                // If the solver REQUIRES libraries and those cannot be loaded we will not return this solver
                if (lpsolver.getLibraryNames() != null) {
                    if (!loadLibraries(lpsolver.getLibraryNames())) {
                        lpsolver = null;
                    }
                }
            }

            if (lpsolver == null) {

                debug("Could not find specific solver: " + name,0);
                debug("Returning new default solver ...",0);

                lpsolver = newDefault();
            }

        } catch (ServiceConfigurationError e) {
            lpsolver = null;
            e.printStackTrace();
        }

        return lpsolver;
    }

    /**
     * Loads all libraries and returns the success status of the individual calls.
     *
     * @return <code>true</code> iff all libraries were loaded successfully
     */
    private static boolean loadLibraries (String[] libraries) {

        if (libraries == null)
            return true;

        boolean success = true;

        for (String s : libraries)
            success = success && loadLibrary(s);


        if (!success)
            debug("Could not load all necessary libraries",0);


        return success;
    }

    /**
     * Loads a single library and returns true after success.
     *
     * @return <code>true</code> iff the library was loaded successfully
     */
    private static boolean loadLibrary (String library) {

        // Looking for real library name based on possible architecture modifications
        String libname = modifyLibname(library);
        debug("Trying to load library: " + libname,1);
        if (loadedLibraries.contains(libname)) {
            debug("Already loaded",1);
            return true;
        }

        try {

            // This way, the JVM automatically searches the lib-path for the library
            System.loadLibrary(libname);
            loadedLibraries.add(libname);
            debug("Regular library load succesful",1);
            return true;

        } catch (UnsatisfiedLinkError error) {

            // TODO Check if library was extracted and loaded previously!

            // Library was not found in the library path
            // Trying to extract it from the jar-file
            debug("Regular loading failed. Trying to extract library from jar-file",1);

            // Somewhat inspired by
            // http://www.javaworld.com/javatips/javatip49/JarResources.java
            try {

                String fileSeparator = System.getProperty("file.separator");
                String pathSeparator = System.getProperty("path.separator");
                String classpath = System.getProperty("java.class.path");
                String qualifiedLibname = System.mapLibraryName(libname);


                debug("Checking paths and filenames:",2);
                debug("Full library name is: " + qualifiedLibname,2);
                //debug("java.class.path is: " + classpath,2);

                // We must search for the library in all jar-files, because we don't know in which jar-file the solver came
                Pattern filename = Pattern.compile(".*jar");
                String[] files = classpath.split(pathSeparator);
                debug(files.length + " files in classpath",2);
                for (String s : files) {
                    //	debug(s,2);
                }
                // Searching for the libraries in the jars with a regexp enables that the libraries can be stored in a subdirectory
                Pattern ziplibname = Pattern.compile(".*" + qualifiedLibname);

                InputStream in = null;
                for (String s : files) {
                    // Looking up all the contents of every jar-file
                    if (s != null) {
                        if (!filename.matcher(s).matches())
                            continue;
                        debug("Checking " + s,2);
                        try {

                            ZipFile jarfile = new ZipFile(s);
                            Enumeration<? extends ZipEntry> e = jarfile.entries();

                            while (e.hasMoreElements() && in == null) {
                                ZipEntry ze = e.nextElement();

                                //debug("Current file is: " + ze.getName(), 2);
                                if (ziplibname.matcher(ze.getName()).matches()) {
                                    debug("FOUND!!!", 2);

                                    in = jarfile.getInputStream(ze);
                                }
                            }
                            debug("Finished " + s,2);
                        } catch (Exception e) {

                            debug("An "+ e.getMessage() + " exception occured while trying to open as a ZIP-File.",2);

                        }

                        if (in != null) {
                            debug("File found in " + s, 2);
                            break;
                        }
                    }
                }


                if (in == null) {
                    debug("Could not find required library: " + libname,0);
                    return false;
                }

                // Okay, we found the library
                // Now we have to write it to the file-system somewhere convenient
                // The following locations are tried in this order:
                // - any directory in the library path
                // - the current working directory
                // (- directories containing jar-files) <-- not implemented yet!
                // (- user.home) <-- not requested so far
                // - the system temp-directory

                // Trying library path locations:
                String libpath = System.getProperty("java.library.path");
                debug("java.library.path is: " + libpath,2);

                if (libpath != null && libpath.length() > 0) {
                    debug("java.library.path seems to contain valid paths",2);

                    String[] paths = libpath.split(pathSeparator);
                    for(String fullname : paths) {
                        fullname = fullname + fileSeparator + qualifiedLibname;
                        if (writeInStreamToFile(in, fullname)) {
                            debug("Wrote library to library path: " + fullname,1);
                            System.loadLibrary(libname);
                            loadedLibraries.add(libname);
                            return true;
                        }
                    }
                } else {
                    debug("java.library.path contains no valid paths",2);
                }

                // Now test the rest locations ...
                String[] paths = new String[2];
                paths[0] = System.getProperty("user.dir");
                paths[1] = System.getProperty("java.io.tmpdir");

                for(String fullname : paths) {
                    fullname = fullname + fileSeparator + qualifiedLibname;
                    if (writeInStreamToFile(in, fullname)) {
                        debug("Wrote library to regular path: " + fullname,1);

                        System.load(fullname);
                        loadedLibraries.add(libname);
                        return true;
                    }
                }

                // No more locations to write file
                return false;

            } catch (Exception e) {
                // Looking up jar-files failed
                System.err.println("An error occured while searching for the library in the solver module:");
                e.printStackTrace();
            }
        }

        // If we end up here, we could not load the library!
        return false;
    }

    /**
     * Tries to write the input stream to the location specified in filename. If
     * copying was successful, the method returns true, otherwise it returns false.
     *
     * @param in the input stream containing the source file
     * @param filename the destiny location the file is to be written to
     * @return <code>true</code> if copying was successful, <code>false</code> otherwise
     */
    private static boolean writeInStreamToFile(InputStream in, String filename) {

        try {
            File f = new File(filename);
            if(f.exists()) { return true; }
            // Opening an appropriate output stream and copy the library
            FileOutputStream fileout = new FileOutputStream(filename);
            BufferedOutputStream out = new BufferedOutputStream(fileout);

            byte[] buffer = new byte[1024];
            int len;

            while((len = in.read(buffer)) >= 0)
                out.write(buffer, 0, len);

            in.close();
            out.close();

            debug("Copied file successfully to: " + filename,2);

            return true;

        } catch (IOException e) {
            // TODO Is this the correct way of checking whether file copying worked?
            // TODO Do have to add functionality that deletes half-copied files?

            debug("Failed to write file to: " + filename, 1);
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifies the base library name to the modified library name depending
     * on different architecture types. Therefore, it checks which properties
     * to determine the underlying architecture are available and then adds it
     * to the current library name.
     *
     * @param libname the unmodified library name
     * @return the modified library name based on the specifications in the module manual
     */
	/*
	 * This might cause speed issues if the method gets called with every
	 * loading attempt to a library (and there might be more than one attempt
	 * to load the same library). Solutions could include:
	 * - saving the loaded libraries list in a static class variable and
	 *   consult this list first
	 * - build the modifier string only once in a static class variable 
	 */
    private static String modifyLibname(String libname) {
        StringBuilder sb = new StringBuilder(libname);

        if (System.getProperty("sun.arch.data.model") != null) {
            if (System.getProperty("sun.arch.data.model").equalsIgnoreCase("64")) {
                sb.append("_x64");
                debug("64-bit detected",2);
            }
        }
        else if (System.getProperty("os.arch.data.model") != null) {
            if (System.getProperty("os.arch.data.model").equalsIgnoreCase("64")) {
                sb.append("_x64");
                debug("64-bit detected",2);
            }
        }
        else if (System.getProperty("os.arch") != null) {
            // TODO Which properties are there and how are they modified?
            // TODO Test multiple OS's and architectures (32bit, 64bit)
            if (System.getProperty("os.arch").indexOf("64") > -1) {
                sb.append("_x64");
                debug("64-bit detected",2);
            }

            System.err.println("NOTE: SolverFactory tried to detect library name modifications,");
            System.err.println("but is not sure if all was detected correctly. If the method");
            System.err.println("continues to fail write an email stating all your system properties");
            System.err.println("to: hannes.planatscher@informatik.uni-tuebingen.de");
        }

        return sb.toString();
    }

    /**
     * Writes debug messages to standard error, if level < DEBUG. Messages are
     * indented 4*level spaces.
     *
     * @param msg the debug message
     * @param level the level of the debug message
     */
    private static void debug(String msg, int level) {

        if (level < DEBUG) {
            System.err.print("DEBUG: ");
            for (int i = 0; i < level; i++)
                System.err.print("    ");
            System.err.println(msg);
        }
    }

    // TODO Remove before release - for testing purposes only!
    public static void main (String[] args) {

        LinearProgram lp = new LinearProgram(new double[]{25.0, 30.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{(1.0/200.0), (1.0/140.0)}, 40, "Time"));
        lp.setLowerbound(new double[]{0.0, 0.0});
        lp.setUpperbound(new double[]{6000, 4000});
        lp.setInteger(0);
        lp.setInteger(1);

        // System.out.print(lp.convertToCPLEX());

        LinearProgramSolver lpsolver = SolverFactory.newDefault();

        // This line should hold, if no service providing jar is provided
        //		Assert.assertEquals(lpsolver, null);

        // The following four lines should hold, if a functioning service providing jar is provided
        double[] solution = lpsolver.solve(lp);
        System.out.println("Solved with: " + lpsolver.getName());
        double[] expected = {6000.0, 1400.0};
        for (int i = 0; i < solution.length; i++)
            if (Math.abs(expected[i] - solution[i]) > 0.001)
                System.out.println("WRONG RESULTS - but still, it works ;-)");

        System.out.println("New method invocation!");
        LinearProgramSolver lpsolver2 = SolverFactory.getSolver("GLPK");
        lpsolver2.solve(lp);
        System.out.println("What is going on here?");
        lpsolver2 = SolverFactory.getSolver("CPLEX");

        System.out.println("Got solver CPLEX - trying to solve ...");
        lpsolver2.solve(lp);
        System.out.println("Solved with: " + lpsolver2.getName());
		/*
              Map<String, String> info = System.getenv();
              for (Map.Entry<String, String> e : info.entrySet()) {
                  System.out.println("INFO:\t" + e.getKey() + " : " + e.getValue());
              }
		 */

		/*
              Properties p = System.getProperties();
              for (Map.Entry<Object, Object> e : p.entrySet()) {
                  System.out.println("INFO:\t" + e.getKey().toString() + " : " + e.getValue().toString());
              }
		 */
    }

}
