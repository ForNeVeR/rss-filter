rss-filter
==========

A simple application to filter an external RSS feed and return the results
through HTTP (as another RSS feed).

Configuration
-------------

The configuration file `appsettings.json` is automatically read from the
working directory at the application start. RssFilter has one configuration
section, `"rss"`, consisting of the items in the following form:

```json
{
    "name": "some_feed_name_for_using_in_url",
    "url": "https://feed-url",
    "titlefilter": "^a regular expression that feed title should match$"
}
```

Build
-----

To build the project, first install [.NET Core SDK][net-core]. After that

```console
$ dotnet build
```

Deployment
----------

To prepare the application for the production environment, run

```console
$ dotnet publish -c Release -o out
```

This will create an `out` directory that should be deployed to the production
site.

This application supports Docker. To deploy it, tune `appsettings.json` and
then run the script:

```console
$ pwsh docker-compose.ps1
```

Consult `docker-compose.ps1` parameters for additional options.

Usage
-----

Start the application and direct your browser to URL
`<application>/feed/<feed name>`. It will fetch the corresponding feed and
filter it with a regular expression.

[net-core]: https://www.microsoft.com/net/download
