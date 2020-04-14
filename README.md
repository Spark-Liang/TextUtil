##### Abstract:
This is a util for replacing some of the content in the source files and then output the content replace to other directory.

##### Usage:
Following is the help text of this text util. You can run this util by "java -jar" command.
```shell script
usage: text-util [options] source target
    --conf <The configuration>                         The configuration provided via command line which is usually for override the value config in
                                                       the file.
                                                       The format of each argument is <key>=<value>.
    --default-conf-dir <The configuration file path>   The file path of the configuration file.
 -h,--help                                             usage help
 -p,--pipe <Class name of transform pipe>              The class to control which file that need to be transformed and how to place the file in
                                                       target folder.
                                                       The default value is org.sparkliang.textutil.impl.DefaultTransformPipe.
 -t,--transformer <Class name of text transformer>     The class to control how the program transform the content in each file.
                                                       The default value is org.sparkliang.textutil.impl.DefaultXMLParameterFileTransformer.
```


##### Examples:
**For all the examples as below, we assume that jar file after package is "text-util.jar".**

###### Generate parameter files based on the configuration file
We can create a configuration file for some of our execution environment and config the environment information in the file.
```text
transformer.parameter.SQLDB_Connection=Connection1
transformer.parameter.SQLDB_GOESLSDS_Owner=GOESLSDS
``` 
Then you can run the following command to transform all the file in source path and place the files into the given target path.
```shell script
java -jar text-util.jar \ 
    --default-conf-dir <the path of your confirguration file> \
    <source path> <target path>
```  

##### Dynamically config the parameter in command line
For some cases, you might need to overwrite the value in the configuration file or dynamically set the value for some parameters. 
In these cases, you can config the parameter by providing configuration via option "--conf" with format "\<key\>=\<value\>".
For example, you can dynamically set the parameter CYCLE_DT by "--conf transformer.parameter.CYCLE_DT=\<your cycle date\>".
You can provide multiple configuration via "--conf" at the same time.