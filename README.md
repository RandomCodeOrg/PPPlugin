# PPPlugin <img src="https://travis-ci.org/RandomCodeOrg/PPPlugin.svg" />

A maven plugin that executes one or multiple user defined processors that can be used to transform or modify the compilation result.

## Getting started

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

