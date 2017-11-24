rss-filter
==========

A simple application to filter any RSS feed.

Configuration
-------------

Open the `conf/application.conf` file and look for comments of `rss` section.


Build
-----

To build the project, first install [sbt][]. After that

```console
$ sbt run
```

Deployment
----------

To prepare the application for the production environment, run

```console
$ sbt dist
``` 

This will create `target/universal/rss-filter-<version>.zip` artifact that
should be deployed to the production site.

Usage
-----

Start the application and direct your browser to URL
`<application>/feed/<feed name>`. It will fetch the corresponding feed and
filter it with regex.

[sbt]: http://www.scala-sbt.org/
