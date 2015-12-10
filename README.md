# PPPlugin <img src="https://travis-ci.org/RandomCodeOrg/PPPlugin.svg" />

A maven plugin that executes one or multiple user defined processors that can be used to transform or modify the compilation result.

## Getting started

###1. Executing the plugin
To run the PPPlugin insert the following snippet into the &lt;build&gt;&lt;plugins&gt;-section of your pom.xml:

    <plugin>
      <groupId>com.github.randomcodeorg.ppplugin</groupId>
      <artifactId>ppplugin</artifactId>
      <version>0.1.0</version>
      <executions>
        <execution>
        <phase>process-classes</phase>
          <goals>
            <goal>postprocess</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    
To define a custom processor you also have to include 

    <dependency>
        <groupId>com.github.randomcodeorg.ppplugin</groupId>
        <artifactId>ppplugin</artifactId>
        <version>0.1.0</version>
    </dependency>
    
in your pom's &lt;dependencies&gt;-section.
###2. Creating a processor
The plugin will search the compiled classes for implementations of the PProcessor interface. Every implementation (you want to
be executed) must be non-abstract and provide a public default constructor in order to be instantiated.

You may take a look at the <a href="https://github.com/RandomCodeOrg/PPDefaults">PPDefaults</a> project to see some default processor implementations.
