package org.oaky.cuke4duke;

import cuke4duke.ant.CucumberTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.StringUtils;
import org.jruby.Main;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JUnitRunnerCucumberTask extends CucumberTask {

    private final Log log = LogFactory.getLog(this.getClass());
    private final Map<String,String> envVars = new HashMap<String,String>();

    public JUnitRunnerCucumberTask() {
        Project project = new Project();
        setProject(project);
        setFork(false);
    }

	public File getBinDir() {
	    return super.getBinDir();
	}

    public void setClasspath(String classpath) {
        Path path = new Path(getProject(), classpath);
        getProject().addReference("jruby.classpath", path);
    }

    public String getClasspath() {
        return ((Path) getProject().getReference("jruby.classpath")).toString();
    }
    
    public void setObjectFactory(Class clazz) {
        if (clazz != null) {
            envVars.put("cuke4duke.objectFactory", clazz.getName());
        }
    }

    public void setGemHome(String gemHome) {
        envVars.put("jruby.gem.home", gemHome);
        envVars.put("jruby.gem.path", gemHome);
    }

    @Override
    protected int executeJava(CommandlineJava commandLine) {
        OutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);

        Properties systemProperties = System.getProperties();
        Properties cucumberSystemProperties = new Properties();
	    cucumberSystemProperties.putAll(systemProperties);

	    String gemHomeString = envVars.get("jruby.gem.home");
	    if (gemHomeString.startsWith("classpath:")) {
		    final URL resource = Ruby.getClassLoader().getResource(gemHomeString.substring("classpath:/".length()));
		    if (resource != null) {
			    gemHomeString = new File(resource.getFile()).getAbsolutePath();
			    envVars.put("jruby.gem.home", gemHomeString);
			    envVars.put("jruby.gem.path", gemHomeString);
		    }
	    }

        for (Map.Entry<String,String> o : envVars.entrySet()) {
            cucumberSystemProperties.setProperty(o.getKey(), o.getValue());
        }
        System.setProperties(cucumberSystemProperties);

        try {
	        exec(commandLine, bos);
        } catch(Exception e) {
		    throw new RuntimeException(e);
        } finally {
            System.setProperties(systemProperties);
        }
//        System.out.println(bos.toString());
        return 0;
    }

	private void exec(CommandlineJava commandLine, OutputStream os) throws Exception {
		String[] args = commandLine.getJavaCommand().getArguments();

		String gemHomeString = envVars.get("jruby.gem.home");
//		if (gemHomeString.startsWith("classpath:")) {
//			final URL resource = Ruby.getClassLoader().getResource(gemHomeString.substring("classpath:/".length()));
//			if (resource != null) {
////				gemHomeString = "classpath:" + gemHomeString.substring("classpath:/".length());
//				gemHomeString = new File(resource.getFile()).getAbsolutePath().replace('\\','/');
//				envVars.put("jruby.gem.home", gemHomeString);
//				envVars.put("jruby.gem.path", gemHomeString);
//			}
//		}
		
		args[0] = gemHomeString + "/bin/cuke4duke";
		
        if (log.isDebugEnabled()) {
            log.debug("workingdir='"+new File(".").getAbsolutePath());
            log.debug("classpath=" + this.getClasspath());
            log.debug("jruby.gem.home=" + System.getProperty("jruby.gem.home") );
            log.debug("jruby.gem.path=" + System.getProperty("jruby.gem.path") );
            for(String arg:args) {
                log.debug("Args:" + arg);
            }
        }

		final ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		final PrintStream out = new PrintStream(os);
		final PrintStream err = new PrintStream(os);

		RubyInstanceConfig config = new RubyInstanceConfig(){{
            setInput(in);
            setOutput(out);
            setError(err);
        }};

//		String scriptName = args[0].substring("classpath:".length());
//		final ClassLoader classLoader = Ruby.getClassLoader();
//		boolean exists = (classLoader.getResource(scriptName) != null);
//		String scriptName2 = args[0].substring("classpath:/".length());
//		boolean exists2 = (classLoader.getResource(scriptName2) != null);
//		if (exists2) {
//			args[0] = "classpath:" + scriptName2;
//		}
		
		Main main = new Main(config);
		Main.Status status = main.run(args);
		if (status.getStatus() != 0) {
			throw new RuntimeException(os.toString());
		}
//		config.processArguments(args);
//
//		internalRun(config);
	}

	private void internalRun(RubyInstanceConfig config) throws Exception {
//		ClassLoader loader = this.getClass().getClassLoader();
//		String script = config.getScriptFileName();
//		ClassPathResource cpr = new ClassPathResource("/META-INF/jruby.gem.home/bin/cuke4duke");
//		boolean exists = cpr.exists();
//
//		final String scriptResourcePath = script.substring("classpath:".length());

//		config.setLoader(Thread.currentThread().getContextClassLoader());
//		InputStream in = loader.getResourceAsStream(scriptResourcePath);

//		config.setLoader(Thread.currentThread().getContextClassLoader());
		
		InputStream in;
//		String scriptFileName = config.getScriptFileName();
//		if (scriptFileName.startsWith("classpath:")) {
//			scriptFileName = scriptFileName.substring("classpath:".length());
//			ClassPathResource cpr = new ClassPathResource(scriptFileName);
//			boolean exists = cpr.exists(); // this.getClass().getClassLoader().getResource(scriptFileName) != null;
//
//			in = cpr.getInputStream();
////			ClassPathResource cpr = new ClassPathResource(scriptFileName);
////			boolean exists = cpr.exists();
////			in = cpr.getInputStream();
//		} else {
//			in   = config.getScriptSource();
//		}
		in   = config.getScriptSource();

	    String filename  = config.displayedFileName();

//	    doProcessArguments(in);
	    Ruby runtime     = Ruby.newInstance(config);

	    try {
		    Thread.currentThread().setContextClassLoader(runtime.getJRubyClassLoader());
		    runtime.runFromMain(in, filename);
//
//	        if (in == null) {
//	            // no script to run, return success
//	            return;
//	        } else if (config.isxFlag() && !config.hasShebangLine()) {
//	            // no shebang was found and x option is set
//	            throw new MainExitException(1, "jruby: no Ruby script found in input (LoadError)");
//	        } else if (config.isShouldCheckSyntax()) {
//	            // check syntax only and exit
//	            return doCheckSyntax(runtime, in, filename);
//	        } else {
//	            // proceed to run the script
//	            return doRunFromMain(runtime, in, filename);
//	        }
	    } finally {
	        runtime.tearDown();
	    }
	}
	
	@Override
    protected File getJrubyHome() {
        String jruby_home = envVars.get("jruby.gem.home");
        return new File(jruby_home);
    }
}
