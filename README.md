rss-filter
==========
A simple application for filtering any RSS feed.

Build
-----
This application uses Play framework build system - [Typesafe Activator](http://typesafe.com/activator). Go and get it
if you still haven't. After that, simply run

    $ activator run
    
for running the application.

For production build use `activator dist` command.

Deployment
----------
Application meant to run through [YAJSW](http://yajsw.sourceforge.net/). Follow the instructions found on the site.
rss-filter was tested with YAJSW 11.11.

Configuration
-------------
Open the `conf/application.conf` file and look for comments of `rss` section.

Usage
-----
Start the application and direct your browser to URL `<application>/feed/<feed name>`. It will fetch the corresponding
feed and filter it with regex.