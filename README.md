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

This application supports Docker. To deploy it, perform the following:

```console
$ sbt dist
```

Then unpack `target/universal/rss-filter-<version>.zip` into
`docker/rss-filter` directory, and edit the
`docker/rss-filter/conf/application.conf` file if necessary. After that:

```console
$ pwsh docker/compose.ps1
``` 

Consult `docker/compose.ps1` parameters for additional options.

Usage
-----

Start the application and direct your browser to URL
`<application>/feed/<feed name>`. It will fetch the corresponding feed and
filter it with regex.

[sbt]: http://www.scala-sbt.org/
