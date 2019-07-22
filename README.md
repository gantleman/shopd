# shopd

This is a distributed system of online shopping mall, which supports all
functions from single machine to cluster. Can help you quickly build a
distributed online mall. It is also an example engineering for learning
the concepts of distributed system, lock-free programming and
micro-service splitting.

Development environment:Ubuntu 16.04.4, vscode 1.25.1, mysql Ver 14.14
Distrib 5.7.26, redis 3.0.6, maven 3.6.0, java 1.8.0\_201, git 2.7.4

After installing the above environment, execute in the installation
directory

**\>mvn install**

Then import shop. SQL into MySQL database

**\>mysql -h localhost -u root -p test \< /shop.sql
\--default-character-set=utf8**

If you use the VSCode editor, you need to add the launch.json file

{

// Use IntelliSense to learn about possible attributes.

// Hover to view descriptions of existing attributes.

// For more information, visit:
https://go.microsoft.com/fwlink/?linkid=830387

\"version\": \"0.2.0\",

\"configurations\": \[

{

\"type\": \"java\",

\"name\": \"Debug (Launch) - Current File\",

\"request\": \"launch\",

\"mainClass\": \"\${file}\"

},

{

\"type\": \"java\",

\"name\": \"Debug (Launch)-DemoApplication\<shopd\>\",

\"request\": \"launch\",

\"mainClass\": \"com.github.gantleman.shopd.DemoApplication\",

\"projectName\": \"shopd\"

}

\]

}
