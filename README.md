This is a util for replacing some of the content in the source files and then output the content replace to other directory.

We hope to use the following command to achieve this goal.
```shell script
java -jar "<source dir or file>" "<target dir or file>" \
--conf-dir "the path of the file configurate the default value of the command line option value, such as --text-util-file-dir ..." \
--text-util-file-dir "<the file for configurating the replacement behavior>" \
--processpipe-class "<the name of the class which is for controlling how we read and write the parameter files>" \
--text-ulit-class "<the name of class which actually apply some change on the content of the source file>" \

```